package com.jiawa.wiki.controller;

import com.jiawa.wiki.domain.Ebook;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:38 PM
 * @Describe:
 */
@RestController
@RequestMapping("ebook")
public class EbookController {
    @Autowired
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp<List<Ebook>> getAll() {
        List<Ebook> list = ebookService.list();

        CommonResp<List<Ebook>> res = new CommonResp<>();

        res.setContent(list);

        return res;
    }

}
