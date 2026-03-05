package com.leo.uilib.popup.util.navbar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 导航栏可见性判断工具类
 * 内部使用，不对外暴露
 */
class NavigationBarVisibilityChecker {

    private static final String TAG = "NavBarVisibilityChecker";

    /**
     * 判断导航栏是否可见
     *
     * @param window Window 对象
     * @return true 表示导航栏可见
     */
    static boolean isVisible(@Nullable Window window) {
        if (window == null) {
            Log.e(TAG, "Window is null");
            return false;
        }

        try {
            View decorView = window.getDecorView();
            Context context = window.getContext();

            // 根据 Android 版本选择不同的判断方法
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ (API 30+)
                return isVisibleApi30(decorView, context);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0 - 10 (API 23-29)
                return isVisibleApi23(decorView, context);
            } else {
                // Android 5.x 及以下 (API < 23)
                return isVisibleLegacy(decorView, context);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking navigation bar visibility", e);
            return hasNavigationBar(window.getContext());
        }
    }

    /**
     * Android 11+ (API 30+) 判断方法
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private static boolean isVisibleApi30(View decorView, Context context) {
        WindowInsets insets = decorView.getRootWindowInsets();

        if (insets == null) {
            Log.w(TAG, "WindowInsets is null, using default value");
            return hasNavigationBar(context);
        }

        // 判断导航栏是否可见
        boolean isVisible = insets.isVisible(WindowInsets.Type.navigationBars());

        // 获取导航栏 Insets（检查所有方向）
        android.graphics.Insets navigationBarInsets =
                insets.getInsets(WindowInsets.Type.navigationBars());

        int navBarSize = Math.max(
                navigationBarInsets.bottom,
                Math.max(navigationBarInsets.left, navigationBarInsets.right)
        );

        Log.d(TAG, "API 30+ - isVisible: " + isVisible + ", size: " + navBarSize);

        // 同时满足：可见 && 尺寸 > 0
        return isVisible && navBarSize > 0;
    }

    /**
     * Android 6.0 - 10 (API 23-29) 判断方法
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean isVisibleApi23(View decorView, Context context) {
        WindowInsets insets = decorView.getRootWindowInsets();

        if (insets == null) {
            Log.w(TAG, "WindowInsets is null, using default value");
            return hasNavigationBar(context);
        }

        // 获取导航栏 Insets（检查所有方向）
        int navBarSize = Math.max(
                insets.getSystemWindowInsetBottom(),
                Math.max(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetRight())
        );

        // 检查 SystemUiVisibility
        int visibility = decorView.getSystemUiVisibility();
        boolean hideNavigation = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0;

        Log.d(TAG, "API 23-29 - navBarSize: " + navBarSize +
                ", hideNavigation: " + hideNavigation);

        // 尺寸 > 0 && 未设置隐藏标志
        return navBarSize > 0 && !hideNavigation;
    }

    /**
     * Android 5.x 及以下 (API < 23) 判断方法
     */
    @SuppressWarnings("deprecation")
    private static boolean isVisibleLegacy(View decorView, Context context) {
        // 使用 SystemUiVisibility 判断
        int visibility = decorView.getSystemUiVisibility();
        boolean hideNavigation = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0;

        Log.d(TAG, "API < 23 - hideNavigation: " + hideNavigation);

        // 设备有导航栏 && 未设置隐藏标志
        return hasNavigationBar(context) && !hideNavigation;
    }

    /**
     * 判断设备是否有导航栏（硬件层面）
     *
     * @param context Context 对象
     * @return true 表示设备有导航栏
     */
    static boolean hasNavigationBar(@Nullable Context context) {
        if (context == null) {
            return false;
        }

        try {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier(
                    "config_showNavigationBar",
                    "bool",
                    "android"
            );

            if (resourceId > 0) {
                return resources.getBoolean(resourceId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking hasNavigationBar", e);
        }

        // 默认假设有导航栏
        return true;
    }
}
