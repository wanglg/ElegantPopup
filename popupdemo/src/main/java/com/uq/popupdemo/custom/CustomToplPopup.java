package com.uq.popupdemo.custom;

import android.content.Context;

import androidx.annotation.NonNull;

import com.leo.uilib.popup.animator.PopupAnimator;
import com.leo.uilib.popup.animator.TranslateAnimator;
import com.leo.uilib.popup.enums.PopupAnimation;
import com.leo.uilib.popup.impl.PositionPopupView;
import com.uq.popupdemo.R;


/**
 * @Author: wangliugeng
 * @Date : 2020/4/15
 * @Email: leo3552@163.com
 * @Desciption:
 */
public class CustomToplPopup extends PositionPopupView {
    public CustomToplPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_qq_msg;
    }

    @Override
    public void configViews() {
        super.configViews();
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new TranslateAnimator(getPopupContentView(), getAnimationDuration(), PopupAnimation.TranslateFromRight);
    }
}
