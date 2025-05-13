package com.software.androidthesis.entity;

import java.io.Serializable;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/23 22:48
 * @Decription:
 */
public class WordDTO implements Serializable {
    private String word;
    private String pro;
    private String mean;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    // Getters and Setters

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }
}
