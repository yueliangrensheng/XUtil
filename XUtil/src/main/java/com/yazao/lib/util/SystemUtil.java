package com.yazao.lib.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.lang.Runtime.getRuntime;

/**
 * 类描述：系统相关的工具类
 *
 * @author zhaishaoping
 * @data 06/04/2017 6:34 PM
 */

public class SystemUtil {

    /**
     * 方法描述：打开App 应用信息提示
     *
     * @return
     * @author zhaishaoping
     * @time 10/08/2017 4:25 PM
     */
    public static void getAppInfo(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

    /** =============================获取 UI线程/线程名称 ================================== */
    /**
     * 方法描述：获取当前正在运行App的进程名
     *
     * @return
     * @author zhaishaoping
     * @time 06/04/2017 6:59 PM
     */
    public static final String getProcessName(Context context) {
        String processName = null;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : activityManager.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }

            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 方法描述：判断当前进程是否是UI进程
     *
     * @return
     * @author zhaishaoping
     * @time 06/04/2017 7:06 PM
     */
    public static boolean isMainProcess(Context context) {
        String packageName = context.getPackageName();
        String processName = getProcessName(context);
        return packageName.equals(processName);
    }


    /** =============================获取 渠道 标记================================== */

    /**
     * @param context
     * @return String 返回渠道标记。 例如： 获取出来的渠道标记为 wandoujia。
     *      platformChannel[0] :返回的是 platform；
     *      platformChannel[1] :返回的是 channel；
     * @description <p>
     * 渠道标记
     * </p>
     * <p>
     * 空文件（名称：7659channel_wandoujia）添加到 app中的 META-INF文件夹中。
     * </p>
     * @author yueliangrensheng create at 2015年4月3日上午10:35:08
     */
    public static String[] getChannelFromApkFile(Context context) {
        if (context == null) {
            return null;
        }
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith("META-INF/7659")) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 7659platform_xxx7659sep7659channel_xxx
        String[] split1 = ret.split("7659sep");
        if (split1 != null && split1.length > 0) {
            String platformChannel[] = new String[]{"", ""};
            for (int i = 0; i < split1.length; i++) {
                String[] split = split1[i].split("_");
                if (split != null && split.length >= 2) {
                    platformChannel[i] = split1[i]
                            .substring(split[0].length() + 1);
                }
            }
            return platformChannel;
        }
        return null;
    }

    /**
     * @param context
     * @return String
     * @description 获取清单文件中 配置的 meta-data 数据
     * @author Administrator create at 2014-9-12下午3:39:58
     */
    public static String getMetaData(Context context, String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String code = null;
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Object value = ai.metaData.get(key);
            if (value != null) {
                code = value.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 获取applicationInfo
     */
    public static ApplicationInfo getApplicationInfo(Application application) {
        try {
            return application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** =============================获取 App 信息================================== */

    /**
     * 跳转到app的安装详情页面
     *
     * @param context
     */
    public static void showAppInstalledDetails(Context context) {
        if (context == null) {
            return;
        }
        Uri packageURI = Uri.parse("package:" + context.getPackageName());

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);

        context.startActivity(intent);
    }

    /**
     * 获取 app的名称
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        if (context == null) {
            return "";
        }
        String appName = "";
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            applicationInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            appName = (String) pm.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    /**
     * @param context
     * @return String
     * @description 获取 当前应用的 包名
     * @author Administrator create at 2014-9-12下午3:04:53
     */
    public static String getAppPackageName(Context context) {
        if (context == null) {
            return "";
        }
        String packagename = "";
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(
                    context.getPackageName(), 0);
            packagename = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            packagename = context.getPackageName();
        }
        return packagename;
    }

    /**
     * @param context
     * @return int
     * @description 取得应用软件的版本号
     * @author Administrator create at 2014-7-15下午4:13:05
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 1;
        if (context == null) {
            return versionCode;
        }
        try {
            versionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * @param context
     * @return String
     * @description 取得应用软件的版本名称。如2.3.1
     * @author Administrator create at 2014-7-15下午4:13:49
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        if (context == null) {
            return "";
        }
        try {
            versionName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /** =============================获取 设备 信息================================== */

    /**
     * @param context
     * @return String
     * @description 获取设备的mac地址
     * <p>
     * 需要权限："android.permission.ACCESS_WIFI_STATE"
     * </p>
     * @author Administrator create at 2014-7-17下午1:13:20
     */
    public static String getAndroidMacID(Context context) {
        if (context == null) {
            return "";
        }
        WifiManager manager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getMacAddress();
        }
        return null;
    }

    /**
     * @param context
     * @return 如果wifi关闭，那么ip地址为0 。
     * @description 获取设备的ip地址。
     * <p>
     * 需要权限："android.permission.ACCESS_WIFI_STATE"
     * </p>
     * @author Administrator create at 2014-7-17下午1:14:45
     */
    public static int getDeviceIP(Context context) {
        if (context == null) {
            return -1;
        }
        WifiManager manager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getIpAddress();
        }
        return 0;
    }

    /**
     * @param context
     * @return Returns the unique device ID, for example, the IMEI for GSM and
     * the MEID or ESN for CDMA phones. Return null if device ID is not
     * available
     * @description 获取设备的id（IMEI、ESN）
     * <p>
     * 需要权限："android.permission.READ_PHONE_STATE"
     * </p>
     * @author Administrator create at 2014-7-15下午2:30:24
     */
    public static String getAndroidIMEI(Context context) {
        if (context == null) {
            return "";
        }
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);
        // check if has the permission
        if (PackageManager.PERMISSION_GRANTED == context.getPackageManager()
                .checkPermission(Manifest.permission.READ_PHONE_STATE,
                        context.getPackageName())) {
            return manager.getDeviceId();
        } else {
            return getAndroidMacID(context);
        }
    }

    /**
     * @return String
     * @description 取到android系统版本号如2.3.3，4.0.3
     * @author Administrator create at 2014-7-15下午3:57:02
     */
    public static String getAndroidVersion() {
        String androidVersion = "";
        androidVersion = android.os.Build.VERSION.RELEASE;
        return androidVersion;
    }

    /**
     * @return String
     * @description 取到android系统API LEVEL编号如 14, 15, 17, 21
     * @author Administrator create at 2014-7-15下午3:57:02
     */
    public static int getDeviceSysLevel() {
        int androidVersion = android.os.Build.VERSION.SDK_INT;
        return androidVersion;
    }

    /**
     * @param context
     * @return String
     * @description 获取设备的型号，如Mi 1S
     * @author Administrator create at 2014-7-15下午4:07:24
     */
    public static String getDeviceModel(Context context) {
        if (context == null) {
            return "";
        }
        String androidModel = "";
        String model = "";
        // String mtyb = android.os.Build.BRAND;// 手机品牌
        androidModel = android.os.Build.MODEL;// 手机型号
        //判断是手机还是ｐａｄ
        boolean pad = isPad(context);
        if (pad) {
            model = "(pad)";
        } else {
            model = "(phone)";
        }

        return androidModel + model;
    }

    /**
     * @param context
     * @return String
     * @description 取到系统的编辑系统，主要是关于手机，如android的内核，编译时间
     * @author Administrator create at 2014-7-15下午4:11:42
     */
    public static String getDeviceDisplayVersion(Context context) {
        if (context == null) {
            return "";
        }
        String androidDisplay = "";
        androidDisplay = android.os.Build.DISPLAY;
        return androidDisplay;
    }

    /**
     * @param context
     * @return String
     * @description 取得手机的主板供应商。
     * @author Administrator create at 2014-7-15下午4:12:30
     */
    public static String getDeviceBoardName(Context context) {
        if (context == null) {
            return "";
        }
        String androidBoard = "";
        androidBoard = android.os.Build.BOARD;
        return androidBoard;
    }

    /**
     * @param context
     * @return String
     * @description 获取运营商
     * @author Administrator create at 2014-7-15下午4:20:06
     */
    public static String getSimOperator(Context context) {
        if (context == null) {
            return "";
        }
        TelephonyManager tel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tel.getSimOperator();
    }

    /**
     * @return boolean true:已经root了，否则 没有root过。
     * @description 判断设备是否 Root
     * @author yueliangrensheng create at 2015年4月3日上午10:54:44
     */
    public static boolean isRoot() {
        boolean isRoot = false;
        String sys = System.getenv("PATH");
        ArrayList<String> commands = new ArrayList<String>();
        String[] path = sys.split(":");
        for (int i = 0; i < path.length; i++) {
            String commod = "ls -l " + path[i] + "/su";
            commands.add(commod);
        }
        ArrayList<String> res = run("/system/bin/sh", commands);
        String response = "";
        for (int i = 0; i < res.size(); i++) {
            response += res.get(i);
        }
        String root = "-rwsr-sr-x root     root";
        if (response.contains(root)) {
            isRoot = true;
        }
        return isRoot;
    }

    private static ArrayList<String> run(String shell,
                                         ArrayList<String> commands) {
        ArrayList<String> output = new ArrayList<String>();
        Process process = null;
        try {
            process = getRuntime().exec(shell);

            BufferedOutputStream shellInput = new BufferedOutputStream(
                    process.getOutputStream());
            BufferedReader shellOutput = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            for (String command : commands) {

                shellInput.write((command + " 2>&1\n").getBytes());
            }

            shellInput.write("exit\n".getBytes());
            shellInput.flush();

            String line;
            while ((line = shellOutput.readLine()) != null) {

                output.add(line);
            }

            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (process != null) {
                    //exitValue()函数采用非阻塞的方式返回，如果没有立即拿到返回值，则抛出异常。
                    process.exitValue();
                }
            } catch (Exception e) {
                process.destroy();
            }
        }

        return output;
    }

    /**
     * @param moContext
     * @return int
     * @description 获取手机屏幕的宽度(像素)
     * @author Administrator create at 2014-7-31下午4:46:38
     */
    public static int getScreenWidth(Context moContext) {
        if (moContext == null) {
            return 0;
        }
        WindowManager winManager = (WindowManager) moContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        winManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;

    }

    /**
     * @param moContext
     * @return int
     * @description 获取手机屏幕的高度(像素)
     * @author Administrator create at 2014-7-31下午4:57:21
     */
    public static int getScreenHeight(Context moContext) {
        if (moContext == null) {
            return 0;
        }
        WindowManager winManager = (WindowManager) moContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        winManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * @param moContext
     * @return float
     * @description 获取手机屏幕的密度（0.75/ 1.0/ 1.5）
     * @author Administrator create at 2014-7-31下午5:00:48
     */
    public static float getScreenDensity(Context moContext) {
        if (moContext == null) {
            return 0;
        }
        WindowManager winManager = (WindowManager) moContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        winManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.density;

    }

    /**
     * @param moContext
     * @return float
     * @description 获取手机屏幕密度Dpi(120 / 160 / 240)
     * @author Administrator create at 2014-7-31下午5:01:34
     */
    public static float getScreenDensityDpi(Context moContext) {
        if (moContext == null) {
            return 0;
        }
        WindowManager winManager = (WindowManager) moContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        winManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.densityDpi;

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断是否为平板
     *
     * @return
     */
    public static boolean isPad(Context context) {
        if (context == null) {
            return false;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // 屏幕宽度
        float screenWidth = display.getWidth();
        // 屏幕高度
        float screenHeight = display.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
//        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
//        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
//        // 屏幕尺寸
//        double screenInches = Math.sqrt(x + y);

        double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
        double screenInches = diagonalPixels / (160 * dm.density);
        // 大于6尺寸则为Pad
        if (screenInches >= 6.5) {
            return true;
        }
        return false;
    }


    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        if (context == null) {
            return false;
        }
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /** =============================上报数据（ 设备 信息+ app信息）======================= */

    /**
     * @return String
     * @description 根据汇报日志格式，获取设备信息
     * @author Administrator create at 2014-7-15下午4:15:34
     */
    public static String getDeviceProper(Context moContext) {
        if (moContext == null) {
            return null;
        }
        // udid（android可以标识设备的标识可以填在这个职位，也可以是多个标识的组合，也可以客户端自定义设备标识）
        // devmac（ios设备mac）
        // idfa（操作系统广告标识，android也可以考虑使用）
        // devicetype(设备类型，填iphone或ipad或android)
        // osVer（设备版本）
        // jailbreak（是否越狱或者root，是填y，不是填n）
        // telcom(网络或电信商，获取不到填空)
        // devBrand（设备品牌，获取不到填空）
        // screenR（设备屏幕分辨率，获取不到填空）

        String lstrUdid = SystemUtil.getAndroidIMEI(moContext);
        String lstrDeviceType = "android";
        String lstrOsver = SystemUtil.getAndroidVersion();
        String lstrjailbreak = "";
        if (isRoot()) {
            lstrjailbreak = "y";
        } else {
            lstrjailbreak = "n";
        }

        // // 获取运营商
        String lstrtelcom = SystemUtil.getSimOperator(moContext);

        String lstrdevBrand = android.os.Build.MANUFACTURER;// 制造商

        // 获取手机分辨率 --- height * weight
        int widthPixels = SystemUtil.getScreenWidth(moContext);
        int heightPixels = SystemUtil.getScreenHeight(moContext);

        String lstrscreenR = String.format("%d*%d", heightPixels, widthPixels);
        String lstrDeviceProper = String
                .format("udid=%s&devicetype=%s&osVer=%s&jailbreak=%s&telcom=%s&devBrand=%s&screenR=%s",
                        lstrUdid, lstrDeviceType, lstrOsver, lstrjailbreak,
                        lstrtelcom, lstrdevBrand, lstrscreenR);
        return lstrDeviceProper;
    }

    /** =============================Activity======================= */
    /**
     * @param context
     * @param packageName
     * @return boolean
     * @description 判断当前的app(packageName)是否 显示。
     * @author yueliangrensheng create at 2015年4月1日下午2:01:33
     */
    @SuppressWarnings("deprecation")
    public static boolean isTopActivity(Context context, String packageName) {
        boolean result = false;
        if (context == null || "".equals(packageName) || packageName == null) {
            return result;
        }
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
            if (runningTasks != null && runningTasks.size() > 0) {
                if (runningTasks.get(0).topActivity.getPackageName().equals(
                        packageName)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 方法描述：清空应用的缓存数据
     *
     * @return
     * @author zhaishaoping
     * @time 21/07/2017 4:08 PM
     */
    public static void clearAppCacheData(Context context) {
        if (context == null) {
            return;
        }

        String packageName = context.getPackageName();
        String command = "pm clear " + packageName; // 或者： "pm clear" + packageName + "HERE";
        try {
            Runtime.getRuntime().exec(command);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法描述：判断设备是否安装 指定 packageName 的应用
     *
     * @return
     * @author zhaishaoping
     * @time 29/08/2017 5:03 PM
     */
    public static boolean isClientInstalled(Context context, String packageName) {

        if (context == null || TextUtils.isEmpty(packageName)) {
            return false;
        }

        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                PackageInfo packageInfo = pinfo.get(i);
                if (packageInfo == null) {
                    continue;
                }
                String pn = packageInfo.packageName;
                if (packageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回系统 默认分配的最大内存
     *
     * @return
     */
    public static long getSysMaxMemory() {
        long maxMemory = 0l;
        Runtime runtime = Runtime.getRuntime();
        maxMemory = runtime.maxMemory();
        return maxMemory;
    }


}
