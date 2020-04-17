package com.leo.uilib.popup.impl;

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
import com.leo.uilib.popup.util.XPopupUtils;

import static com.leo.uilib.popup.enums.PopupAnimation.ScaleAlphaFromCenter;


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
        XPopupUtils.applyPopupSize((ViewGroup) getPopupContentView(), getMaxWidth(), getMaxHeight(), () -> {
            if (popupInfo.isCenterHorizontal) {
                float left = (XPopupUtils.getWindowWidth(getContext()) - attachPopupContainer.getMeasuredWidth()) / 2f;
                attachPopupContainer.setTranslationX(left);
            } else {
                attachPopupContainer.setTranslationX(popupInfo.offsetX);
            }
            attachPopupContainer.setTranslationY(popupInfo.offsetY);
        });

        if (popupInfo != null) {
            LayoutParams vl = (LayoutParams) attachPopupContainer.getLayoutParams();
            if (popupInfo.popupPosition == PopupPosition.Bottom) {
                vl.gravity = Gravity.BOTTOM;
            } else if (popupInfo.popupPosition == PopupPosition.Top_Right) {
                vl.gravity = Gravity.END | Gravity.TOP;
            } else if (popupInfo.popupPosition == PopupPosition.Bottom_Right) {
                vl.gravity = Gravity.END | Gravity.BOTTOM;
            }
        }
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new ScrollScaleAnimator(getPopupContentView(), ScaleAlphaFromCenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout._xpopup_position_popup_view;
    }
}
