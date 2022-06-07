package com.leo.uilib.popup.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

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
    }

//    @Override
//    public void onNavigationBarChange(boolean show) {
//        if (!show) {
//            applyFull();
//            getPopupContentView().setPadding(0, 0, 0, 0);
//        } else {
//            applySize(true);
//        }
//    }

//    @Override
//    protected void applySize(boolean isShowNavBar) {
//        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
//        View contentView = getPopupContentView();
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) contentView.getLayoutParams();
//        params.gravity = Gravity.TOP;
//        contentView.setLayoutParams(params);
//
//        int actualNabBarHeight = isShowNavBar || PopupUtils.isNavBarVisible(getContext()) ? PopupUtils.getNavBarHeight() : 0;
//        if (rotation == 0) {
//            contentView.setPadding(contentView.getPaddingLeft(), contentView.getPaddingTop(), contentView.getPaddingRight(),
//                    actualNabBarHeight);
//        } else if (rotation == 1 || rotation == 3) {
//            contentView.setPadding(contentView.getPaddingLeft(), contentView.getPaddingTop(), contentView.getPaddingRight(), 0);
//        }
//    }

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
        return new TranslateAnimator(getPopupContentView(),getAnimationDuration(), PopupAnimation.TranslateFromBottom);
    }
}
