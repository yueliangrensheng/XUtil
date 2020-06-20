package com.yazao.lib.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

/**
 * 类描述：TextView | EditText 相关工具类
 *
 * @author zhaishaoping
 * @data 2019/5/14 4:20 PM
 */

public class TextViewUtil {
    private static class SingleHolder {
        private static final TextViewUtil ourInstance = new TextViewUtil();
    }

    public static TextViewUtil getInstance() {
        return SingleHolder.ourInstance;
    }

    private TextViewUtil() {
    }

    /**
     * set TextView or EditText 's hint text size
     *
     * @param hint
     * @param hintSize sp
     * @return
     */
    public SpannableString setTextViewHintSize(String hint, int hintSize) {
        try {
            SpannableString spannedString = new SpannableString(hint);
            AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(hintSize, true);
            spannedString.setSpan(absoluteSizeSpan, 0, hint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return new SpannableString(spannedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SpannableString(hint);
    }
}