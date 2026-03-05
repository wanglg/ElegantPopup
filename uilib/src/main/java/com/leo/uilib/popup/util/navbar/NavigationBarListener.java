package com.leo.uilib.popup.util.navbar;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 导航栏变化监听工具类
 * 内部使用，不对外暴露
 */
class NavigationBarListener {

    private static final String TAG = "NavBarListener";

    // 存储每个 Window 的监听器信息
    private static final Map<String, ListenerInfo> sListenerMap = new ConcurrentHashMap<>();

    /**
     * 添加导航栏变化监听
     *
     * @param window   要监听的 Window
     * @param listener 监听回调
     */
    static void addListener(@NonNull Window window, @NonNull NavigationBarManager.OnChangeListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "Navigation bar listener requires API 23+");
            return;
        }

        // 获取 Window 的唯一标识
        String windowKey = getWindowKey(window);

        // 如果已经存在监听器，先移除
        if (sListenerMap.containsKey(windowKey)) {
            Log.w(TAG, "Window already has listener, removing old listener");
            removeListener(window);
        }

        // 获取 DecorView
        View decorView = window.getDecorView();
        if (decorView == null) {
            Log.e(TAG, "DecorView is null");
            return;
        }

        // 创建 WindowInsets 监听器
        View.OnApplyWindowInsetsListener insetsListener = new View.OnApplyWindowInsetsListener() {

            // 记录上次状态
            private boolean mLastVisible = false;
            private int mLastHeight = 0;
            private boolean mIsInitialized = false;

            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // 判断导航栏是否可见
                boolean isVisible = isNavigationBarVisible(insets, v);

                // 获取导航栏高度
                int height = getNavigationBarHeight(insets, v);

                Log.d(TAG, "WindowInsets changed - isVisible: " + isVisible + ", height: " + height);

                // 首次调用时只记录状态，不回调
                if (!mIsInitialized) {
                    mIsInitialized = true;
                    mLastVisible = isVisible;
                    mLastHeight = height;
                    Log.d(TAG, "First callback, recording initial state");
                    return v.onApplyWindowInsets(insets);
                }

                // 检测变化
                if (isVisible != mLastVisible || height != mLastHeight) {
                    mLastVisible = isVisible;
                    mLastHeight = height;

                    // 回调监听器
                    if (isVisible && height > 0) {
                        listener.onNavigationBarShow(height);
                    } else {
                        listener.onNavigationBarHide();
                    }
                } else {
                    Log.d(TAG, "State unchanged, no callback");
                }

                return v.onApplyWindowInsets(insets);
            }

            /**
             * 判断导航栏是否可见
             */
            private boolean isNavigationBarVisible(WindowInsets insets, View decorView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    return insets.isVisible(WindowInsets.Type.navigationBars());
                } else {
                    // Android 6.0 - 10
                    int visibility = decorView.getSystemUiVisibility();
                    boolean hideNavigation = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0;
                    return !hideNavigation;
                }
            }

            /**
             * 获取导航栏高度
             */
            private int getNavigationBarHeight(WindowInsets insets, View decorView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    android.graphics.Insets navBarInsets =
                            insets.getInsets(WindowInsets.Type.navigationBars());
                    return Math.max(
                            navBarInsets.bottom,
                            Math.max(navBarInsets.left, navBarInsets.right)
                    );
                } else {
                    // Android 6.0 - 10
                    return Math.max(
                            insets.getSystemWindowInsetBottom(),
                            Math.max(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetRight())
                    );
                }
            }
        };

        // 设置监听器
        decorView.setOnApplyWindowInsetsListener(insetsListener);

        // 保存监听器信息
        ListenerInfo listenerInfo = new ListenerInfo(
                new WeakReference<>(window),
                listener,
                insetsListener
        );
        sListenerMap.put(windowKey, listenerInfo);

        Log.d(TAG, "Added listener for window: " + windowKey);
    }

    /**
     * 移除导航栏变化监听
     *
     * @param window 要移除监听的 Window
     */
    static void removeListener(@NonNull Window window) {
        String windowKey = getWindowKey(window);
        removeListenerByKey(windowKey);
    }

    /**
     * 根据 key 移除监听器
     */
    private static void removeListenerByKey(String windowKey) {
        ListenerInfo listenerInfo = sListenerMap.remove(windowKey);

        if (listenerInfo == null) {
            Log.w(TAG, "No listener found for window: " + windowKey);
            return;
        }

        Window window = listenerInfo.windowRef.get();
        if (window != null) {
            View decorView = window.getDecorView();
            if (decorView != null) {
                decorView.setOnApplyWindowInsetsListener(null);
            }
        }

        Log.d(TAG, "Removed listener for window: " + windowKey);
    }

    /**
     * 移除所有监听器
     */
    static void removeAllListeners() {
        Log.d(TAG, "Removing all listeners, count: " + sListenerMap.size());

        for (String key : sListenerMap.keySet()) {
            removeListenerByKey(key);
        }

        sListenerMap.clear();
    }

    /**
     * 获取当前监听器数量
     */
    static int getListenerCount() {
        return sListenerMap.size();
    }

    /**
     * 检查指定 Window 是否已添加监听器
     */
    static boolean hasListener(@NonNull Window window) {
        String windowKey = getWindowKey(window);
        return sListenerMap.containsKey(windowKey);
    }

    /**
     * 获取 Window 的唯一标识
     */
    private static String getWindowKey(Window window) {
        return "Window@" + Integer.toHexString(window.hashCode());
    }

    /**
     * 监听器信息类
     */
    private static class ListenerInfo {
        final WeakReference<Window> windowRef;
        final NavigationBarManager.OnChangeListener listener;
        final View.OnApplyWindowInsetsListener insetsListener;

        ListenerInfo(
                WeakReference<Window> windowRef,
                NavigationBarManager.OnChangeListener listener,
                View.OnApplyWindowInsetsListener insetsListener
        ) {
            this.windowRef = windowRef;
            this.listener = listener;
            this.insetsListener = insetsListener;
        }
    }
}
