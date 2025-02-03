package com.software.androidthesis.entity;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/2 21:29
 * @Decription:分段堆积柱状图View（绘制分段柱状图）
 */
public class BarEntity {
    public String title = "";
    private float positivePer;
    public String negativeColor = "#ffb5a0";
    private float neutralPer;
    public String neutralColor = "#f5e1a7";
    private float negativePer;
    public String positiveColor = "#c5ecce";
    private float Allcount;
    private float scale;
    /*填充区域比例*/
    private float fillScale;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPositivePer() {
        return positivePer;
    }

    public void setPositivePer(float positivePer) {
        this.positivePer = positivePer;
    }

    public String getNegativeColor() {
        return negativeColor;
    }

    public void setNegativeColor(String negativeColor) {
        this.negativeColor = negativeColor;
    }

    public float getNeutralPer() {
        return neutralPer;
    }

    public void setNeutralPer(float neutralPer) {
        this.neutralPer = neutralPer;
    }

    public String getNeutralColor() {
        return neutralColor;
    }

    public void setNeutralColor(String neutralColor) {
        this.neutralColor = neutralColor;
    }

    public float getNegativePer() {
        return negativePer;
    }

    public void setNegativePer(float negativePer) {
        this.negativePer = negativePer;
    }

    public String getPositiveColor() {
        return positiveColor;
    }

    public void setPositiveColor(String positiveColor) {
        this.positiveColor = positiveColor;
    }

    public float getAllcount() {
        return Allcount;
    }

    public void setAllcount(float allcount) {
        Allcount = allcount;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getFillScale() {
        return fillScale;
    }

    public void setFillScale(float fillScale) {
        this.fillScale = fillScale;
    }
}
