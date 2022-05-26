package com.jiawa.wiki.service;


import com.jiawa.wiki.req.UserQueryReq;
import com.jiawa.wiki.req.UserSaveReq;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.resp.UserQueryResp;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:34 PM
 * @Describe:
 */
public interface UserService {

    PageResp<UserQueryResp> list(UserQueryReq req);

    void save(UserSaveReq req);

    void delete(Long id);
}
