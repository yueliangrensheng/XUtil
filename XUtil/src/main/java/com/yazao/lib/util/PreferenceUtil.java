package com.yazao.lib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 类描述：sharedPreference管理类
 *
 * @author zhaishaoping
 * @data 14/04/2017 3:05 PM
 */
public class PreferenceUtil {

    private static Context mContext;
    private static String SP_NAME;

    public static void init(Context context, String spName) {
        mContext = context.getApplicationContext();
        SP_NAME = spName;
    }


    private static SharedPreferences getSharedPreference() {
        if (mContext == null) {
            throw new RuntimeException("context = null, please init() first");
        }
        if (TextUtils.isEmpty(SP_NAME)) {
            throw new RuntimeException("SharedPreferences's name is empty");
        }
        return mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor() {
        SharedPreferences sp = getSharedPreference();
        return sp.edit();
    }

    public static void putString(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            String encrypt = value;
            if (!TextUtils.isEmpty(value)) {
                encrypt = value; //JniAlgorithm.encode(value);//TODO 对数据加密
            }
            SharedPreferences.Editor editor = getEditor();
            editor.putString(key, encrypt);
            editor.commit();
        }
    }

    /**
     * 得到配置数据，增加了解密的过程
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(String key, String defaultValue) {
        String origin = getSharedPreference().getString(key, null);
        if (!TextUtils.isEmpty(origin)) {
            return origin; //JniAlgorithm.decrypt(origin);// TODO 可以返回解密数据
        }
        return defaultValue;
    }

    public static String getString(String key) {
        return getString(key, "");
    }


    public static void putInt(String key, int value) {
        putString(key, value + "");
    }

    public static void putLong(String key, long value) {
        putString(key, value + "");
    }

    public static void putBoolean(String key, boolean value) {
        putString(key, value + "");
    }

    public static void remove(String... keys) {
        SharedPreferences.Editor editor = getEditor();
        for (String key : keys) {
            if (!TextUtils.isEmpty(key)) {
                editor.remove(key);
            }
        }
        editor.commit();
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static long getLong(String key) {
        String origin = getString(key);
        if (!TextUtils.isEmpty(origin) && TextUtils.isDigitsOnly(origin)) {
            return Long.valueOf(origin);
        }
        return 0L;
    }

    public static int getInt(String key, int defaultValue) {
        String origin = getString(key);
        if (!TextUtils.isEmpty(origin) && TextUtils.isDigitsOnly(origin)) {
            return Integer.valueOf(origin);
        }
        return defaultValue;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String origin = getString(key);
        if (!TextUtils.isEmpty(origin)) {
            return Boolean.valueOf(origin);
        }
        return defaultValue;
    }

    public static boolean getBoolean(String key) {
        String origin = getString(key);
        if (!TextUtils.isEmpty(origin)) {
            return Boolean.valueOf(origin);
        }
        return false;
    }

    public static void putParcelable(String key, Parcelable value) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            putString(key, temp);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void putSerializable(String key, Serializable value) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            putString(key, temp);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static Serializable getSerializable(String key) {
        String origin = getString(key);
        if (!TextUtils.isEmpty(origin)) {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(origin.getBytes(), Base64.DEFAULT));
            try {
                ObjectInputStream ois = new ObjectInputStream(bais);
                return (Serializable) ois.readObject();
            } catch (Exception e) {
            }

        }
        return null;
    }
}
