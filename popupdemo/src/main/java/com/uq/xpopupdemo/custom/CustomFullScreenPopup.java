package com.uq.xpopupdemo.custom;

import android.content.Context;

import androidx.annotation.NonNull;

import com.leo.uilib.popup.impl.FullScreenPopupView;
import com.uq.xpopupdemo.R;

/**
 * Description:
 * Create by lxj, at 2019/3/12
 */
public class CustomFullScreenPopup extends FullScreenPopupView {
    public CustomFullScreenPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_fullscreen_popup;
    }

    @Override
    public int getAnimationDuration() {
        return 500;
    }
}
