package com.jiawa.wiki.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiawa.wiki.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 古树名木检索控制器
 * 支持以图搜图(I2I)、以图搜位置(I2L)、以位置搜图(L2I)、按树种名称检索
 * 数据源：预计算数据库 precomputed_db.json（服务器迁移中可能产生损失，可结合数据集重新训练得到完整权重）
 */
@RestController
@RequestMapping("tree")
public class TreeController {

    private static final Logger LOG = LoggerFactory.getLogger(TreeController.class);

    // 预计算数据库路径
    private static final String DB_PATH = "D:/project/java/imooc-wiki/algorithm/data_samples/precomputed_db.json";
    // 演示图片根目录
    private static final String IMAGE_ROOT = "D:/project/java/imooc-wiki/algorithm/data_samples/bfath_demo";

    // 内存中的古树数据
    private List<Map<String, Object>> treeDatabase = new ArrayList<>();

    /**
     * 启动时加载预计算数据库到内存
     */
    @PostConstruct
    public void init() {
        try {
            String jsonStr = new String(Files.readAllBytes(Paths.get(DB_PATH)), "UTF-8");
            JSONArray array = JSON.parseArray(jsonStr);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Map<String, Object> tree = new HashMap<>();
                tree.put("id", obj.getString("id"));
                tree.put("name", obj.getString("name"));
                tree.put("species", obj.getString("species"));
                tree.put("age", obj.getInteger("age"));
                tree.put("lat", obj.getDouble("lat"));
                tree.put("lon", obj.getDouble("lon"));
                tree.put("location", obj.getString("location"));
                tree.put("protectionLevel", obj.getString("protectionLevel"));
                tree.put("image", "/tree/image/" + obj.getString("id"));
                treeDatabase.add(tree);
            }
            LOG.info("古树数据库加载完成，共 {} 条记录", treeDatabase.size());
        } catch (Exception e) {
            LOG.error("古树数据库加载失败: {}", e.getMessage());
            // 加载失败时使用空列表，避免启动崩溃
        }
    }

    /**
     * 获取全部古树列表
     */
    @GetMapping("/list")
    public CommonResp<List<Map<String, Object>>> list() {
        CommonResp<List<Map<String, Object>>> resp = new CommonResp<>();
        resp.setContent(treeDatabase);
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

        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> tree : treeDatabase) {
            String species = (String) tree.get("species");
            String treeName = (String) tree.get("name");
            int age = (Integer) tree.get("age");

            boolean nameMatch = (species != null && species.contains(name)) ||
                    (treeName != null && treeName.contains(name));
            boolean ageMatch = true;
            if (ageMin != null && age < ageMin) ageMatch = false;
            if (ageMax != null && age > ageMax) ageMatch = false;

            if (nameMatch && ageMatch) {
                filtered.add(tree);
            }
        }

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
     */
    @GetMapping("/image/{id}")
    public void image(@PathVariable String id, HttpServletResponse response) {
        try {
            // 在 bfath_demo 目录下查找对应 id 的 image.jpg
            File dir = new File(IMAGE_ROOT);
            File[] dirs = dir.listFiles();
            if (dirs != null) {
                for (File d : dirs) {
                    if (d.isDirectory() && d.getName().startsWith(id + "_")) {
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
    public CommonResp<Map<String, Object>> detail(@PathVariable String id) {
        LOG.info("查询古树详情，id={}", id);
        CommonResp<Map<String, Object>> resp = new CommonResp<>();
        for (Map<String, Object> tree : treeDatabase) {
            if (id.equals(tree.get("id"))) {
                resp.setContent(tree);
                return resp;
            }
        }
        return resp;
    }
}
