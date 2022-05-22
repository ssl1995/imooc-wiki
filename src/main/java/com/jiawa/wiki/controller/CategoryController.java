package com.jiawa.wiki.controller;

import com.jiawa.wiki.req.CategorySaveReq;
import com.jiawa.wiki.resp.CategoryQueryResp;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.service.CategoryService;
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
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public CommonResp<List<CategoryQueryResp>> all() {
        List<CategoryQueryResp> all = categoryService.all();

        CommonResp<List<CategoryQueryResp>> res = new CommonResp<>();

        res.setContent(all);

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
