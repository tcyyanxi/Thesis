package com.software.androidthesis.entity;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/14 23:58
 * @Decription:
 */
public class BookItem {
    private String bookName;
    private List<String> units;

    public BookItem(String bookName, List<String> units) {
        this.bookName = bookName;
        this.units = units;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public List<String> getUnits() {
        return units;
    }

    public void setUnits(List<String> units) {
        this.units = units;
    }
}
