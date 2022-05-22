package com.jiawa.wiki.controller;

import com.jiawa.wiki.domain.Category;
import com.jiawa.wiki.req.CategoryQueryReq;
import com.jiawa.wiki.req.CategorySaveReq;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:38 PM
 * @Describe:
 */
@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public CommonResp<PageResp<Category>> list(@Valid CategoryQueryReq req) {
        PageResp<Category> list = categoryService.list(req);

        CommonResp<PageResp<Category>> res = new CommonResp<>();

        res.setContent(list);

        return res;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req) {
        categoryService.save(req);

        return new CommonResp<>();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        categoryService.delete(id);
        return new CommonResp<>();
    }


}
