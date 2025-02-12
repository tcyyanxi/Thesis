package com.software.androidthesis.entity;

import java.util.Date;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/8 23:06
 * @Decription:
 */
public class UserEdit {
    private Long id;  // 外键与 users 表的 id 字段关联
    private String avatar;  // 用户头像 URL
    private String username;  // 用户名
    private String gender;  // 性别
    private Date birthDate;  // 生日

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setUpdatedAt(Date birthDate) {
        this.birthDate = birthDate;
    }
}