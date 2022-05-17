package com.jiawa.wiki.service;

import com.jiawa.wiki.req.EbookReq;
import com.jiawa.wiki.resp.EbookResp;

import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:34 PM
 * @Describe:
 */
public interface EbookService {

    List<EbookResp> list(EbookReq req);
}
