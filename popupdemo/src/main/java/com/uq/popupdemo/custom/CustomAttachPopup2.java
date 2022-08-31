package com.uq.popupdemo.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.leo.uilib.popup.impl.AttachPopupView;
import com.uq.popupdemo.R;


public class CustomAttachPopup2 extends AttachPopupView {
    public CustomAttachPopup2(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_attach_popup2;
    }

    @Override
    public void configViews() {
        super.configViews();
        findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });
    }

    //如果要自定义弹窗的背景，不要给布局设置背景图片，重写这个方法返回一个Drawable即可
    @Override
    protected Drawable getPopupBackground() {
        return getResources().getDrawable(R.drawable.shadow_bg);
    }

}
