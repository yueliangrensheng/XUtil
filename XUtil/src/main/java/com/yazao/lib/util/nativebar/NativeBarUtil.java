package com.yazao.lib.util.nativebar;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * 类描述：底部导航栏相关处理
 *
 * @author zhaishaoping
 * @data 12/12/2017 9:26 PM
 */

public class NativeBarUtil {


    // 获取是否存在NavigationBar

    public boolean checkDeviceHasNavigationBar(Context context) {

        boolean hasNavigationBar = false;

        Resources rs = context.getResources();

        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");

        if (id > 0) {

            hasNavigationBar = rs.getBoolean(id);

        }

        try {

            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");

            Method m = systemPropertiesClass.getMethod("get", String.class);

            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");

            if ("1".equals(navBarOverride)) {

                hasNavigationBar = false;

            } else if ("0".equals(navBarOverride)) {

                hasNavigationBar = true;

            }

        } catch (Exception e) {

        }

        return hasNavigationBar;

    }

    /**
     * 获取虚拟功能键高度
     */

    public int getVirtualBarHeight(Context context) {

        int vh = 0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics dm = new DisplayMetrics();

        try {

            @SuppressWarnings("rawtypes")

            Class c = Class.forName("android.view.Display");

            @SuppressWarnings("unchecked")

            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);

            method.invoke(display, dm);

            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return vh;

    }

    private static class SingleHolder {
        private static final NativeBarUtil ourInstance = new NativeBarUtil();
    }

    public static NativeBarUtil getInstance() {
        return SingleHolder.ourInstance;
    }

    private NativeBarUtil() {
    }
}