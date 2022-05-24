package com.jiawa.wiki.service;

import com.jiawa.wiki.domain.Doc;
import com.jiawa.wiki.req.DocQueryReq;
import com.jiawa.wiki.req.DocSaveReq;
import com.jiawa.wiki.resp.DocQueryResp;
import com.jiawa.wiki.resp.PageResp;

import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:34 PM
 * @Describe:
 */
public interface DocService {

    List<DocQueryResp> all();
    PageResp<Doc> list(DocQueryReq req);

    void save(DocSaveReq req);

    void delete(Long id);
}
