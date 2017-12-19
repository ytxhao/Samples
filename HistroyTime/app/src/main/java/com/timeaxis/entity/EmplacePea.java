package com.timeaxis.entity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.timeaxis.global.Config;
import com.timeaxis.model.Sprite;
import com.timeaxis.model.Touchable;
import com.timeaxis.view.TimeAxis;

/**
 * Created by miaomiao on 2017/12/17.
 */

public class EmplacePea extends Sprite implements Touchable{
    public EmplacePea(Bitmap defaultBitmap, Point position) {
        super(defaultBitmap, position);
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if(touchArea.contains(x,y)){
            Log.i(TAG,"Pea touch");

            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:

                    position.x= x - Config.peaFrames[0].getWidth()/2;
                    position.y = y - Config.peaFrames[0].getHeight()/2;
                    touchArea.offsetTo(position.x,position.y);

                    break;
                case MotionEvent.ACTION_UP:

                    isAlive = false;
                    TimeAxis.getTimeAxis().apply4Plant(position,this);
                    break;
            }
            return true;
        }

        return false;
    }
}
