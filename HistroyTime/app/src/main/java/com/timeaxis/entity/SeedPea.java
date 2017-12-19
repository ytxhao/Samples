package com.timeaxis.entity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.timeaxis.model.Sprite;
import com.timeaxis.model.Touchable;
import com.timeaxis.view.TimeAxis;

/**
 * Created by miaomiao on 2017/12/17.
 */

public class SeedPea extends Sprite implements Touchable {
    public SeedPea(Bitmap defaultBitmap, Point position) {
        super(defaultBitmap, position);
    }

    @Override
    public boolean onTouch(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        if(touchArea.contains(x,y)){
            Log.i(TAG,"Pea touch");
            apply4EmplacePea();
            return true;
        }

        return false;
    }

    private void apply4EmplacePea(){
        TimeAxis.getTimeAxis().apply4EmplacePea(new Point(position.x,position.y));
    }

}
