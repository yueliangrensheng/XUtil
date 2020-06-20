package com.yazao.lib.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by zhaishaoping on 29/03/2017.
 */

public class FileUtil {
    private FileUtil() {
    }

    public static File getCacheDir(Context context, String dirName) {
        File result;
        if (existsSdcard().booleanValue()) {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir == null) {
                result = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/cache/" + dirName);
            } else {
                result = new File(cacheDir, dirName);
            }
        } else {
            result = new File(context.getCacheDir(), dirName);
        }

        return !result.exists() && !result.mkdirs() ? null : result;
    }

    public static boolean isDiskAvailable() {
        long size = getDiskAvailableSize();
        return size > 10485760L;
    }

    public static long getDiskAvailableSize() {
        if (!existsSdcard().booleanValue()) {
            return 0L;
        } else {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getAbsolutePath());
            long blockSize = (long) stat.getBlockSize();
            long availableBlocks = (long) stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
    }

    public static Boolean existsSdcard() {
        return Boolean.valueOf(Environment.getExternalStorageState().equals("mounted"));
    }

    public static long getFileOrDirSize(File file) {
        if (!file.exists()) {
            return 0L;
        } else if (!file.isDirectory()) {
            return file.length();
        } else {
            long length = 0L;
            File[] list = file.listFiles();
            if (list != null) {
                File[] var4 = list;
                int var5 = list.length;

                for (int var6 = 0; var6 < var5; ++var6) {
                    File item = var4[var6];
                    length += getFileOrDirSize(item);
                }
            }

            return length;
        }
    }

    public static String getFilePath(Activity activity) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + activity.getPackageName() + File.separator + "cache" + File.separator;
    }

    public static String getFilePath(Activity activity, String fileName) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + activity.getPackageName() + File.separator+ "cache" + File.separator + fileName + File.separator;
    }

    public static String getFilePath(Context activity, String fileName) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + activity.getPackageName() + File.separator + "cache" + File.separator+ fileName + File.separator;
    }

    public static boolean isExists(File file) {
        return file.exists();
    }

    public static File newFile(Activity activity) {
        String fileParentName = "Photos";
        File file = new File(getFilePath(activity, fileParentName) + System.currentTimeMillis() + ".png");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        return file;
    }

    public static File newFile(Activity activity, String fileName) {
        String fileParentName = "Photos";
        File file = new File(getFilePath(activity, fileParentName) + fileName);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        return file;
    }
}
