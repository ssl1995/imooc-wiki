package com.jiawa.wiki.service;

import com.jiawa.wiki.domain.Category;
import com.jiawa.wiki.req.CategoryQueryReq;
import com.jiawa.wiki.req.CategorySaveReq;
import com.jiawa.wiki.resp.PageResp;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:34 PM
 * @Describe:
 */
public interface CategoryService {

    PageResp<Category> list(CategoryQueryReq req);

    void save(CategorySaveReq req);

    void delete(Long id);
}
