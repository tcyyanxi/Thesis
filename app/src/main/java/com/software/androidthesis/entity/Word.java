package com.software.androidthesis.entity;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/13 15:14
 * @Decription:
 */
public class Word {
    private Long wordId;
    private String book;
    private String unit;
    private String word;
    private String pro;
    private String mean;

    private boolean isPermanentSelected;

    private boolean matched; // 新增 matched 字段

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isPermanentSelected() {
        return isPermanentSelected;
    }

    public void setPermanentSelected(boolean permanentSelected) {
        isPermanentSelected = permanentSelected;
    }

    private boolean selected;  // 用于标记该单词是否被选中

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

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
