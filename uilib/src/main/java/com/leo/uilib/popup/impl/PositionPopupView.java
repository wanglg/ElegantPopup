package com.leo.uilib.popup.impl;

import static com.leo.uilib.popup.enums.PopupAnimation.ScaleAlphaFromCenter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.leo.uilib.popup.R;
import com.leo.uilib.popup.animator.PopupAnimator;
import com.leo.uilib.popup.animator.ScrollScaleAnimator;
import com.leo.uilib.popup.enums.PopupPosition;
import com.leo.uilib.popup.util.PopupUtils;


public class PositionPopupView extends BasePopupView {
    ViewGroup attachPopupContainer;

    public PositionPopupView(@NonNull Context context) {
        super(context);
        attachPopupContainer = findViewById(R.id.popupContainer);
        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), attachPopupContainer, false);
        attachPopupContainer.addView(contentView);
    }


    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        PopupUtils.applyPopupSize((ViewGroup) getPopupContentView(), getMaxWidth(), getMaxHeight());
        if (popupInfo != null) {
            LayoutParams vl = (LayoutParams) attachPopupContainer.getLayoutParams();
            if (popupInfo.popupPosition == PopupPosition.Bottom) {
                vl.gravity = Gravity.BOTTOM;
                vl.bottomMargin = popupInfo.offsetY;
                vl.leftMargin = popupInfo.offsetX;
            } else if (popupInfo.popupPosition == PopupPosition.Top_Right) {
                vl.gravity = Gravity.END | Gravity.TOP;
                vl.topMargin = popupInfo.offsetY;
                vl.rightMargin = popupInfo.offsetX;
            } else if (popupInfo.popupPosition == PopupPosition.Bottom_Right) {
                vl.gravity = Gravity.END | Gravity.BOTTOM;
                vl.bottomMargin = popupInfo.offsetY;
                vl.rightMargin = popupInfo.offsetX;
            }
        }
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new ScrollScaleAnimator(getPopupContentView(), getAnimationDuration(), ScaleAlphaFromCenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.elegant_popup_position_popup_view;
    }
}
