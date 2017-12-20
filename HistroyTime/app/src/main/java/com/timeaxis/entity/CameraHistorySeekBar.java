package com.timeaxis.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

import com.timeaxis.model.Sprite;
import com.timeaxis.model.Touchable;
import com.timeaxis.tools.DateUtil;
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

    private float mMoveY;
    private float mOldMoveY;
    private long mStartDrawTime;

    private final static int LINE_COLOR = 0xFFFFFFFF;
    private final static float FONT_SIZE = 30;

    private int mSmallScaleWidth;
    private int mSmallScaleDiffWidth;
    private int mBigScaleDiffWidth;

    private int mSeekBarWidth;
    private int mSeekBarHeight;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public CameraHistorySeekBar(Bitmap defaultBitmap, Point position) {
        super(defaultBitmap, position);
        mSeekBarWidth = defaultBitmap.getWidth();
        mSeekBarHeight = defaultBitmap.getHeight();
    }


    @Override
    public void drawSelf(Canvas canvas) {
        super.drawSelf(canvas);
        paint.setColor(Color.RED);
        float defaultBitmapWidth = defaultBitmap.getWidth();
        float defaultBitmapHeight = defaultBitmap.getHeight();
        canvas.drawRect(position.x,position.y,position.x+defaultBitmapWidth,position.y+defaultBitmapHeight,paint);
        mSmallScaleWidth = ScreenUtil.dip2px(1);
        mSmallScaleDiffWidth = ScreenUtil.dip2px( 23);
        mBigScaleDiffWidth = ScreenUtil.dip2px(15);
        /**
         * 两个小时一共有多少条小刻度线
         */
        totalSmallScaleCount = smallScaleCount * bigScaleCount;

        /**
         * mSeekBarHeight 为空间高度
         * mOneScaleTime * bigScaleCount 为两个小时
         * mOneScaleTime * bigScaleCount / mSeekBarHeight 表示 在 mSeekBarHeight 这个高度显示两个
         * 小时的时间的情况下每一个像素表示的时间，单位为毫秒
         */
        mOnePointTime = 1.0 * mOneScaleTime * bigScaleCount / (double) mSeekBarHeight;


        /**
         * (mOneScaleTime * bigScaleCount / 1000) 表示两个小时对应多少秒
         * (mSeekBarHeight * 1.0 / (mOneScaleTime * bigScaleCount / 1000)) 表示每一秒有多高
         */
        mOneSecondHeight = (float) (mSeekBarHeight * 1.0 / (mOneScaleTime * bigScaleCount / 1000));
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
        mMoveY =  (float) offsetTime / (float) 1000 * mOneSecondHeight;
        mOldMoveY = mMoveY;
        drawSmallScale(canvas);
        drawBigScale(canvas);

    }


    private void drawBigScale(Canvas canvas) {
        /**
         * 每两个大条之间的距离
         */
        float scaleSize = (float) (mSeekBarHeight) / bigScaleCount;
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(FONT_SIZE);
        float moveStep = mMoveY;
        paint.setAntiAlias(true);
        int first = (int) (-(mOldMoveY) / scaleSize);
        first = first - bigScaleCount;
        float left = mBigScaleDiffWidth + position.x ;
        float right = mSeekBarWidth - mBigScaleDiffWidth + position.x ;
        for (int i = first; i <= first + bigScaleCount * 3; i++) {
            float top = i * scaleSize + moveStep + position.y;
            float bottom = top + mSmallScaleWidth + position.y;

            if (top < 0) {
                continue;
            }
            if (bottom > mSeekBarHeight) {
                return;
            }
            paint.setColor(LINE_COLOR);
            final RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRect(rect, paint);

            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(mStartDrawTime + i * mOneScaleTime);

            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            String text = "";
            if (hour < 10) {
                text += "0" + hour;
            } else {
                text += hour;
            }
            text += ":";
            if (minute < 10) {
                text += "0" + minute;
            } else {
                text += minute;
            }
            /**
             * 绘制时间的文字
             */
            paint.setColor(LINE_COLOR);
            paint.setStrokeWidth(0);
            paint.setTextSize(FONT_SIZE);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();

            String newText = DateUtil.convertStandardTime2Local(text);
            float baseLineX = right + 10;
            float baseLineY = top+(bottom-top)/2 +(fm.bottom - fm.top)/2 - fm.bottom;
            canvas.drawText(newText, baseLineX, baseLineY, paint);

        }

    }

    private void drawSmallScale(Canvas canvas) {
        /**
         * 一共格子占有的宽度
         */
        float scaleSize = (float) mSeekBarHeight / (float)totalSmallScaleCount;
        /**
         * 时间轴要移动的距离
         */
        float moveStep = mMoveY;

        /**
         * 时间轴要移动的距离是否大于一个格子，大于则 first > 0
         */
        int first = (int) (-(mOldMoveY) / scaleSize);
        first = first - totalSmallScaleCount;
        /**
         * 画格子
         */
        int length = first + totalSmallScaleCount * 3;
        Log.d(TAG,"first="+first+" totalSmallScaleCount="+totalSmallScaleCount+" length="+length +" mSeekBarWidth="+mSeekBarWidth+" mSeekBarHeight="+mSeekBarHeight);
        for (int i = first; i <= length; i++) {
            if (i % 5 == 0) {
                continue;
            }

            float left = mSmallScaleDiffWidth + position.x;
            float top = i * scaleSize + moveStep + position.y;
            float right = mSeekBarWidth - mSmallScaleDiffWidth + position.x;
            float bottom = top + mSmallScaleWidth + position.y;
            Log.d(TAG,"left="+left+" top="+top+" right="+right+" bottom="+bottom+" scaleSize="+scaleSize+" moveStep="+moveStep+" i="+i);
            if (top < 0) {
                continue;
            }
            if (bottom > mSeekBarHeight) {
                return;
            }
            Log.d(TAG,"draw left="+left+" top="+top+" right="+right+" bottom="+bottom+" scaleSize="+scaleSize+" moveStep="+moveStep+" i="+i);
            final RectF rect = new RectF(left, top, right, bottom);
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
