package com.leo.uilib.popup.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;


import androidx.annotation.NonNull;

import com.leo.uilib.popup.impl.BasePopupView;

import java.util.HashMap;

public final class KeyboardUtils {

    private static HashMap<BasePopupView, ViewTreeObserver.OnGlobalLayoutListener> globalObserverMap = new HashMap<>();

    private KeyboardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static int sDecorViewDelta = 0;

    private static int getDecorViewInvisibleHeight(final Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
//        if (decorView == null) {
//            return sDecorViewInvisibleHeightPre;
//        }
        final Rect outRect = new Rect();
        decorView.getWindowVisibleDisplayFrame(outRect);
        int delta = Math.abs(decorView.getBottom() - outRect.bottom);
        if (delta <= getNavBarHeight()) {
            sDecorViewDelta = delta;
            return 0;
        }
        return delta - sDecorViewDelta;
    }

    /**
     * Register soft input changed listener.
     *
     * @param popupView The view.
     * @param listener  The soft input changed listener.
     */
    public static void registerSoftInputChangedListener(final BasePopupView popupView, final OnSoftInputChangedListener listener) {
        Context context = popupView.getContext();
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            final int flags = activity.getWindow().getAttributes().flags;
            if ((flags & WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
            final FrameLayout contentView = activity.findViewById(android.R.id.content);
            final int[] decorViewInvisibleHeightPre = {getDecorViewInvisibleHeight(activity)};

            ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = () -> {
                int height = getDecorViewInvisibleHeight(activity);
                if (decorViewInvisibleHeightPre[0] != height) {
                    listener.onSoftInputChanged(height);
                    decorViewInvisibleHeightPre[0] = height;
                }
            };
            contentView.getViewTreeObserver()
                    .addOnGlobalLayoutListener(onGlobalLayoutListener);
            globalObserverMap.put(popupView, onGlobalLayoutListener);
        }
    }

    public static void removeLayoutChangeListener(BasePopupView popupView) {
//        onGlobalLayoutListener = null;
        if (popupView == null) {
            return;
        }
        Context context = popupView.getContext();
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            final FrameLayout contentView = activity.findViewById(android.R.id.content);
            if (contentView == null) {
                return;
            }
            if (globalObserverMap.containsKey(popupView)) {
                ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = globalObserverMap.get(popupView);
                contentView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
                globalObserverMap.remove(popupView);
            }
//        contentView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
//        listenerMap.remove(popupView);
        }
    }

    private static int getNavBarHeight() {
        Resources res = Resources.getSystem();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return res.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    public static void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Return whether soft input is visible.
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSoftInputVisible(@NonNull final Activity activity) {
        return getDecorViewInvisibleHeight(activity) > 0;
    }

    public interface OnSoftInputChangedListener {
        void onSoftInputChanged(int height);
    }
}