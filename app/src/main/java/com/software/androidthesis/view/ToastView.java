package com.software.androidthesis.view;

import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.software.androidThesis.R;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 17:43
 * @Decription:自定义Toast
 */
public class ToastView {
    public static void showCustomToast(Context context, String message) {
        Toast toast = new Toast(context);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();

        toast.setGravity(Gravity.CENTER, 0, height / 3);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        TextView tvMessage = view.findViewById(R.id.ErrorTips);
        tvMessage.setText(message);

        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
