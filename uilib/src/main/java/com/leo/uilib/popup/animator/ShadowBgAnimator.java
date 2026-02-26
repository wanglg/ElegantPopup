package com.leo.uilib.popup.animator;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.leo.uilib.popup.Popup;
import com.leo.uilib.popup.util.PopupUtils;
import com.leo.uilib.popup.util.navbar.OnNavigationBarListener;


/**
 * Description: 背景Shadow动画器，负责执行半透明的渐入渐出动画
 * Create by dance, at 2018/12/9
 */
public class ShadowBgAnimator extends PopupAnimator implements OnNavigationBarListener {

    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    public int startColor = Color.TRANSPARENT;
    public int navigationBarColorStartColor = Color.TRANSPARENT;
    public boolean isZeroDuration = false;
    public int shadowColor;
    public Window window;
    public boolean isNavBarVisible;
    public boolean isNavBarFollow;

    public ShadowBgAnimator(View target, int animationDuration, int shadowColor, Window window, boolean isNavBarFollow) {
        super(target, animationDuration);
        this.shadowColor = shadowColor;
        this.window = window;
        this.isNavBarFollow = isNavBarFollow;
    }

    @Override
    public void initAnimator() {
        targetView.setBackgroundColor(startColor);
        if (isNavBarFollow && window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isNavBarVisible = PopupUtils.isNavBarVisible(window);
            navigationBarColorStartColor = window.getNavigationBarColor();
        }
    }

    @Override
    public void animateShow() {
        if (startColor == shadowColor) {
            return;
        }
        if (isNavBarFollow && window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isNavBarVisible = PopupUtils.isNavBarVisible(window);
            navigationBarColorStartColor = window.getNavigationBarColor();
        }
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, startColor, shadowColor);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                targetView.setBackgroundColor((Integer) animation.getAnimatedValue());
                if (isNavBarFollow && isNavBarVisible && window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    PopupUtils.setNavigationBarColor(window, (Integer) animation.getAnimatedValue());
                }
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration ? 0 : Popup.getAnimationDuration()).start();
    }

    @Override
    public void animateDismiss() {
        if (animating) {
            return;
        }
        if (startColor == shadowColor) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, shadowColor, startColor);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                targetView.setBackgroundColor(value);
                if (isNavBarFollow && isNavBarVisible && window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (value == startColor) {
                        PopupUtils.setNavigationBarColor(window, navigationBarColorStartColor);
                    } else {
                        PopupUtils.setNavigationBarColor(window, (Integer) animation.getAnimatedValue());
                    }

                }
            }
        });
        observerAnimator(animator);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(isZeroDuration ? 0 : getDuration()).start();
    }

    public void applyColorValue(float val) {
        if (startColor == shadowColor) {
            return;
        }
        int color = calculateBgColor(val);
        targetView.setBackgroundColor(color);
        if (isNavBarFollow && isNavBarVisible && window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PopupUtils.setNavigationBarColor(window, color);
        }
    }

    public int calculateBgColor(float fraction) {
        return (int) argbEvaluator.evaluate(fraction, startColor, shadowColor);
    }

    @Override
    public void onNavigationBarChange(boolean show) {
        isNavBarVisible = show;
    }
}
