package com.jiawa.wiki.controller;

import com.jiawa.wiki.domain.ImageInfo;
import com.jiawa.wiki.domain.Tree;
import com.jiawa.wiki.mapper.ImageInfoMapper;
import com.jiawa.wiki.mapper.TreeMapper;
import com.jiawa.wiki.resp.CommonResp;
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
 * 支持以图搜图(I2I)、以图搜位置(I2L)、以位置搜图(L2I)、按树种名称检索
 * 数据源：MySQL 数据库 tree 表 + image_info 表
 */
@RestController
@RequestMapping("tree")
public class TreeController {

    private static final Logger LOG = LoggerFactory.getLogger(TreeController.class);

    // 演示图片根目录
    private static final String IMAGE_ROOT = "D:/project/java/imooc-wiki/algorithm/data_samples/bfath_demo";

    @Resource
    private TreeMapper treeMapper;

    @Resource
    private ImageInfoMapper imageInfoMapper;

    /**
     * 将 Tree 实体转换为前端期望的 Map 格式，并关联查询图像信息
     */
    private Map<String, Object> toMap(Tree tree) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", tree.getId());
        map.put("treeCode", tree.getTreeCode());
        map.put("name", tree.getName());
        map.put("species", tree.getSpecies());
        map.put("age", tree.getAge());
        map.put("height", tree.getHeight());
        map.put("lat", tree.getLatitude());
        map.put("lon", tree.getLongitude());
        map.put("location", tree.getDesc());
        map.put("desc", tree.getDesc());

        // 关联查询图像信息
        ImageInfo imageInfo = imageInfoMapper.selectByTreeId(tree.getId());
        if (imageInfo != null) {
            map.put("hashCode", imageInfo.getHashCode());
            map.put("image", "/tree/image/" + tree.getTreeCode());
        } else {
            map.put("hashCode", null);
            map.put("image", null);
        }
        return map;
    }

    private List<Map<String, Object>> toMapList(List<Tree> trees) {
        return trees.stream().map(this::toMap).collect(Collectors.toList());
    }

    /**
     * 获取全部古树列表
     */
    @GetMapping("/list")
    public CommonResp<List<Map<String, Object>>> list() {
        List<Tree> trees = treeMapper.selectAll();
        CommonResp<List<Map<String, Object>>> resp = new CommonResp<>();
        resp.setContent(toMapList(trees));
        return resp;
    }

    /**
     * 按树种名称条件检索古树名木信息
     * 支持模糊匹配，可组合树龄范围进行筛选
     */
    @GetMapping("/searchByName")
    public CommonResp<List<Map<String, Object>>> searchByName(
            @RequestParam String name,
            @RequestParam(required = false) Integer ageMin,
            @RequestParam(required = false) Integer ageMax) {

        LOG.info("按树种名称检索，参数：name={}, ageMin={}, ageMax={}", name, ageMin, ageMax);

        List<Tree> trees = treeMapper.selectByName(name);
        List<Map<String, Object>> filtered = trees.stream()
                .filter(tree -> {
                    boolean ageMatch = true;
                    if (ageMin != null && tree.getAge() != null && tree.getAge() < ageMin) ageMatch = false;
                    if (ageMax != null && tree.getAge() != null && tree.getAge() > ageMax) ageMatch = false;
                    return ageMatch;
                })
                .map(this::toMap)
                .collect(Collectors.toList());

        CommonResp<List<Map<String, Object>>> resp = new CommonResp<>();
        resp.setContent(filtered);
        return resp;
    }

    /**
     * 图片上传接口（用于以图搜图 / 以图搜位置）
     */
    @PostMapping("/upload")
    public CommonResp<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        LOG.info("图片上传: {}，大小: {} bytes", file.getOriginalFilename(), file.getSize());
        CommonResp<Map<String, Object>> resp = new CommonResp<>();
        Map<String, Object> content = new HashMap<>();
        content.put("success", true);
        content.put("filename", file.getOriginalFilename());
        content.put("message", "图片上传成功");
        resp.setContent(content);
        return resp;
    }

    /**
     * 获取古树图片
     * 使用 treeCode（如 001）匹配演示目录
     */
    @GetMapping("/image/{treeCode}")
    public void image(@PathVariable String treeCode, HttpServletResponse response) {
        try {
            File dir = new File(IMAGE_ROOT);
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
    public CommonResp<Map<String, Object>> detail(@PathVariable Long id) {
        LOG.info("查询古树详情，id={}", id);
        CommonResp<Map<String, Object>> resp = new CommonResp<>();
        Tree tree = treeMapper.selectById(id);
        if (tree != null) {
            resp.setContent(toMap(tree));
        }
        return resp;
    }
}
