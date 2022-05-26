package com.jiawa.wiki.controller;

import com.jiawa.wiki.req.DocSaveReq;
import com.jiawa.wiki.resp.DocQueryResp;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:38 PM
 * @Describe:
 */
@RestController
@RequestMapping("doc")
public class DocController {
    @Autowired
    private DocService docService;

    @GetMapping("/all/{ebookId}")
    public CommonResp<List<DocQueryResp>> all(@PathVariable Long ebookId) {
        List<DocQueryResp> all = docService.all(ebookId);

        CommonResp<List<DocQueryResp>> res = new CommonResp<>();

        res.setContent(all);

        return res;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req) {
        docService.save(req);

        return new CommonResp<>();
    }

    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr) {
        CommonResp<String> res = new CommonResp<>();
        if (ObjectUtils.isEmpty(idsStr)) {
            res.setSuccess(Boolean.FALSE);
            res.setMessage("删除文档Id为空");
            return res;
        }
        List<String> ids = Arrays.asList(idsStr.split(","));
        docService.delete(ids.stream().map(Long::parseLong).collect(Collectors.toList()));

        return res;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp<String> findContent(@PathVariable Long id) {
        CommonResp<String> res = new CommonResp<>();

        String content = docService.findContent(id);

        res.setContent(content);

        return res;
    }


}
