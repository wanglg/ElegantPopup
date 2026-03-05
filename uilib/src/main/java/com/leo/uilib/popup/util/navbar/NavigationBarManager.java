package com.leo.uilib.popup.util.navbar;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 导航栏管理器 - 统一入口
 * <p>
 * 功能：
 * 1. 判断导航栏是否可见
 * 2. 获取导航栏高度
 * 3. 监听导航栏变化
 * 4. 判断设备是否有导航栏
 * <p>
 * 使用示例：
 * <pre>
 * // 判断可见性
 * boolean isVisible = NavigationBarManager.isVisible(getWindow());
 *
 * // 获取高度
 * int height = NavigationBarManager.getHeight(getWindow());
 *
 * // 添加监听
 * NavigationBarManager.addListener(getWindow(), new NavigationBarManager.OnChangeListener() {
 *     @Override
 *     public void onShow(int height) {
 *         // 导航栏显示
 *     }
 *
 *     @Override
 *     public void onHide() {
 *         // 导航栏隐藏
 *     }
 * });
 *
 * // 移除监听
 * NavigationBarManager.removeListener(getWindow());
 * </pre>
 */
public class NavigationBarManager {

    private static final String TAG = "NavigationBarManager";

    /**
     * 私有构造函数，防止实例化
     */
    private NavigationBarManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==================== 可见性判断 ====================

    /**
     * 判断导航栏是否可见
     *
     * @param window Window 对象
     * @return true 表示导航栏可见
     */
    public static boolean isVisible(@Nullable Window window) {
        return NavigationBarVisibilityChecker.isVisible(window);
    }

    /**
     * 判断设备是否有导航栏（硬件层面）
     *
     * @param context Context 对象
     * @return true 表示设备有导航栏
     */
    public static boolean hasNavigationBar(@Nullable Context context) {
        return NavigationBarVisibilityChecker.hasNavigationBar(context);
    }

    // ==================== 高度获取 ====================

    /**
     * 获取导航栏高度
     *
     * @param window Window 对象
     * @return 导航栏高度（像素），如果不可见则返回 0
     */
    public static int getHeight(@Nullable Window window) {
        return NavigationBarHeightProvider.getHeight(window);
    }

    /**
     * 获取导航栏高度（从资源文件）
     *
     * @param context Context 对象
     * @return 导航栏高度（像素）
     */
    public static int getHeightFromResource(@Nullable Context context) {
        return NavigationBarHeightProvider.getHeightFromResource(context);
    }

    // ==================== 变化监听 ====================

    /**
     * 添加导航栏变化监听
     *
     * @param window   Window 对象
     * @param listener 监听回调
     */
    public static void addListener(@NonNull Window window, @NonNull OnChangeListener listener) {
        NavigationBarListener.addListener(window, listener);
    }

    /**
     * 移除导航栏变化监听
     *
     * @param window Window 对象
     */
    public static void removeListener(@NonNull Window window) {
        NavigationBarListener.removeListener(window);
    }

    /**
     * 移除所有监听器
     */
    public static void removeAllListeners() {
        NavigationBarListener.removeAllListeners();
    }

    /**
     * 获取当前监听器数量
     *
     * @return 监听器数量
     */
    public static int getListenerCount() {
        return NavigationBarListener.getListenerCount();
    }

    /**
     * 检查指定 Window 是否已添加监听器
     *
     * @param window Window 对象
     * @return true 表示已添加监听器
     */
    public static boolean hasListener(@NonNull Window window) {
        return NavigationBarListener.hasListener(window);
    }

    // ==================== 回调接口 ====================

    /**
     * 导航栏变化监听接口
     */
    public interface OnChangeListener {
        /**
         * 导航栏显示
         *
         * @param height 导航栏高度（像素）
         */
        void onNavigationBarShow(int height);

        /**
         * 导航栏隐藏
         */
        void onNavigationBarHide();
    }

    // ==================== 调试工具 ====================

    /**
     * 打印导航栏信息（用于调试）
     *
     * @param window Window 对象
     */
    public static void printDebugInfo(@Nullable Window window) {
        if (window == null) {
            Log.e(TAG, "Window is null");
            return;
        }

        Context context = window.getContext();
        boolean isVisible = isVisible(window);
        int height = getHeight(window);
        boolean hasNavBar = hasNavigationBar(context);
        int resourceHeight = getHeightFromResource(context);

        Log.d(TAG, "========== Navigation Bar Debug Info ==========");
        Log.d(TAG, "Android Version: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "Manufacturer: " + Build.MANUFACTURER);
        Log.d(TAG, "Model: " + Build.MODEL);
        Log.d(TAG, "Has Navigation Bar: " + hasNavBar);
        Log.d(TAG, "Is Visible: " + isVisible);
        Log.d(TAG, "Current Height: " + height + " px");
        Log.d(TAG, "Resource Height: " + resourceHeight + " px");
        Log.d(TAG, "Listener Count: " + getListenerCount());
        Log.d(TAG, "Has Listener: " + hasListener(window));
        Log.d(TAG, "===============================================");
    }
}
