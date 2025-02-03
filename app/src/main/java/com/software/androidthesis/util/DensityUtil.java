package com.software.androidthesis.util;

import android.content.Context;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/2 21:38
 * @Decription:
 */
public class DensityUtil {

    /**
     * Convert dp to px.
     *
     * @param context Context to get resources and device specific display metrics
     * @param dp Value in dp (density independent pixels) to convert into px
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int dip2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Convert px to dp.
     *
     * @param context Context to get resources and device specific display metrics
     * @param px Value in px (pixels) to convert into dp
     * @return A float value to represent dp equivalent to px value
     */
    public static int px2dip(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
