package com.software.androidthesis.entity;

import java.util.Date;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 21:13
 * @Decription:
 */
public class User {
    private Long id;
    private String email;
    private Boolean isFirstLogin;
    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getFirstLogin() {
        return isFirstLogin;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstLogin(Boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

