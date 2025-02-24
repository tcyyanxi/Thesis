package com.software.androidthesis.entity;

import java.util.Date;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/21 17:53
 * @Decription:
 */
public class UserWords {
    private Long id;
    private String word;
    private int count;
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
