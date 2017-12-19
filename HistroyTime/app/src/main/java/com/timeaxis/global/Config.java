package com.timeaxis.global;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Point;

public class Config {
	public static float scaleWidth;
	public static float scaleHeight;
	
	public static int deviceWidth;
	public static int deviceHeight;
	
	public static Bitmap gameBK;
	public static Bitmap seedBank;
	public static Bitmap seedFlower;
	public static Bitmap seedPea;
	public static Bitmap sun;
	
	public static Bitmap[] flowerFrames = new Bitmap[8];
	public static Bitmap[] peaFrames = new Bitmap[8];
	public static Bitmap[] zombieFrames = new Bitmap[7];

	public static HashMap<Integer, Point> plantPoints = new HashMap<Integer, Point>();
	public static int[] raceWayYpoints = new int[5];
	
	public static int sunDeadLocationX;

	public static int heightYDistance;
}
