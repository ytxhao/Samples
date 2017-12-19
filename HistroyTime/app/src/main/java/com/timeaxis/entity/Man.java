package com.timeaxis.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.timeaxis.entity.Face;
import com.timeaxis.model.Sprite;


/**
 * Created by miaomiao on 2017/12/16.
 */

public class Man extends Sprite {
    public Man(Bitmap defaultBitmap, Point position) {
        super(defaultBitmap, position);
    }

    public Face createFace(Context context){
        Bitmap faceBitmap = BitmapFactory.decodeResource(context.getResources(), android.R.mipmap.sym_def_app_icon);
        Face face = new Face(faceBitmap,new Point(position.x+20,position.y+20));
        return face;
    }
}
