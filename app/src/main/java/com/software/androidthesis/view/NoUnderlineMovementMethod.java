package com.software.androidthesis.view;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 自定义 LinkMovementMethod，去除点击链接时的默认下划线效果
 */
public class NoUnderlineMovementMethod extends LinkMovementMethod {

    private static NoUnderlineMovementMethod instance;

    public static NoUnderlineMovementMethod getInstance() {
        if (instance == null) {
            instance = new NoUnderlineMovementMethod();
        }
        return instance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        return super.onTouchEvent(widget, buffer, event);
    }
}
