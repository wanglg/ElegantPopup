package com.leo.uilib.popup.impl;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.leo.uilib.popup.R;
import com.leo.uilib.popup.animator.PopupAnimator;
import com.leo.uilib.popup.animator.ScrollScaleAnimator;
import com.leo.uilib.popup.enums.PopupAnimation;
import com.leo.uilib.popup.enums.PopupPosition;
import com.leo.uilib.popup.util.PopupUtils;
import com.leo.uilib.popup.widget.PartShadowContainer;

public abstract class AttachPopupView extends BasePopupView {
    protected int defaultOffsetY = 0;
    protected int defaultOffsetX = 0;
    protected PartShadowContainer attachPopupContainer;

    public AttachPopupView(@NonNull Context context) {
        super(context);
        attachPopupContainer = findViewById(R.id.attachPopupContainer);

        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), attachPopupContainer, false);
        attachPopupContainer.addView(contentView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.elegant_popup_attach_popup_view;
    }

    public boolean isShowUp;
    boolean isShowLeft;
    protected int bgDrawableMargin = 6;

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        if (popupInfo.getAtView() == null && popupInfo.touchPoint == null) {
            throw new IllegalArgumentException("atView() or touchPoint must not be null for AttachPopupView ！");
        }

        defaultOffsetY = popupInfo.offsetY == 0 ? PopupUtils.dp2px(getContext(), 4) : popupInfo.offsetY;
        defaultOffsetX = popupInfo.offsetX == 0 ? PopupUtils.dp2px(getContext(), 0) : popupInfo.offsetX;

        attachPopupContainer.setTranslationX(popupInfo.offsetX);
        attachPopupContainer.setTranslationY(popupInfo.offsetY);
        if (!popupInfo.hasShadowBg) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ////优先使用implView的背景
                if (getPopupBackground() == null) {
                    attachPopupContainer.setBackgroundColor(Color.WHITE);
                } else {
                    attachPopupContainer.setBackgroundDrawable(getPopupBackground());
                }
                attachPopupContainer.setElevation(PopupUtils.dp2px(getContext(), 10));
            } else {
                //优先使用implView的背景
                if (getPopupImplView().getBackground() == null) {
                    defaultOffsetX -= bgDrawableMargin;
                    defaultOffsetY -= bgDrawableMargin;
                    attachPopupContainer.setBackgroundResource(R.drawable._xpopup_shadow);
                } else {
                    attachPopupContainer.setBackgroundDrawable(getPopupBackground());
                }
            }
        }
        PopupUtils.applyPopupSize((ViewGroup) getPopupContentView(), getMaxWidth(), getMaxHeight(), new Runnable() {
            @Override
            public void run() {
                doAttach();
            }
        });

    }

    /**
     * 执行倚靠逻辑
     */
    float translationX = 0, translationY = 0;
    // 弹窗显示的位置不能超越Window高度
    float maxY = PopupUtils.getWindowHeight(getContext());
    float maxX = 0; // 显示在右边时候的最大值

    protected void doAttach() {
        //0. 判断是依附于某个点还是某个View
        if (popupInfo.touchPoint != null) {
            // 依附于指定点
            maxX = Math.max(popupInfo.touchPoint.x - getPopupContentView().getMeasuredWidth(), 0);
            // 尽量优先放在下方，当不够的时候在显示在上方
            //假设下方放不下，超出window高度
            boolean isTallerThanWindowHeight = (popupInfo.touchPoint.y + getPopupContentView().getMeasuredHeight()) > maxY;
            if (isTallerThanWindowHeight) {
                isShowUp = popupInfo.touchPoint.y > PopupUtils.getWindowHeight(getContext()) / 2;
            } else {
                isShowUp = false;
            }
            isShowLeft = popupInfo.touchPoint.x < PopupUtils.getWindowWidth(getContext()) / 2;

            //修正高度，弹窗的高有可能超出window区域
            if (isShowUpToTarget()) {
                if (getPopupContentView().getMeasuredHeight() > popupInfo.touchPoint.y) {
                    ViewGroup.LayoutParams params = getPopupContentView().getLayoutParams();
                    params.height = (int) (popupInfo.touchPoint.y - PopupUtils.getStatusBarHeight());
                    getPopupContentView().setLayoutParams(params);
                }
            } else {
                if (getPopupContentView().getMeasuredHeight() + popupInfo.touchPoint.y > PopupUtils.getWindowHeight(getContext())) {
                    ViewGroup.LayoutParams params = getPopupContentView().getLayoutParams();
                    params.height = (int) (PopupUtils.getWindowHeight(getContext()) - popupInfo.touchPoint.y);
                    getPopupContentView().setLayoutParams(params);
                }
            }

            getPopupContentView().post(new Runnable() {
                @Override
                public void run() {
                    translationX = (isShowLeft ? popupInfo.touchPoint.x : maxX) + (isShowLeft ? defaultOffsetX : -defaultOffsetX);
                    if (popupInfo.isCenterHorizontal) {
                        //水平居中
                        if (isShowLeft) {
                            translationX -= getPopupContentView().getMeasuredWidth() / 2f;
                        } else {
                            translationX += getPopupContentView().getMeasuredWidth() / 2f;
                        }
                    }
                    if (isShowUpToTarget()) {
                        // 应显示在point上方
                        // translationX: 在左边就和atView左边对齐，在右边就和其右边对齐
                        translationY = popupInfo.touchPoint.y - getPopupContentView().getMeasuredHeight() - defaultOffsetY;
                    } else {
                        translationY = popupInfo.touchPoint.y + defaultOffsetY;
                    }
                    getPopupContentView().setTranslationX(translationX);
                    getPopupContentView().setTranslationY(translationY);
                }
            });

        } else {
            // 依附于指定View
            //1. 获取atView在屏幕上的位置
            int[] locations = new int[2];
            popupInfo.getAtView().getLocationOnScreen(locations);
            final Rect rect = new Rect(locations[0], locations[1], locations[0] + popupInfo.getAtView().getMeasuredWidth(),
                    locations[1] + popupInfo.getAtView().getMeasuredHeight());

            maxX = Math.max(rect.right - getPopupContentView().getMeasuredWidth(), 0);
            int centerX = (rect.left + rect.right) / 2;

            // 尽量优先放在下方，当不够的时候在显示在上方
            //假设下方放不下，超出window高度
            boolean isTallerThanWindowHeight = (rect.bottom + getPopupContentView().getMeasuredHeight()) > maxY;
            if (isTallerThanWindowHeight) {
                int centerY = (rect.top + rect.bottom) / 2;
                isShowUp = centerY > PopupUtils.getWindowHeight(getContext()) / 2;
            } else {
                isShowUp = false;
            }
            isShowLeft = centerX < PopupUtils.getWindowWidth(getContext()) / 2;

            //修正高度，弹窗的高有可能超出window区域
            if (isShowUpToTarget()) {
                if (getPopupContentView().getMeasuredHeight() > rect.top) {
                    ViewGroup.LayoutParams params = getPopupContentView().getLayoutParams();
                    params.height = rect.top - PopupUtils.getStatusBarHeight();
                    getPopupContentView().setLayoutParams(params);
                }
            } else {
                if (getPopupContentView().getMeasuredHeight() + rect.bottom > PopupUtils.getWindowHeight(getContext())) {
                    ViewGroup.LayoutParams params = getPopupContentView().getLayoutParams();
                    params.height = PopupUtils.getWindowHeight(getContext()) - rect.bottom;
                    getPopupContentView().setLayoutParams(params);
                }
            }

            getPopupContentView().post(() -> {
                translationX = (isShowLeft ? rect.left : maxX) + (isShowLeft ? defaultOffsetX : -defaultOffsetX);
                if (popupInfo.isCenterHorizontal) {
                    //水平居中
                    if (isShowLeft) {
                        translationX += (rect.width() - getPopupContentView().getMeasuredWidth()) / 2f;
                    } else {
                        translationX -= (rect.width() - getPopupContentView().getMeasuredWidth()) / 2f;
                    }
                }
                if (isShowUpToTarget()) {
                    //说明上面的空间比较大，应显示在atView上方
                    // translationX: 在左边就和atView左边对齐，在右边就和其右边对齐
                    translationY = rect.top - getPopupContentView().getMeasuredHeight() - defaultOffsetY;
                } else {
                    translationY = rect.bottom + defaultOffsetY;
                }
                getPopupContentView().setTranslationX(translationX);
                getPopupContentView().setTranslationY(translationY);
            });

        }
    }

    protected boolean isShowUpToTarget() {
        return (isShowUp || popupInfo.popupPosition == PopupPosition.Top)
                && popupInfo.popupPosition != PopupPosition.Bottom;
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        PopupAnimator animator;
        if (isShowUpToTarget()) {
            // 在上方展示
            if (isShowLeft) {
                animator = new ScrollScaleAnimator(getPopupContentView(), getAnimationDuration(), PopupAnimation.ScrollAlphaFromLeftBottom);
            } else {
                animator = new ScrollScaleAnimator(getPopupContentView(), getAnimationDuration(), PopupAnimation.ScrollAlphaFromRightBottom);
            }
        } else {
            // 在下方展示
            if (isShowLeft) {
                animator = new ScrollScaleAnimator(getPopupContentView(), getAnimationDuration(), PopupAnimation.ScrollAlphaFromLeftTop);
            } else {
                animator = new ScrollScaleAnimator(getPopupContentView(), getAnimationDuration(), PopupAnimation.ScrollAlphaFromRightTop);
            }
        }
        return animator;
    }

    /**
     * 如果Attach弹窗的子类想自定义弹窗的背景，不能去直接给布局设置背景，那样效果不好；需要实现这个方法返回一个Drawable
     *
     * @return
     */
    protected Drawable getPopupBackground() {
        return null;
    }

}
