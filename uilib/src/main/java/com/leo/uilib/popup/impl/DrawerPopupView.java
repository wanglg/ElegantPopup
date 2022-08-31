package com.leo.uilib.popup.impl;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.leo.uilib.popup.R;
import com.leo.uilib.popup.enums.PopupPosition;
import com.leo.uilib.popup.util.PopupUtils;
import com.leo.uilib.popup.widget.PopupDrawerLayout;


/**
 * Description: 带Drawer的弹窗
 * Create by dance, at 2018/12/20
 */
public abstract class DrawerPopupView extends BasePopupView {
    PopupDrawerLayout drawerLayout;
    protected FrameLayout drawerContentContainer;
    float mFraction = 0f;

    public DrawerPopupView(@NonNull Context context) {
        super(context);
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerContentContainer = findViewById(R.id.drawerContentContainer);
        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), drawerContentContainer, false);
        drawerContentContainer.addView(contentView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.elegant_popup_drawer_popup_view;
    }

    Paint paint = new Paint();
    Rect shadowRect;
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    int currColor = Color.TRANSPARENT;
    int defaultColor = Color.TRANSPARENT;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (popupInfo != null && popupInfo.hasStatusBarShadow) {
            if (shadowRect == null) {
                shadowRect = new Rect(0, 0, getMeasuredWidth(), PopupUtils.getStatusBarHeight());
            }
            paint.setColor((Integer) argbEvaluator.evaluate(mFraction, defaultColor, getStatusBarBgColor()));
            canvas.drawRect(shadowRect, paint);
        }
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        drawerLayout.isDismissOnTouchOutside = popupInfo.isDismissOnTouchOutside;
        drawerLayout.setOnCloseListener(new PopupDrawerLayout.OnCloseListener() {
            @Override
            public void onClose() {
                DrawerPopupView.super.dismiss();
            }

            @Override
            public void onOpen() {
                DrawerPopupView.super.doAfterShow();
            }

            @Override
            public void onDrag(int x, float fraction, boolean isToLeft) {
                if (popupInfo == null) {
                    return;
                }
                mFraction = fraction;
                if (popupInfo.hasShadowBg) {
                    shadowBgAnimator.applyColorValue(fraction);
                }
                postInvalidate();
            }
        });
        getPopupImplView().setTranslationX(popupInfo.offsetX);
        getPopupImplView().setTranslationY(popupInfo.offsetY);
        drawerLayout.setDrawerPosition(popupInfo.popupPosition == null ? PopupPosition.Left : popupInfo.popupPosition);
        drawerLayout.setOnClickListener(v -> drawerLayout.close());
    }

    @Override
    protected void doAfterShow() {
        //do nothing self.
    }

    @Override
    public void doShowAnimation() {
        drawerLayout.open();
    }

    @Override
    public void doDismissAnimation() {
        drawerLayout.close();
    }

    /**
     * 动画是跟随手势发生的，所以不需要额外的动画器，因此动画时间也清零
     *
     * @return
     */
    @Override
    public int getAnimationDuration() {
        return 0;
    }

    @Override
    public void dismiss() {
        // 关闭Drawer，由于Drawer注册了关闭监听，会自动调用dismiss
        drawerLayout.close();
    }

    @Override
    protected View getTargetSizeView() {
        return getPopupImplView();
    }
}
