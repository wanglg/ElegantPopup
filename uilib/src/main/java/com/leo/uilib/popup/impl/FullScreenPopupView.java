package com.leo.uilib.popup.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.leo.uilib.popup.Popup;
import com.leo.uilib.popup.animator.PopupAnimator;
import com.leo.uilib.popup.animator.TranslateAnimator;
import com.leo.uilib.popup.enums.PopupAnimation;
import com.leo.uilib.popup.util.PopupUtils;


public class FullScreenPopupView extends CenterPopupView {
    public FullScreenPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getMaxWidth() {
        return 0;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        popupInfo.hasShadowBg = false;
        applySize();
    }


    protected void applySize() {
        View contentView = getPopupContentView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) contentView.getLayoutParams();
        params.gravity = Gravity.TOP;
        contentView.setLayoutParams(params);
    }

    Paint paint = new Paint();
    Rect shadowRect;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (popupInfo.hasStatusBarShadow) {
            paint.setColor(Popup.statusBarShadowColor);
            shadowRect = new Rect(0, 0, PopupUtils.getWindowWidth(getContext()), PopupUtils.getStatusBarHeight());
            canvas.drawRect(shadowRect, paint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paint = null;
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new TranslateAnimator(getPopupContentView(), getAnimationDuration(), PopupAnimation.TranslateFromBottom);
    }
}
