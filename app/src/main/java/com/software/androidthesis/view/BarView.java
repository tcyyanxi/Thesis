package com.software.androidthesis.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.software.androidthesis.entity.BarEntity;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/2 21:26
 * @Decription:分段堆积柱状图View（绘制分段柱状图）
 */
public class BarView extends View {
    private BarEntity data;
    private Paint paint;
    private float animTimeCell = 0;
    private float fillHeight; // 添加 fillHeight 变量

    public BarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("BarView", "🖌 onDraw 被调用了，高度: " + getHeight());
        if (data != null) {
            float totalHeight = getHeight();
            float top = 0;

            // 计算每一段高度（按比例 * 动画进度）
            float fillHeight = data.getFillScale() * totalHeight * animTimeCell;
            float negativeHeight = data.getNegativePer() * totalHeight * animTimeCell;
            float neutralHeight = data.getNeutralPer() * totalHeight * animTimeCell;
            float positiveHeight = data.getPositivePer() * totalHeight * animTimeCell;

            // 一般（最底层）
            paint.setColor(Color.parseColor(data.negativeColor));
            canvas.drawRect(0f, totalHeight - negativeHeight, getWidth(), totalHeight, paint);
            top = totalHeight - negativeHeight;

            // 良好
            paint.setColor(Color.parseColor(data.neutralColor));
            canvas.drawRect(0f, top - neutralHeight, getWidth(), top, paint);
            top -= neutralHeight;

            // 优秀
            paint.setColor(Color.parseColor(data.positiveColor));
            canvas.drawRect(0f, top - positiveHeight, getWidth(), top, paint);
            top -= positiveHeight;
            Log.d("BarView111111111111111111111", "negativePer=" + data.getNegativePer()
                    + ", neutralPer=" + data.getNeutralPer()
                    + ", positivePer=" + data.getPositivePer());

        }
        else {
            Log.w("BarView", "⚠️ data 为空，无法绘制！");
        }
    }

    public void startAnimation() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "animTimeCell", 0, 1).setDuration(1000);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animTimeCell = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }

    public float getFillHeight() {
        return getHeight() * (data.getNegativePer() + data.getNeutralPer() + data.getPositivePer()) * animTimeCell;
    }

    public void setData(BarEntity data) {
        this.data = data;
        Log.d("BarView", "✅ setData 被调用了: " + data.getTitle());
        invalidate(); // 数据改变时重新绘制视图
    }

    public BarEntity getData() {
        return data;
    }

    public float getAnimTimeCell() {
        return animTimeCell;
    }

    public void setAnimTimeCell(float animTimeCell) {
        this.animTimeCell = animTimeCell;
        invalidate(); // 动画时间单元值改变时重新绘制视图
    }
}


