package com.uq.xpopupdemo.custom;

import android.content.Context;

import com.leo.uilib.popup.impl.dialog.FullScreenPopupDialog;
import com.uq.xpopupdemo.R;

/**
 * @Author: wangliugeng
 * @Date : 2020/4/14
 * @Email: leo3552@163.com
 * @Desciption:
 */
public class CustomFullScreenDialog extends FullScreenPopupDialog {
    public CustomFullScreenDialog(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.custom_fullscreen_popup;
    }
}
