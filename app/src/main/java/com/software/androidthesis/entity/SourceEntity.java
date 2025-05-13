package com.software.androidthesis.entity;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/4/6 8:29
 * @Decription:
 */
public class SourceEntity {
    private List<Source> list;

    public List<Source> getList() {
        return list;
    }
    public void setList(List<Source> list) {
        this.list = list;
    }

    public static class Source {
        private String source;
        private int badCount;
        private int goodCount;
        private int otherCount;
        private int allCount;
        private int scale;


        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public int getBadCount() {
            return badCount;
        }

        public void setBadCount(int badCount) {
            this.badCount = badCount;
        }

        public int getGoodCount() {
            return goodCount;
        }

        public void setGoodCount(int goodCount) {
            this.goodCount = goodCount;
        }

        public int getOtherCount() {
            return otherCount;
        }

        public void setOtherCount(int otherCount) {
            this.otherCount = otherCount;
        }

        public int getAllCount() {
            return allCount;
        }

        public void setAllCount(int allCount) {
            this.allCount = allCount;
        }

        public int getScale() {
            return scale;
        }

        public void setScale(int scale) {
            this.scale = scale;
        }
    }
}
