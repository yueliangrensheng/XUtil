package com.yazao.lib.util;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;

import com.yazao.lib.util.click.ClickUtil;
import com.yazao.lib.util.demo.R;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean isOpenKeyboard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = findViewById(R.id.edit_text);
        //进入页面自动打开软键盘
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();


        //手动操作软键盘 打开、隐藏
        findViewById(R.id.btn_open_keyboard).setOnClickListener((view) -> {

            if (isOpenKeyboard) {
//                editText.setFocusable(false);
//                editText.setFocusableInTouchMode(false);
                editText.clearFocus();
                HideSoftInputUtil.getInstance().hideSoftInput(editText);
            } else {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                HideSoftInputUtil.getInstance().openSoftInput(editText);
            }
            isOpenKeyboard = !isOpenKeyboard;

        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (HideSoftInputUtil.getInstance().isShouldHideInput(this, ev)) {
                HideSoftInputUtil.getInstance().hideSoftInput(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
