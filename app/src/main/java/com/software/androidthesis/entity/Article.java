package com.software.androidthesis.entity;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/13 21:30
 * @Decription:
 */
public class Article {
    private int articleId;
    private String title;
    private String content;
    private int articlesSum;
    private String category;

    private byte[] img;

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getArticlesSum() {
        return articlesSum;
    }

    public void setArticlesSum(int articlesSum) {
        this.articlesSum = articlesSum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
