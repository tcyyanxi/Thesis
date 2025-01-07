package com.software.androidthesis.base;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 13:44
 * @Decription:
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 应用动态颜色
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
