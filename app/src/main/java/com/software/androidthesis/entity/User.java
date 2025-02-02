package com.software.androidthesis.entity;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 21:13
 * @Decription:
 */
public class User {
    private String userId;
    private String username;;
    private String email;
    private String code;
    private String avatar;
    private String birthdate;
    // 英文水平
    private String level;

    private String sex;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public User() {
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", avatar='" + avatar + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", level='" + level + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }

    public User(String userId, String username, String email, String code, String avatar, String birthdate, String level) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.code = code;
        this.avatar = avatar;
        this.birthdate = birthdate;
        this.level = level;
    }
}

