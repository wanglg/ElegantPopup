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
 * 导航栏高度获取工具类
 * 内部使用，不对外暴露
 */
class NavigationBarHeightProvider {

    private static final String TAG = "NavBarHeightProvider";

    /**
     * 获取导航栏高度
     *
     * @param window Window 对象
     * @return 导航栏高度（像素），如果不可见则返回 0
     */
    static int getHeight(@Nullable Window window) {
        if (window == null) {
            Log.e(TAG, "Window is null");
            return 0;
        }

        try {
            View decorView = window.getDecorView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ (API 30+)
                return getHeightApi30(decorView);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0 - 10 (API 23-29)
                return getHeightApi23(decorView);
            } else {
                // Android 5.x 及以下 (API < 23)
                return getHeightFromResource(window.getContext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting navigation bar height", e);
            return 0;
        }
    }

    /**
     * Android 11+ (API 30+) 获取高度
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private static int getHeightApi30(View decorView) {
        WindowInsets insets = decorView.getRootWindowInsets();

        if (insets == null) {
            Log.w(TAG, "WindowInsets is null");
            return 0;
        }

        // 判断是否可见
        if (!insets.isVisible(WindowInsets.Type.navigationBars())) {
            Log.d(TAG, "Navigation bar is not visible");
            return 0;
        }

        // 获取导航栏 Insets（检查所有方向）
        android.graphics.Insets navBarInsets =
                insets.getInsets(WindowInsets.Type.navigationBars());

        int height = Math.max(
                navBarInsets.bottom,
                Math.max(navBarInsets.left, navBarInsets.right)
        );

        Log.d(TAG, "API 30+ - height: " + height);
        return height;
    }

    /**
     * Android 6.0 - 10 (API 23-29) 获取高度
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static int getHeightApi23(View decorView) {
        WindowInsets insets = decorView.getRootWindowInsets();

        if (insets == null) {
            Log.w(TAG, "WindowInsets is null");
            return 0;
        }

        // 检查是否隐藏
        int visibility = decorView.getSystemUiVisibility();
        boolean hideNavigation = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0;

        if (hideNavigation) {
            Log.d(TAG, "Navigation bar is hidden");
            return 0;
        }

        // 获取导航栏 Insets（检查所有方向）
        int height = Math.max(
                insets.getSystemWindowInsetBottom(),
                Math.max(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetRight())
        );

        Log.d(TAG, "API 23-29 - height: " + height);
        return height;
    }

    /**
     * 从资源文件获取导航栏高度
     *
     * @param context Context 对象
     * @return 导航栏高度（像素）
     */
    static int getHeightFromResource(@Nullable Context context) {
        if (context == null) {
            return 0;
        }

        try {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier(
                    "navigation_bar_height",
                    "dimen",
                    "android"
            );

            if (resourceId > 0) {
                int height = resources.getDimensionPixelSize(resourceId);
                Log.d(TAG, "Height from resource: " + height);
                return height;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting height from resource", e);
        }

        return 0;
    }
}
