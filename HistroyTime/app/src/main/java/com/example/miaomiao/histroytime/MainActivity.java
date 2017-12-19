package com.example.miaomiao.histroytime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.timeaxis.global.Config;
import com.timeaxis.tools.DeviceTools;
import com.timeaxis.tools.ScreenUtil;
import com.timeaxis.view.TimeAxis;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //读取屏幕相关信息
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ScreenUtil.screenWidth = dm.widthPixels < dm.heightPixels ? dm.widthPixels : dm.heightPixels;
        ScreenUtil.screenHeight = dm.widthPixels > dm.heightPixels ? dm.widthPixels : dm.heightPixels;
        ScreenUtil.density = dm.density;
        ScreenUtil.scaledDensity = dm.scaledDensity;
        ScreenUtil.densityDpi = dm.densityDpi;
        Log.d("MainActivity", "screen width:" + ScreenUtil.screenWidth
                + ", height:" + ScreenUtil.screenHeight
                + ", density:" + ScreenUtil.density
                + ", densityDpi:" + ScreenUtil.densityDpi);

        Config.deviceWidth = DeviceTools.getDeviceInfo(this)[0];
        Config.deviceHeight = DeviceTools.getDeviceInfo(this)[1];

//        TimeAxis timeAxis = new TimeAxis(getApplicationContext());
        setContentView(R.layout.activity_main);
    }
}
