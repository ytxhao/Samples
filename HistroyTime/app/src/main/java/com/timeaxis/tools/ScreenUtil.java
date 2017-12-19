package com.timeaxis.tools;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

public class ScreenUtil {
    public static int screenWidth;
    public static int screenHeight;
    public static float density;
    public static float scaledDensity;
    public static float densityDpi;
    public static int navBarHeight;  //pixels

    private static Properties properties;

    public static int dip2px(float dipValue) {
        return (int) (dipValue * density);
    }

    public static int sp2px(float spValue) {
        return (int) (spValue * scaledDensity + 0.5f);
    }

    public static int px2dip(float pxValue) {

        return (int) (pxValue / density);
    }

    public static float adapterPaintTextSize(float paintTextSize) {
        return paintTextSize * density;
    }

    private static Properties getProperties() {
        try {
            if (properties == null) {
                properties = new Properties();
                properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }


    public static boolean isMakeTransparent() {
        return (isMIUIV6() || isFLYME4() || Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private static boolean isMIUIV6() {
        if (getProperties() != null) {
            String miuiVer = getProperties().getProperty("ro.miui.ui.version.name");
            if (!TextUtils.isEmpty(miuiVer) &&
                    ("V6".equals(miuiVer) || "V7".equals(miuiVer) || "V8".equals(miuiVer))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMIUI() {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            if (prop.getProperty("ro.miui.ui.version.code", null) != null
                    || prop.getProperty("ro.miui.ui.version.name", null) != null
                    || prop.getProperty("ro.miui.internal.storage", null) != null) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isFLYME4() {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            String flymeId = (String) get.invoke(clz, "ro.build.display.id", "");
            if (!TextUtils.isEmpty(flymeId) && flymeId.toLowerCase().contains("flyme")) {
                if (flymeId.compareToIgnoreCase("flyme 4.0") > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class clazz = activity.getWindow().getClass();
        try {
            int tranceFlag = 0;
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
            tranceFlag = field.getInt(layoutParams);

            field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);

            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            // 状态栏透明且黑色字体
            if (darkmode) {
                extraFlagField.invoke(activity.getWindow(), tranceFlag | darkModeFlag,
                        tranceFlag | darkModeFlag);
            } else {// 只需要状态栏透明
                extraFlagField.invoke(activity.getWindow(), tranceFlag, tranceFlag);
            }
            // //清除黑色字体
            // extraFlagField.invoke(window, 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean setMeizuStatusBarDarkIcon(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {     // >=5.0
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {     //4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 获取虚拟键高度
     */
    public static void calculateNavBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        navBarHeight = getHeightDpi(activity) - rect.bottom;
    }

    public static int getHeightDpi(Activity activity) {
        int dpi = 0;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

}
