package com.jiawa.wiki.service;

import com.jiawa.wiki.domain.Ebook;
import com.jiawa.wiki.req.EbookReq;
import com.jiawa.wiki.resp.PageResp;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:34 PM
 * @Describe:
 */
public interface EbookService {

    PageResp<Ebook> list(EbookReq req);
}
