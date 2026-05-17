package com.jiawa.wiki.resp;

/**
 * @Author: SongShengLin
 * @Date: 2022/05/27 3:12 PM
 * @Describe:
 */
public class UserLoginResp {

    private Long id;

    private String loginName;

    private String name;

    private Long perRoleId;

    private String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPerRoleId() {
        return perRoleId;
    }

    public void setPerRoleId(Long perRoleId) {
        this.perRoleId = perRoleId;
    }

    @Override
    public String toString() {
        return "UserLoginResp{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", name='" + name + '\'' +
                ", perRoleId=" + perRoleId +
                ", token='" + token + '\'' +
                '}';
    }

}
