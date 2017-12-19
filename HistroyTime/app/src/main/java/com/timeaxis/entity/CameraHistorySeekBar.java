package com.timeaxis.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.timeaxis.model.Sprite;
import com.timeaxis.model.Touchable;
import com.timeaxis.tools.ScreenUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2017/12/18.
 */

public class CameraHistorySeekBar extends Sprite implements Touchable{

    /**
     * 为半个小时对应的毫秒
     */
    private long mOneScaleTime = 30 * 60 * 1000;
    private int smallScaleCount = 5;
    private int bigScaleCount = 4;
    private int totalSmallScaleCount;
    private double mOnePointTime;
    private float mOneSecondHeight;

    private float mMoveX;
    private float mOldMoveX;
    private long mStartDrawTime;

    private final static int LINE_COLOR = 0x19ffffff;


    private int mSmallScaleWidth;
    private int mSmallScaleDiffHeight;
    private int mBigScaleDiffHeight;

    int layerId;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public CameraHistorySeekBar(Bitmap defaultBitmap, Point position) {
        super(defaultBitmap, position);
    }


    @Override
    public void drawSelf(Canvas canvas) {
        super.drawSelf(canvas);
//        layerId = canvas.saveLayer(0, 0, defaultBitmap.getWidth(), defaultBitmap.getHeight(), null, Canvas.ALL_SAVE_FLAG);
//        Canvas c = new Canvas(defaultBitmap);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        c.drawPaint(paint);
//        paint.setXfermode(null);
        paint.setColor(Color.RED);
        mSmallScaleWidth = ScreenUtil.dip2px(1);
        mSmallScaleDiffHeight = ScreenUtil.dip2px( 23);
        mBigScaleDiffHeight = ScreenUtil.dip2px(15);
        /**
         * 两个小时一共有多少条小刻度线
         */
        totalSmallScaleCount = smallScaleCount * bigScaleCount;

        /**
         * position.y 为空间高度
         * mOneScaleTime * bigScaleCount 为两个小时
         * mOneScaleTime * bigScaleCount / position.y 表示 在 position.y 这个高度显示两个
         * 小时的时间的情况下每一个像素表示的时间，单位为毫秒
         */
        mOnePointTime = 1.0 * mOneScaleTime * bigScaleCount / (double) position.y;


        /**
         * (mOneScaleTime * bigScaleCount / 1000) 表示两个小时对应多少秒
         * (position.y * 1.0 / (mOneScaleTime * bigScaleCount / 1000)) 表示每一秒有多高
         */
        mOneSecondHeight = (float) (position.y * 1.0 / (mOneScaleTime * bigScaleCount / 1000));
        Log.d(TAG,"mOnePointTime="+mOnePointTime+" mOneSecondHeight="+mOneSecondHeight);

        Calendar cal = new GregorianCalendar();
        long currentTimeMillis = System.currentTimeMillis();
        cal.setTimeInMillis(currentTimeMillis);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        /**
         * 获取到小时的整点
         */
        mStartDrawTime = cal.getTimeInMillis();
        /**
         * mOneScaleTime * bigScaleCount / 2 中间的时间点
         * mOneScaleTime * bigScaleCount / 2 - (currentTimeMillis - mStartDrawTime) 中间的时间点的偏移量
         */
        long offsetTime = mOneScaleTime * bigScaleCount / 2 - (currentTimeMillis - mStartDrawTime);
        /**
         * 时间轴要移动的距离
         */
        mMoveX =  (float) offsetTime / (float) 1000 * mOneSecondHeight;
        mOldMoveX = mMoveX;
        drawSmallScale(canvas);
      //  canvas.restoreToCount(layerId);

    }


    private void drawSmallScale(Canvas canvas) {
        /**
         * 一共格子占有的宽度
         */
        float scaleSize = (float) position.y / (float)totalSmallScaleCount;
        /**
         * 时间轴要移动的距离
         */
        float moveStep = mMoveX;

        /**
         * 时间轴要移动的距离是否大于一个格子，大于则 first > 0
         */
        int first = (int) (-(mOldMoveX) / scaleSize);
        first = first - totalSmallScaleCount;
        /**
         * 画格子
         */
        for (int i = first; i <= first + totalSmallScaleCount * 3; i++) {
            if (i % 5 == 0) {
                continue;
            }

//            float left = i * scaleSize + moveStep;
//            float top = mSmallScaleDiffHeight;
//            float right = left + mSmallScaleWidth;
//            float buttom = position.x - mSmallScaleDiffHeight;

            float left = mSmallScaleDiffHeight;
            float top = i * scaleSize + moveStep;
            float right = position.x - mSmallScaleDiffHeight;
            float buttom = top + mSmallScaleWidth;
            if (top < 0) {
                continue;
            }
            if (buttom > position.y) {
                return;
            }

            final RectF rect = new RectF(left, top, right, buttom);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(LINE_COLOR);
            paint.setAntiAlias(true);
            canvas.drawRect(rect, paint);
        }
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return false;
    }
}
