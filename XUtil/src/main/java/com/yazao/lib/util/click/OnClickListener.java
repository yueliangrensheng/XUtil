package com.yazao.lib.util.click;

import android.view.View;

/**
 * 类描述：自定义 Click 接口
 *
 * @author zhaishaoping
 * @data 2018/9/10 1:34 PM
 */
public interface OnClickListener {
    /**
     * @Description Click事件回调方法： position为view在其父类中的角标位置
     */
    public void onClick(View view, int position);
}
