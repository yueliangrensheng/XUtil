package com.yazao.lib.util.click;

import android.os.SystemClock;
import android.view.View;

/**
 * 类描述：点击事件处理 工具类
 *
 * @author zhaishaoping
 * @data 2018/9/10 1:31 PM
 */

public class ClickUtil {

    //这里修改工具类为普通类。防止单例实例引用实例导致的内存泄漏。
//    private static class SingleHolder {
//        private static final ClickUtil ourInstance = new ClickUtil();
//    }
//
//    public static ClickUtil getInstance() {
//        return SingleHolder.ourInstance;
//    }

    public ClickUtil() {
    }

    long[] mHints = null;
    private OnClickListener mClickListener;

    public void setOnClickListener(OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    /**
     * <p>多次Click，至少2次click</p>
     *
     * <p>
     * 思想是： 1。创建一个 3维数组，用来存储 点击的时间
     *          * 2。每次点击，都会将 数组中的元素 向前移动一位，然后再对比数组中的第一个元素 和 此时的时间，
     *          * 满足条件的 触发 click事件
     * </p>
     */
    public void click(View view, int clickCount, int position, OnClickListener listener) {
        if (clickCount <= 1) {
            clickCount = 2;
        }

        if (listener != null) {
            mClickListener = listener;
        }

        if (mHints == null) {
            mHints = new long[clickCount];
        }

        System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
        mHints[mHints.length - 1] = SystemClock.uptimeMillis();

        if (mHints[0] >= (SystemClock.uptimeMillis() - 500)) {

            //事件 回调
            if (mClickListener != null) {
                mClickListener.onClick(view, position);
            }

            //重置数组中的数据
            for (int i = 0; i < clickCount; i++) {
                mHints[i] = 0;
            }
        }
    }
}