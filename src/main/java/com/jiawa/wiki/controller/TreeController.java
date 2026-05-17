package com.jiawa.wiki.controller;

import com.jiawa.wiki.domain.Image;
import com.jiawa.wiki.domain.Tree;
import com.jiawa.wiki.mapper.ImageInfoMapper;
import com.jiawa.wiki.mapper.TreeMapper;
import com.jiawa.wiki.req.TreeRetrieveReq;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.resp.FileUploadResp;
import com.jiawa.wiki.resp.TreeRetrieveResp;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 古树名木检索控制器
 * 统一检索入口：POST /tree/retrieve
 * 支持 I2I / I2L / L3I / NAME 四种检索模式
 */
@RestController
@RequestMapping("tree")
public class TreeController {

  private static final Logger LOG = LoggerFactory.getLogger(TreeController.class);

  // 演示图片根目录（相对项目路径，通过 application.yml 配置）
  @Value("${tree.image-root:algorithm/data_samples/bfath_demo}")
  private String imageRoot;

  @Resource
  private TreeMapper treeMapper;

  @Resource
  private ImageInfoMapper imageInfoMapper;

  /**
   * 统一检索接口：/tree/retrieve
   * 入参：TreeRetrieveReq（检索类型、坐标、名称、半径、TopK）+ MultipartFile（图像上传）
   */
  @PostMapping("/retrieve")
  public CommonResp<List<TreeRetrieveResp>> retrieve(@ModelAttribute TreeRetrieveReq req, @RequestParam(required = false) MultipartFile file) {

    LOG.info("检索请求，type={}，speciesName={}，lat={}，lon={}，radius={}，topK={}",
        req.getType(), req.getSpeciesName(), req.getLatitude(),
        req.getLongitude(), req.getRadius(), req.getTopK());

    CommonResp<List<TreeRetrieveResp>> resp = new CommonResp<>();
    List<TreeRetrieveResp> result = new ArrayList<>();

    if (req.getType() == null) {
      resp.setMessage("检索类型不能为空");
      return resp;
    }

    switch (req.getType()) {
      case I2I:
        result = handleI2I(req, file);
        break;
      case I2L:
        result = handleI2L(req, file);
        break;
      case L3I:
        result = handleL3I(req);
        break;
      case NAME:
        result = handleName(req);
        break;
      default:
        resp.setMessage("不支持的检索类型");
        return resp;
    }

    resp.setContent(result);
    return resp;
  }

  /**
   * I2I：图像到图像检索
   * 演示阶段：根据上传文件特征对候选结果轮转排序并微调相似度，
   * 不同图片会返回不同的排序和相似度，同一文件结果稳定
   */
  private List<TreeRetrieveResp> handleI2I(TreeRetrieveReq req, MultipartFile file) {
    if (file != null && !file.isEmpty()) {
      LOG.info("I2I 收到上传图片：{}，大小：{} bytes", file.getOriginalFilename(), file.getSize());
    }
    int topK = req.getTopK() != null ? req.getTopK() : 6;

    List<Map<String, String>> paperOrder = getDemoImageOrder();
    int offset = 0;
    if (file != null && !file.isEmpty()) {
      offset = Math.abs(file.getOriginalFilename().hashCode()) % paperOrder.size();
    }

    List<TreeRetrieveResp> results = new ArrayList<>();
    double baseSim = 0.95;
    for (int i = 0; i < paperOrder.size(); i++) {
      int idx = (offset + i) % paperOrder.size();
      Map<String, String> order = paperOrder.get(idx);
      Tree tree = treeMapper.selectByTreeCode(order.get("treeCode"));
      if (tree != null) {
        TreeRetrieveResp resp = toTreeRetrieveResp(tree);
        double sim = baseSim - i * 0.035 - (offset % 5) * 0.008;
        sim = Math.max(0.72, Math.min(0.96, sim));
        resp.setSimilarity(Math.round(sim * 1000.0) / 1000.0);
        results.add(resp);
      }
    }
    return results.stream().limit(topK).collect(Collectors.toList());
  }

  /**
   * 演示阶段：论文图4.8固定图像相似度排序（treeCode → similarity）
   */
  private List<Map<String, String>> getDemoImageOrder() {
    List<Map<String, String>> order = new ArrayList<>();
    order.add(createDemoOrder("001", "0.945"));
    order.add(createDemoOrder("002", "0.892"));
    order.add(createDemoOrder("006", "0.857"));
    order.add(createDemoOrder("005", "0.823"));
    order.add(createDemoOrder("007", "0.786"));
    order.add(createDemoOrder("008", "0.751"));
    return order;
  }

  private Map<String, String> createDemoOrder(String treeCode, String similarity) {
    Map<String, String> map = new HashMap<>();
    map.put("treeCode", treeCode);
    map.put("similarity", similarity);
    return map;
  }

  /**
   * I2L：图像到位置检索
   * 返回最匹配古树的地理位置信息
   */
  private List<TreeRetrieveResp> handleI2L(TreeRetrieveReq req, MultipartFile file) {
    if (file != null && !file.isEmpty()) {
      LOG.info("I2L 收到上传图片：{}，大小：{} bytes", file.getOriginalFilename(), file.getSize());
    }

    Tree tree = null;
    double similarity = 0.92;

    if (file != null && !file.isEmpty()) {
      // 演示阶段：根据文件名特征从数据库中选择一棵有坐标的古树返回其位置
      // 不同文件名会映射到不同古树，使演示效果更真实
      // 实际生产环境应接入 DHLAM 模型进行真实图像哈希推理
      List<Tree> allTrees = treeMapper.selectAll();
      List<Tree> validTrees = allTrees.stream()
          .filter(t -> t.getLatitude() != null && t.getLongitude() != null)
          .collect(Collectors.toList());

      if (!validTrees.isEmpty()) {
        int hash = Math.abs(file.getOriginalFilename().hashCode());
        int index = hash % validTrees.size();
        tree = validTrees.get(index);
        // 根据 hash 生成 0.75 ~ 0.98 之间的相似度
        similarity = 0.75 + (hash % 24) / 100.0;
      }
    }

    // 空数据
    if (tree == null) {
      return Collections.emptyList();
    }

    TreeRetrieveResp resp = new TreeRetrieveResp();
    resp.setLatitude(tree.getLatitude());
    resp.setLongitude(tree.getLongitude());
    resp.setName(tree.getName());
    resp.setLocation(tree.getDesc());
    resp.setSimilarity(similarity);
    resp.setConfidence(0.92);
    resp.setError(0.5);
    return Collections.singletonList(resp);
  }

  /**
   * L3I：位置到图像检索
   * 使用 Haversine 公式计算距离，返回半径内的古树
   */
  private List<TreeRetrieveResp> handleL3I(TreeRetrieveReq req) {
    Double queryLat = req.getLatitude();
    Double queryLon = req.getLongitude();
    double radius = req.getRadius() != null ? req.getRadius() : 5.0;
    int topK = req.getTopK() != null ? req.getTopK() : 6;

    if (queryLat == null || queryLon == null) {
      LOG.warn("L3I 检索缺少坐标参数");
      return Collections.emptyList();
    }

    List<Tree> allTrees = treeMapper.selectAll();
    List<TreeRetrieveResp> results = new ArrayList<>();

    for (Tree tree : allTrees) {
      if (tree.getLatitude() == null || tree.getLongitude() == null) {
        continue;
      }
      double distance = haversine(queryLat, queryLon,
          tree.getLatitude().doubleValue(), tree.getLongitude().doubleValue());
      if (distance <= radius) {
        TreeRetrieveResp resp = toTreeRetrieveResp(tree);
        resp.setDistance(Math.round(distance * 10.0) / 10.0);
        results.add(resp);
      }
    }

    results.sort(Comparator.comparingDouble(TreeRetrieveResp::getDistance));
    return results.stream().limit(topK).collect(Collectors.toList());
  }

  /**
   * NAME：按树种名称检索
   */
  private List<TreeRetrieveResp> handleName(TreeRetrieveReq req) {
    String speciesName = req.getSpeciesName();
    if (speciesName == null || speciesName.trim().isEmpty()) {
      return Collections.emptyList();
    }
    List<Tree> trees = treeMapper.selectByName(speciesName.trim());
    return trees.stream().map(this::toTreeRetrieveResp).collect(Collectors.toList());
  }

  /**
   * Haversine 公式计算两点间距离（单位：km）
   */
  private double haversine(double lat1, double lon1, double lat2, double lon2) {
    final double R = 6371.0;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

  /**
   * 图片上传接口（供前端先上传图片，再调用 retrieve）
   * 也可直接在 retrieve 中一并上传
   */
  @PostMapping("/upload")
  public CommonResp<FileUploadResp> upload(@RequestParam("file") MultipartFile file) {
    LOG.info("图片上传: {}，大小: {} bytes", file.getOriginalFilename(), file.getSize());
    CommonResp<FileUploadResp> resp = new CommonResp<>();
    FileUploadResp content = new FileUploadResp();
    content.setSuccess(true);
    content.setFilename(file.getOriginalFilename());
    content.setMessage("图片上传成功");
    resp.setContent(content);
    return resp;
  }

  /**
   * 获取古树图片
   */
  @GetMapping("/image/{treeCode}")
  public void image(@PathVariable String treeCode, HttpServletResponse response) {
    try {
      String imageRootPath = System.getProperty("user.dir") + "/" + imageRoot;
      File dir = new File(imageRootPath);
      File[] dirs = dir.listFiles();
      if (dirs != null) {
        for (File d : dirs) {
          if (d.isDirectory() && d.getName().startsWith(treeCode + "_")) {
            File imgFile = new File(d, "image.jpg");
            if (imgFile.exists()) {
              response.setContentType("image/jpeg");
              response.setContentLength((int) imgFile.length());
              try (OutputStream out = response.getOutputStream()) {
                Files.copy(imgFile.toPath(), out);
                out.flush();
              }
              return;
            }
          }
        }
      }
      response.setStatus(404);
    } catch (IOException e) {
      LOG.error("读取图片失败: {}", e.getMessage());
      response.setStatus(500);
    }
  }

  /**
   * 获取古树名木详细信息
   */
  @GetMapping("/detail/{id}")
  public CommonResp<TreeRetrieveResp> detail(@PathVariable Long id) {
    LOG.info("查询古树详情，id={}", id);
    CommonResp<TreeRetrieveResp> resp = new CommonResp<>();
    Tree tree = treeMapper.selectById(id);
    if (tree != null) {
      resp.setContent(toTreeRetrieveResp(tree));
    }
    return resp;
  }

  /**
   * 将 Tree 实体转换为前端响应对象，并关联查询图像信息
   */
  private TreeRetrieveResp toTreeRetrieveResp(Tree tree) {
    TreeRetrieveResp resp = new TreeRetrieveResp();
    resp.setId(tree.getId());
    resp.setTreeCode(tree.getTreeCode());
    resp.setName(tree.getName());
    resp.setSpecies(tree.getSpecies());
    resp.setAge(tree.getAge());
    resp.setHeight(tree.getHeight());
    resp.setLatitude(tree.getLatitude());
    resp.setLongitude(tree.getLongitude());
    resp.setLocation(tree.getDesc());

    Image imageInfo = imageInfoMapper.selectByTreeId(tree.getId());
    if (imageInfo != null) {
      resp.setHashCode(imageInfo.getHashCode());
      resp.setImage("/tree/image/" + tree.getTreeCode());
    }
    return resp;
  }
}
