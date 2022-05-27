package com.jiawa.wiki.service;


import com.jiawa.wiki.domain.User;
import com.jiawa.wiki.req.UserLoginReq;
import com.jiawa.wiki.req.UserQueryReq;
import com.jiawa.wiki.req.UserRestPasswordReq;
import com.jiawa.wiki.req.UserSaveReq;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.resp.UserLoginResp;
import com.jiawa.wiki.resp.UserQueryResp;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/17 10:34 PM
 * @Describe:
 */
public interface UserService {

    PageResp<UserQueryResp> list(UserQueryReq req);

    void save(UserSaveReq req);

    void resetPassword(UserRestPasswordReq req);

    void delete(Long id);

    User selectByLoginName(String logName);

    UserLoginResp login(UserLoginReq req);
}
