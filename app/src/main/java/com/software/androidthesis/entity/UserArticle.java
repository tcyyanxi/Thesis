package com.software.androidthesis.entity;

import java.sql.Timestamp;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/4/1 23:30
 * @Decription:
 */
public class UserArticle {
    private Long id;           // 用户ID
    private Integer articleId; // 文章ID
    private Integer count;     // 访问次数
    private String time;    // 记录时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
