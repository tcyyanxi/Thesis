package com.software.androidthesis.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/21 21:03
 * @Decription:
 */
public class WaveView extends View {

    private Paint mWavePaint;
    private Bitmap mBallBitmap;
    private ObjectAnimator mWaveAnimator;
    private boolean isWaveMoving;
    private int mOffsetA;
    private int mOffsetB;
    private int mWaveHeightA;
    private int mWaveHeightB;
    private float mWaveACycle;
    private float mWaveBCycle;
    private int mWaveSpeedA;
    private int mWaveSpeedB;
    private int mWaveColor;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mWavePaint = new Paint();
        mWavePaint.setColor(Color.parseColor("#d6e3ff"));// 设置默认颜色为蓝色
        mWavePaint.setStyle(Paint.Style.FILL);
        isWaveMoving = true;
        startWaveAnimation();
    }

    private double getWaveY(int x, int offset, int waveHeight, float waveCycle) {
        return waveHeight * Math.sin(waveCycle * (x + offset)) + getHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getHeight() > 0 && getWidth() > 0) {

            // 裁剪画布为圆形
            Path path = new Path();
            int radius = Math.min(getWidth(), getHeight()) / 2;
            path.addCircle(getWidth() / 2, getHeight() / 2, radius, Path.Direction.CW);
            canvas.clipPath(path);

            canvas.drawColor(Color.TRANSPARENT);
            int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            if (isWaveMoving) {
                for (int i = 0; i < getWidth(); i++) {
                    canvas.drawLine(i, (int) getWaveY(i, mOffsetA, mWaveHeightA, mWaveACycle), i, getHeight(), mWavePaint);
                    canvas.drawLine(i, (int) getWaveY(i, mOffsetB, mWaveHeightB, mWaveBCycle), i, getHeight(), mWavePaint);
                }
            }
            mWavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            if (mBallBitmap != null) {
                canvas.drawBitmap(mBallBitmap, 0, 0, mWavePaint);
            }
            mWavePaint.setXfermode(null);
            canvas.restoreToCount(sc);
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private void startWaveAnimation() {
        mWaveAnimator = ObjectAnimator.ofFloat(this, "waveOffset", 0f, 1f);
        mWaveAnimator.setDuration(1000);
        mWaveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mWaveAnimator.setInterpolator(new LinearInterpolator());
        mWaveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetA += mWaveSpeedA;
                mOffsetB += mWaveSpeedB;
                invalidate();
            }
        });
        mWaveAnimator.start();
    }

    public void setWaveParameters(int waveHeightA, float waveACycle, int waveSpeedA,
                                  int waveHeightB, float waveBCycle, int waveSpeedB, int waveColor) {
        this.mWaveHeightA = waveHeightA;
        this.mWaveACycle = waveACycle;
        this.mWaveSpeedA = waveSpeedA;
        this.mWaveHeightB = waveHeightB;
        this.mWaveBCycle = waveBCycle;
        this.mWaveSpeedB = waveSpeedB;
        this.mWaveColor = waveColor;
        mWavePaint.setColor(waveColor);
    }

    public void setBallBitmap(Bitmap bitmap) {
        this.mBallBitmap = bitmap;
    }
}


