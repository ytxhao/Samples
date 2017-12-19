package com.timeaxis.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by miaomiao on 2017/12/16.
 */

public abstract class Sprite {

    protected  final String TAG = getClass().getSimpleName();
    protected Bitmap defaultBitmap;
    protected Point position;

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    protected boolean isAlive;
    protected Rect touchArea;

    public Sprite(Bitmap defaultBitmap,Point position){
        this.defaultBitmap = defaultBitmap;
        this.position = position;
        touchArea = new Rect(position.x,position.y,
                position.x+defaultBitmap.getWidth(),
                position.y+defaultBitmap.getHeight());
        isAlive = true;
    }

    public void drawSelf(Canvas canvas){
        if(isAlive){
            canvas.drawBitmap(defaultBitmap,position.x,position.y,null);
        }

    }
}
