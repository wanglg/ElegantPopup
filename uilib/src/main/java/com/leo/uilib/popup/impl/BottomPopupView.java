package com.leo.uilib.popup.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.leo.uilib.popup.R;
import com.leo.uilib.popup.animator.PopupAnimator;
import com.leo.uilib.popup.animator.TranslateAnimator;
import com.leo.uilib.popup.enums.PopupAnimation;
import com.leo.uilib.popup.enums.PopupStatus;
import com.leo.uilib.popup.util.KeyboardUtils;
import com.leo.uilib.popup.util.PopupUtils;
import com.leo.uilib.popup.widget.SmartDragLayout;


public class BottomPopupView extends BasePopupView {
    protected SmartDragLayout bottomPopupContainer;

    public BottomPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.elegant_popup_bottom_popup_view;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        bottomPopupContainer = findViewById(R.id.bottomPopupContainer);
        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), bottomPopupContainer, false);
        bottomPopupContainer.addView(contentView);
        bottomPopupContainer.enableDrag(popupInfo.enableDrag);
        bottomPopupContainer.dismissOnTouchOutside(popupInfo.isDismissOnTouchOutside);
        if (popupInfo.enableDrag) {
            popupInfo.popupAnimation = null;
            getPopupImplView().setTranslationX(popupInfo.offsetX);
            getPopupImplView().setTranslationY(popupInfo.offsetY);
        } else {
            getPopupContentView().setTranslationX(popupInfo.offsetX);
            getPopupContentView().setTranslationY(popupInfo.offsetY);
        }

        PopupUtils.applyPopupSize(popupInfo.enableDrag ? (ViewGroup) contentView : (ViewGroup) getPopupContentView(), getMaxWidth(), getMaxHeight());

        bottomPopupContainer.setOnCloseListener(new SmartDragLayout.OnCloseListener() {
            @Override
            public void onClose() {
                doAfterDismiss();
            }

            @Override
            public void onOpen() {
            }

            @Override
            public void onDrag(int y, float percent, boolean isScrollUp) {
                if (popupInfo == null) {
                    return;
                }
                if (popupInfo.hasShadowBg && shadowBgAnimator != null) {
                    setBackgroundColor(shadowBgAnimator.calculateBgColor(percent));
                }
            }
        });

        bottomPopupContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupInfo.isDismissOnTouchOutside) {
                    dismiss();
                }
            }
        });
    }

    @Override
    public void doShowAnimation() {
        if (popupInfo == null) {
            return;
        }
        if (popupInfo.enableDrag) {
            bottomPopupContainer.open();
        } else {
            super.doShowAnimation();
        }
    }

    @Override
    public void doDismissAnimation() {
        if (popupInfo.enableDrag) {
            bottomPopupContainer.close();
        } else {
            super.doDismissAnimation();
        }
    }

    /**
     * 动画是跟随手势发生的，所以不需要额外的动画器，因此动画时间也清零
     *
     * @return
     */
    @Override
    public int getAnimationDuration() {
        return popupInfo.enableDrag ? 0 : super.getAnimationDuration();
    }

    private TranslateAnimator translateAnimator;

    @Override
    protected PopupAnimator getPopupAnimator() {
        if (popupInfo == null) {
            return null;
        }
        if (translateAnimator == null) {
            translateAnimator = new TranslateAnimator(getPopupContentView(), getAnimationDuration(),
                    PopupAnimation.TranslateFromBottom);
        }
        return popupInfo.enableDrag ? null : translateAnimator;
    }

    @Override
    public void dismiss() {
        if (popupInfo.enableDrag) {
            if (popupStatus == PopupStatus.Dismissing) {
                return;
            }
            popupStatus = PopupStatus.Dismissing;
            if (popupInfo.autoOpenSoftInput) {
                KeyboardUtils.hideSoftInput(this);
            }
            clearFocus();
            // 关闭Drawer，由于Drawer注册了关闭监听，会自动调用dismiss
            bottomPopupContainer.close();
        } else {
            super.dismiss();
        }
    }

    /**
     * 具体实现的类的布局
     *
     * @return
     */
    @Override
    protected int getImplLayoutId() {
        return 0;
    }

    @Override
    protected int getMaxWidth() {
        return popupInfo.maxWidth == 0 ? PopupUtils.getWindowWidth(getContext())
                : popupInfo.maxWidth;
    }

    @Override
    protected View getTargetSizeView() {
        return getPopupImplView();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (popupInfo != null && !popupInfo.enableDrag && translateAnimator != null) {
            getPopupContentView().setTranslationX(translateAnimator.startTranslationX);
            getPopupContentView().setTranslationY(translateAnimator.startTranslationY);
            translateAnimator.hasInit = true;
        }
        super.onDetachedFromWindow();
    }
}
