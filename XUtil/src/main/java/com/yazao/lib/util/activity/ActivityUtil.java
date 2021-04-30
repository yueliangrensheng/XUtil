package com.yazao.lib.util.activity;

import android.app.Activity;
import android.content.Intent;

import com.yazao.lib.util.R;

public class ActivityUtil {

    public void startActivity(Activity context, Class clazz) {
        startActivityWithAnim(context, clazz, R.anim.yz_anim_in_right, R.anim.yz_anim_out_left);
    }

    public void startActivityWithAnim(Activity context, Class clazz, int enterAnim, int exitAnim) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, clazz);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.overridePendingTransition(enterAnim, exitAnim);
    }

    public void finishActivity(Activity context) {
        context.finish();
        context.overridePendingTransition(R.anim.yz_anim_in_left, R.anim.yz_anim_out_right);
    }

    private static class SingleHolder {
        private static final ActivityUtil ourInstance = new ActivityUtil();
    }

    public static ActivityUtil getInstance() {
        return ActivityUtil.SingleHolder.ourInstance;
    }

    private ActivityUtil() {
    }

}
