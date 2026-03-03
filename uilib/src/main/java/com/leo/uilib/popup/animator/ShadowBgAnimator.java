package com.leo.uilib.popup.animator;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.leo.uilib.popup.Popup;


/**
 * Description: 背景Shadow动画器，负责执行半透明的渐入渐出动画
 * Create by dance, at 2018/12/9
 */
public class ShadowBgAnimator extends PopupAnimator {

    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    public int startColor = Color.TRANSPARENT;
    public boolean isZeroDuration = false;
    public int shadowColor;

    public ShadowBgAnimator(View target, int animationDuration, int shadowColor) {
        super(target, animationDuration);
        this.shadowColor = shadowColor;
    }

    @Override
    public void initAnimator() {
        targetView.setBackgroundColor(startColor);
    }

    @Override
    public void animateShow() {
        if (startColor == shadowColor) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, startColor, shadowColor);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                targetView.setBackgroundColor((Integer) animation.getAnimatedValue());
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
    }

    public int calculateBgColor(float fraction) {
        return (int) argbEvaluator.evaluate(fraction, startColor, shadowColor);
    }

}
