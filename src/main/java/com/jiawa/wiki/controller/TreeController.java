package com.jiawa.wiki.controller;

import com.jiawa.wiki.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 古树名木信息管理控制器
 * 支持按树种名称、树龄、位置等字段进行条件查询和筛选
 *
 * TODO: 当前返回模拟数据用于前端界面展示，后续需对接实际数据库完成持久化查询
 */
@RestController
@RequestMapping("tree")
public class TreeController {

    private static final Logger LOG = LoggerFactory.getLogger(TreeController.class);

    /**
     * 按树种名称条件检索古树名木信息
     * 支持模糊匹配，可组合树龄范围进行筛选
     *
     * @param name    树种名称（支持模糊查询，如"银杏"、"柏"）
     * @param ageMin  最小树龄（可选参数）
     * @param ageMax  最大树龄（可选参数）
     * @return 符合条件的古树名木列表，包含树种名称、树龄、位置坐标等字段
     */
    @GetMapping("/searchByName")
    public CommonResp<List<Map<String, Object>>> searchByName(
            @RequestParam String name,
            @RequestParam(required = false) Integer ageMin,
            @RequestParam(required = false) Integer ageMax) {

        LOG.info("按树种名称检索，参数：name={}, ageMin={}, ageMax={}", name, ageMin, ageMax);

        // TODO: 后续替换为真实数据库查询，当前使用模拟数据支撑前端界面展示
        List<Map<String, Object>> mockResults = generateMockResults(name);

        CommonResp<List<Map<String, Object>>> resp = new CommonResp<>();
        resp.setContent(mockResults);
        return resp;
    }

    /**
     * 获取古树名木详细信息
     * TODO: 后续需对接数据库实现详情查询
     *
     * @param id 古树记录唯一标识
     * @return 单条古树名木完整信息
     */
    @GetMapping("/detail/{id}")
    public CommonResp<Map<String, Object>> detail(@PathVariable Long id) {
        LOG.info("查询古树详情，id={}", id);
        CommonResp<Map<String, Object>> resp = new CommonResp<>();
        // TODO: 后续实现数据库查询逻辑
        return resp;
    }

    /**
     * 生成模拟检索结果
     * TODO: 待数据库对接后删除此方法
     */
    private List<Map<String, Object>> generateMockResults(String keyword) {
        List<Map<String, Object>> results = new ArrayList<>();

        Map<String, Object> tree1 = new HashMap<>();
        tree1.put("id", 1);
        tree1.put("name", "景山万春亭古柏");
        tree1.put("species", "侧柏");
        tree1.put("age", 500);
        tree1.put("location", "北京市景山公园万春亭北侧");
        tree1.put("latitude", 39.9289);
        tree1.put("longitude", 116.3974);
        tree1.put("protectionLevel", "一级");
        results.add(tree1);

        Map<String, Object> tree2 = new HashMap<>();
        tree2.put("id", 2);
        tree2.put("name", "天坛九龙柏");
        tree2.put("species", "侧柏");
        tree2.put("age", 600);
        tree2.put("location", "北京市天坛公园回音壁西北侧");
        tree2.put("latitude", 39.8833);
        tree2.put("longitude", 116.4069);
        tree2.put("protectionLevel", "一级");
        results.add(tree2);

        Map<String, Object> tree3 = new HashMap<>();
        tree3.put("id", 3);
        tree3.put("name", "潭柘寺帝王银杏");
        tree3.put("species", "银杏");
        tree3.put("age", 1300);
        tree3.put("location", "北京市门头沟区潭柘寺寺院内");
        tree3.put("latitude", 39.9050);
        tree3.put("longitude", 116.0280);
        tree3.put("protectionLevel", "特级");
        results.add(tree3);

        Map<String, Object> tree4 = new HashMap<>();
        tree4.put("id", 4);
        tree4.put("name", "大觉寺千年银杏");
        tree4.put("species", "银杏");
        tree4.put("age", 950);
        tree4.put("location", "北京市海淀区大觉寺寺院内");
        tree4.put("latitude", 40.0510);
        tree4.put("longitude", 116.0950);
        tree4.put("protectionLevel", "一级");
        results.add(tree4);

        Map<String, Object> tree5 = new HashMap<>();
        tree5.put("id", 5);
        tree5.put("name", "北海团城古白皮松");
        tree5.put("species", "白皮松");
        tree5.put("age", 800);
        tree5.put("location", "北京市北海公园团城");
        tree5.put("latitude", 39.9250);
        tree5.put("longitude", 116.3900);
        tree5.put("protectionLevel", "一级");
        results.add(tree5);

        // 根据关键词简单过滤，若关键词为空则返回全部
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> tree : results) {
            String species = (String) tree.get("species");
            String treeName = (String) tree.get("name");
            if ((species != null && species.contains(keyword)) ||
                (treeName != null && treeName.contains(keyword))) {
                filtered.add(tree);
            }
        }
        return filtered;
    }
}
