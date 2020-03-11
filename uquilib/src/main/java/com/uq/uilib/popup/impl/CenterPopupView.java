package com.uq.uilib.popup.impl;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.jifen.uquilib.R;
import com.uq.uilib.popup.animator.PopupAnimator;
import com.uq.uilib.popup.animator.ScaleAlphaAnimator;
import com.uq.uilib.popup.util.XPopupUtils;
import com.uq.uilib.popup.enums.PopupAnimation;


/**
 * Description: 在中间显示的Popup
 * Create by dance, at 2018/12/8
 */
public class CenterPopupView extends BasePopupView {
    protected FrameLayout centerPopupContainer;
    protected int bindLayoutId;
    protected int bindItemLayoutId;

    public CenterPopupView(@NonNull Context context) {
        super(context);
        centerPopupContainer = findViewById(R.id.centerPopupContainer);
    }

    @Override
    protected int getPopupLayoutId() {
        return R.layout._xpopup_center_popup_view;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), centerPopupContainer, false);
        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        centerPopupContainer.addView(contentView, params);
        getPopupContentView().setTranslationX(popupInfo.offsetX);
        getPopupContentView().setTranslationY(popupInfo.offsetY);
        XPopupUtils.applyPopupSize((ViewGroup) getPopupContentView(), getMaxWidth(), getMaxHeight());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setTranslationY(0);
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
        return popupInfo.maxWidth == 0 ? (int) (XPopupUtils.getWindowWidth(getContext()) * 0.86f)
                : popupInfo.maxWidth;
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new ScaleAlphaAnimator(getPopupContentView(), PopupAnimation.ScaleAlphaFromCenter);
    }
}
