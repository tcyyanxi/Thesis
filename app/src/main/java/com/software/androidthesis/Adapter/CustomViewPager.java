package com.software.androidthesis.Adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;


public class CustomViewPager extends ViewPager {
    private boolean swipeEnabled = true;

    public CustomViewPager(@NonNull Context context) {
        super(context);
        swipeEnabled = true;
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        swipeEnabled = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return swipeEnabled && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return swipeEnabled && super.onTouchEvent(ev);
    }

    public void setSwipeEnabled(boolean enabled) {
        swipeEnabled = enabled;
    }
}














