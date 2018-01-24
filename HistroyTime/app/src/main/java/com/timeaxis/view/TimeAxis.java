package com.timeaxis.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.miaomiao.histroytime.R;
import com.timeaxis.entity.CameraHistorySeekBar;
import com.timeaxis.entity.EmplacePea;
import com.timeaxis.entity.SeedFlower;
import com.timeaxis.entity.SeedPea;
import com.timeaxis.global.Config;
import com.timeaxis.model.Sprite;
import com.timeaxis.model.Touchable;
import com.timeaxis.tools.DeviceTools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by miaomiao on 2017/12/16.
 */

public class TimeAxis  extends SurfaceView implements SurfaceHolder.Callback{

    private RenderThread mRenderThread;
    private WeakReference<Context> mWeakContext;

    private ArrayList<Sprite> deadList;
    private ArrayList<Sprite> timeAxisLayout3;
    private ArrayList<Sprite> timeAxisLayout2;
    private ArrayList<Sprite> timeAxisLayout1;

    private ArrayList<Sprite> timeAxisLayout4plant0;
    private ArrayList<Sprite> timeAxisLayout4plant1;
    private ArrayList<Sprite> timeAxisLayout4plant2;
    private ArrayList<Sprite> timeAxisLayout4plant3;
    private ArrayList<Sprite> timeAxisLayout4plant4;

    private ArrayList<Sprite> timeAxisLayout4zombie0;
    private ArrayList<Sprite> timeAxisLayout4zombie1;
    private ArrayList<Sprite> timeAxisLayout4zombie2;
    private ArrayList<Sprite> timeAxisLayout4zombie3;
    private ArrayList<Sprite> timeAxisLayout4zombie4;

    private Object syncObject = new Object();
    private static TimeAxis timeAxis;

    public TimeAxis(Context context) {
        super(context);
        init();
    }



    public TimeAxis(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeAxis(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public static TimeAxis getTimeAxis() {
        return timeAxis;
    }

    private void init() {
        timeAxis = this;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        Config.gameBK = BitmapFactory.decodeResource(getResources(),R.mipmap.bk);
        Config.seedBank = BitmapFactory.decodeResource(getResources(),R.mipmap.seedbank);
        Config.scaleHeight = Config.deviceHeight/(float) Config.gameBK.getHeight();
        Config.scaleWidth = Config.deviceWidth/(float) Config.gameBK.getWidth();

        Config.gameBK = DeviceTools.resizeBitmap(Config.gameBK);
        Config.seedBank = DeviceTools.resizeBitmap(Config.seedBank);

        Config.seedFlower = BitmapFactory.decodeResource(getResources(), R.mipmap.seed_flower);
        Config.seedPea = BitmapFactory.decodeResource(getResources(), R.mipmap.seed_pea);


        Config.seedFlower =  DeviceTools.resizeBitmap(Config.seedFlower, Config.seedBank.getWidth()/8, Config.seedBank.getHeight());

        Config.seedPea =  DeviceTools.resizeBitmap(Config.seedPea, Config.seedBank.getWidth()/8, Config.seedBank.getHeight());


        int flowerFrameIds[] = {R.mipmap.p_1_01,R.mipmap.p_1_02,
                R.mipmap.p_1_03,R.mipmap.p_1_04,
                R.mipmap.p_1_05,R.mipmap.p_1_06,
                R.mipmap.p_1_07,R.mipmap.p_1_08};


        int peaFrameIds[] = {R.mipmap.p_2_01,R.mipmap.p_2_02,
                R.mipmap.p_2_03,R.mipmap.p_2_04,
                R.mipmap.p_2_05,R.mipmap.p_2_06,
                R.mipmap.p_2_07,R.mipmap.p_2_08};


        int zombieFrameIds[] = {R.mipmap.z_1_01,R.mipmap.z_1_02,
                R.mipmap.z_1_03,R.mipmap.z_1_04,
                R.mipmap.z_1_05,R.mipmap.z_1_06,
                R.mipmap.z_1_07};

        for (int i=0;i<flowerFrameIds.length;i++){
            Config.flowerFrames[i]=DeviceTools.resizeBitmap(BitmapFactory.decodeResource(getResources(), flowerFrameIds[i]));
        }

        for (int i=0;i<peaFrameIds.length;i++){
            Config.peaFrames[i]=DeviceTools.resizeBitmap(BitmapFactory.decodeResource(getResources(), peaFrameIds[i]));
        }

        for (int i=0;i<zombieFrameIds.length;i++){
            Config.zombieFrames[i]=DeviceTools.resizeBitmap(BitmapFactory.decodeResource(getResources(), zombieFrameIds[i]));
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        createElement();
        mRenderThread = new RenderThread(holder);
        mRenderThread.startThread();
    }

    private void createElement() {
        timeAxisLayout1 = new ArrayList<>();
        timeAxisLayout2 = new ArrayList<>();
        timeAxisLayout4plant0 = new ArrayList<>();
        timeAxisLayout4plant1 = new ArrayList<>();
        timeAxisLayout4plant2 = new ArrayList<>();
        timeAxisLayout4plant3 = new ArrayList<>();
        timeAxisLayout4plant4 = new ArrayList<>();

        timeAxisLayout4zombie0 = new ArrayList<>();
        timeAxisLayout4zombie1 = new ArrayList<>();
        timeAxisLayout4zombie2 = new ArrayList<>();
        timeAxisLayout4zombie3 = new ArrayList<>();
        timeAxisLayout4zombie4 = new ArrayList<>();
        deadList = new ArrayList<>();
        SeedFlower seedFlower = new SeedFlower(Config.seedFlower,
                new Point((Config.deviceWidth-Config.seedBank.getWidth())/2+Config.seedBank.getWidth()/7,0));

        SeedPea seedPea = new SeedPea(Config.seedPea,
                new Point((Config.deviceWidth-Config.seedBank.getWidth())/2+Config.seedBank.getWidth()/7*2,0));


        Bitmap mSeekBar = Bitmap.createBitmap(getWidth()/5, getHeight(), Bitmap.Config.ARGB_8888);

        CameraHistorySeekBar cameraHistorySeekBar = new CameraHistorySeekBar(mSeekBar,
                new Point(getWidth()/5,0));

        timeAxisLayout2.add(cameraHistorySeekBar);
//        timeAxisLayout2.add(seedFlower);
//        timeAxisLayout2.add(seedPea);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
//				计算植物安放点，并存储这些安放点到Config.plantPoints
                Config.plantPoints.put(i * 10 + j, new Point((j + 2)
                        * Config.deviceWidth / 11
                        - Config.deviceWidth / 11 / 2, (i + 1)
                        * Config.deviceHeight / 6));

//				计算跑道y坐标，并存储这些安放点到Config.raceWayYpoints
                if (j == 0) {
                    Config.raceWayYpoints[i] = (i + 1) * Config.deviceHeight / 6;
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        mRenderThread.stopThread();
    }




    private class RenderThread extends Thread{
        private static final String TAG = "RenderThread";
        private volatile boolean flagRun;
        private WeakReference<SurfaceHolder> mWeakHolder;

        public RenderThread(SurfaceHolder holder) {
            setName(TAG);
            mWeakHolder = new WeakReference<SurfaceHolder>(holder);
        }

        @Override
        public void run() {
            super.run();
            while (flagRun){

                try {
                    drawUI();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }

        private void drawUI() {
            SurfaceHolder holder = mWeakHolder.get();
            Canvas lockCanvas;
            if (holder != null && (lockCanvas = holder.lockCanvas()) != null) {

                synchronized (syncObject){
                    try {
                        Paint paint = new Paint();
                        paint.setColor(getResources().getColor(R.color.color_FF102A3A));
                        lockCanvas.drawRect(0,0,getWidth(),getHeight(),paint);
//
//                        man.drawSelf(lockCanvas);
//                        updateData();
//                        lockCanvas.drawBitmap(Config.gameBK,0,0,null);
//                        lockCanvas.drawBitmap(Config.seedBank,(Config.deviceWidth-Config.seedBank.getWidth())/2,0,null);

                        for(Sprite sprite : timeAxisLayout2){
                            sprite.drawSelf(lockCanvas);
                        }

//                        for(Sprite sprite : timeAxisLayout1){
//                            sprite.drawSelf(lockCanvas);
//                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        holder.unlockCanvasAndPost(lockCanvas);
                    }


                }

                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }else{
                Log.w(TAG,
                        "SurfaceHolder went away with unhandled events");
            }



        }


        public synchronized void startThread() {
            mRenderThread.setFlagRun(true);
            start();
        }


        private void updateData(){
            deadList.clear();
            for(Sprite sprite : timeAxisLayout1){
                if(!sprite.isAlive()){
                    deadList.add(sprite);
                }
            }

            for(Sprite sprite : timeAxisLayout2){
                if(!sprite.isAlive()){
                    deadList.add(sprite);
                }
            }

            for(Sprite sprite : deadList){
                timeAxisLayout1.remove(sprite);
                timeAxisLayout2.remove(sprite);
            }

        }

        public synchronized void stopThread(){
            mRenderThread.setFlagRun(false);
            interrupt();
        }
        public boolean isFlagRun() {
            return flagRun;
        }

        public void setFlagRun(boolean flagRun) {
            this.flagRun = flagRun;
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);


        return handleTouch(event);
    }

    private boolean handleTouch(MotionEvent event) {

        for (Sprite sprite : timeAxisLayout1){
            if(sprite instanceof Touchable){
                if(((Touchable) sprite).onTouch(event)){
                    return true;
                }
            }
        }

        for (Sprite sprite : timeAxisLayout2){
            if(sprite instanceof Touchable){
                if(((Touchable) sprite).onTouch(event)){
                    return true;
                }
            }
        }

        return false;
    }

    public void apply4EmplacePea(Point position) {
        synchronized (syncObject){
            if(timeAxisLayout1.size() < 1){
                timeAxisLayout1.add(new EmplacePea(Config.peaFrames[0],position));
            }


        }
    }

    public void apply4Plant(Point position,EmplacePea emplacePea) {

        synchronized (syncObject){
            Point point;
            for(Integer key:Config.plantPoints.keySet()){
                point = Config.plantPoints.get(key);
                if((Math.abs(position.x - point.x) < Config.deviceWidth/11/2)
                        &&(Math.abs(position.y - point.y) < Config.deviceHeight/6/2)){

                }
            }
        }
    }
}
