package com.software.androidthesis.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/25 11:13
 * @Decription:
 */
public class BarDataEntity implements Serializable {
    private List<Type> typeList;

    public List<Type> getTypeList() {
        return typeList;
    }


    public static class Type implements Serializable {
        private String typeName; // 类型名称
        private double typeScale; // 类型比例

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public double getTypeScale() {
            return typeScale;
        }

        public void setTypeScale(double typeScale) {
            this.typeScale = typeScale;
        }
    }


}


