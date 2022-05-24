package com.jiawa.wiki.controller;

import com.jiawa.wiki.req.DocSaveReq;
import com.jiawa.wiki.resp.DocQueryResp;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/all")
    public CommonResp<List<DocQueryResp>> all() {
        List<DocQueryResp> all = docService.all();

        CommonResp<List<DocQueryResp>> res = new CommonResp<>();

        res.setContent(all);

        return res;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req) {
        docService.save(req);

        return new CommonResp<>();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        docService.delete(id);
        return new CommonResp<>();
    }


}
