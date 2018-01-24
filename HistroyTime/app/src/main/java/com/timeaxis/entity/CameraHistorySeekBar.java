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
import android.view.ScaleGestureDetector;

import com.timeaxis.model.Sprite;
import com.timeaxis.model.Touchable;
import com.timeaxis.tools.DateUtil;
import com.timeaxis.tools.ScreenUtil;
import com.timeaxis.view.TimeAxis;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2017/12/18.
 */

public class CameraHistorySeekBar extends Sprite implements Touchable{

    public static final int ACTION_POINTER_UP = 0x6, ACTION_POINTER_INDEX_MASK = 0x0000ff00,
            ACTION_POINTER_INDEX_SHIFT = 8;
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
    private boolean mIsViewDragging;
    private int mActivePointerId;
    private float mDownMotionY;
    private long now;
    private long mProgress;
    private long mCurrentSelectProgress;
    private ScaleGestureDetector mScaleDetector;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private OnProgressChangeListener progressListener;

    public CameraHistorySeekBar(Bitmap defaultBitmap, Point position) {
        super(defaultBitmap, position);
        mSeekBarWidth = defaultBitmap.getWidth();
        mSeekBarHeight = defaultBitmap.getHeight();
        mScaleDetector = new ScaleGestureDetector(TimeAxis.getTimeAxis().getContext(), new ScaleListener());
        init();
    }

    private void init() {
        mSmallScaleWidth = ScreenUtil.dip2px(1);
        mSmallScaleDiffWidth = ScreenUtil.dip2px( 15);
        mBigScaleDiffWidth = ScreenUtil.dip2px(23);
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
    }


    @Override
    public void drawSelf(Canvas canvas) {
        super.drawSelf(canvas);
        paint.setColor(Color.BLACK);
        float defaultBitmapWidth = defaultBitmap.getWidth();
        float defaultBitmapHeight = defaultBitmap.getHeight();
        canvas.drawRect(position.x,position.y,position.x+defaultBitmapWidth,position.y+defaultBitmapHeight,paint);

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
        float left = position.x ;
        float right = mBigScaleDiffWidth + position.x ;
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

            float left = position.x;
            float top = i * scaleSize + moveStep + position.y;
            float right = mSmallScaleDiffWidth + position.x;
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
    public boolean mZooming = false;
    private boolean isMoving = false;
    @Override
    public boolean onTouch(MotionEvent event) {

        int onTouchX = (int) event.getX();
        int onTouchY = (int) event.getY();

        if(touchArea.contains(onTouchX,onTouchY)){
            Log.i(TAG,"touch");
            mScaleDetector.onTouchEvent(event);
            int pointerIndex;
            final int action = event.getAction() & MotionEvent.ACTION_MASK;

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
                    pointerIndex = event.findPointerIndex(mActivePointerId);
                    mDownMotionY = event.getY(pointerIndex);
                    now = new Date().getTime();
                    onStartViewTouch();
                    attemptClaimDrag();
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mIsViewDragging){
                        try {
                            pointerIndex = event.findPointerIndex(mActivePointerId);
                            final float y = event.getY(pointerIndex);
                            float moveSize = y - mDownMotionY;
                            if (Math.abs(moveSize) < 1) {
                                return true;
                            }

                            mMoveY = mOldMoveY + moveSize;
                            Log.d(TAG,"ACTION_MOVE mMoveY="+mMoveY);
                            onStartViewTouch();
                            attemptClaimDrag();

                            if (progressListener != null) {
                                long progress = mProgress - (long) (moveSize * mOnePointTime);
                                if (progress <= now) {
                                    mCurrentSelectProgress = progress;
                                    progressListener.onProgressChanging(progress, true);
                                }
                            }

                            computeScaleToDetail(y);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "onTouchEvent UP, mIsViewDragging:" + mIsViewDragging + ", mZooming:" + mZooming + ", isMoving:" + isMoving);
                    if (mIsViewDragging) {
                        if (!mZooming || isMoving) {
                            pointerIndex = event.findPointerIndex(mActivePointerId);
                            final float y = event.getY(pointerIndex);
                            float moveSize = y - mDownMotionY;
                            long progress = mProgress - (long) (moveSize * mOnePointTime);
                            if (progress > now) {
                                setProgress(now);
                            } else {
                                resetCalender(moveSize);
                            }
                            if (progressListener != null) {
                                progressListener.onProgressChanged(getProgress());
                            }
                        }
                        onStopViewTouch();
                    }
                    isMoving = false;
                    mZooming = false;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN: {
                    final int index = event.getPointerCount() - 1;
                    // final int index = ev.getActionIndex();
                    mDownMotionY = event.getY(index);
                    mActivePointerId = event.getPointerId(index);
//                    invalidate();
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP:
                    //mZooming = false;
                    onSecondaryPointerUp(event);
//                    invalidate();
                    break;

            }
            return true;
        }

        return false;
    }

    private final void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;

        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose
            // a new active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mDownMotionY = ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private void onStopViewTouch() {
        mIsViewDragging = false;
        if (this.progressListener != null) {
            this.progressListener.onDraggingStateChanged(mIsViewDragging);
        }
    }

    public long getProgress() {
        return mProgress;
    }

    private long startComputeTime = 0;
    private float mLastY = 0;
    private static final long SCALE_TO_DETAIL_WAIT_TIME = 800;
    private boolean enableAutoScaleToDetail = false;
    private void computeScaleToDetail(float y) {
        if (startComputeTime == 0) {
            startComputeTime = System.currentTimeMillis();
            mLastY = y;
            return;
        }

        if (Math.abs(mLastY - y) > 10) {
            startComputeTime = 0;
        } else {
            if (System.currentTimeMillis() - startComputeTime >= SCALE_TO_DETAIL_WAIT_TIME) {
                if (enableAutoScaleToDetail) {
                    float moveSize = y - mDownMotionY;
                    //AntsLog.d("move2", moveSize + "");
                    resetCalender(moveSize);
                    //Log.d("event", "progress=" + mProgress + "");
                    //invalidate();
                    scaleToDetail();
                    mDownMotionY = y;
                    enableAutoScaleToDetail = false;
                    scaleListener.onZoomInOut(ZOOM_AUTO);
                    isMoving = true;
                }
            }
        }
        mLastY = y;
    }
    private double mScaleFactor;
    private boolean animationStarted = false;
    private boolean hasEvent = false;

    private int[] V_SCALES = new int[]{5 * 60 * 1000, 30 * 60 * 1000, 4 * 60 * 60 * 1000};
    private int currentVScale = 1;
    private int lastVScale = 1;

    private void scaleToDetail() {
        mScaleFactor = 2;
        doScale();
    }


    private void doScale() {
        if (animationStarted || !hasEvent) {
            return;
        }

        Log.d("factor", mScaleFactor + "");
        lastVScale = currentVScale;
        if (mScaleFactor < 1) {
            currentVScale++;
            if (currentVScale > V_SCALES.length - 1) {
                currentVScale = V_SCALES.length - 1;
            }
        } else if (mScaleFactor > 1) {
            currentVScale--;
            if (currentVScale < 0) {
                currentVScale = 0;
            }
        }

        if (lastVScale != currentVScale) {
            startAnimation();
        }
    }

    private void startAnimation() {

    }

    private void onStartViewTouch() {
        mIsViewDragging = true;
        if (this.progressListener != null) {
            this.progressListener.onDraggingStateChanged(mIsViewDragging);
        }
    }

    private void attemptClaimDrag() {
        if (TimeAxis.getTimeAxis().getParent() != null) {
            TimeAxis.getTimeAxis().getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void resetCalender(float moveSize) {
        mOldMoveY = mMoveY;
        mProgress -= moveSize * mOnePointTime;
    }


    public void setProgress(final long progress) {
        setProgress(progress, true);

    }

    public void setProgress(final long progress, boolean shouldCallback) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(progress);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        mProgress = progress;
        mCurrentSelectProgress = progress;
        this.mStartDrawTime = cal.getTimeInMillis();
        int diff = (int) (mOneScaleTime * bigScaleCount / 2 - (mProgress - mStartDrawTime));
        mMoveY = (float) (diff / 1000 * mOneSecondHeight);
        mOldMoveY = mMoveY;

        if (shouldCallback) {
            if (progressListener != null) {
                progressListener.onProgressChanging(progress, false);
            }
        }

    }

    public interface OnProgressChangeListener {
         void onProgressChanged(long time);

         void onProgressChanging(long time, boolean fromSeekBar);

         void onDraggingStateChanged(boolean isViewDragging);

         void onTouchWhenDisabled();
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        this.progressListener = listener;
    }


    public interface ScaleStatsListener{
        void onZoomInOut(int mode);
    }

    private ScaleStatsListener scaleListener;

    public void setScaleStatsListener(ScaleStatsListener scaleListener){
        this.scaleListener = scaleListener;
    }

    public static final int ZOOM_MAN_OUT = 1;
    public static final int ZOOM_MAN_IN = 2;
    public static final int ZOOM_AUTO = 3;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mZooming = true;
            Log.d(TAG, "onScaleBegin...");
            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float px = detector.getFocusX();
            float py = detector.getFocusY();
            mScaleFactor = detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            Log.d(TAG, "onScaleEnd...");
            doScale();
            if(scaleListener != null){
                if(mScaleFactor < 1) {
                    scaleListener.onZoomInOut(ZOOM_MAN_OUT);
                }else{
                    scaleListener.onZoomInOut(ZOOM_MAN_IN);
                }
            }
            mZooming = false;
        }
    }
}
