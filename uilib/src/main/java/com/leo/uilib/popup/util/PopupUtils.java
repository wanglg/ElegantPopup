package com.leo.uilib.popup.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import com.leo.uilib.popup.impl.BasePopupView;
import com.leo.uilib.popup.impl.BottomPopupView;
import com.leo.uilib.popup.impl.CenterPopupView;
import com.leo.uilib.popup.impl.DrawerPopupView;
import com.leo.uilib.popup.impl.FullScreenPopupView;

import java.util.ArrayList;

public class PopupUtils {

    //应用界面可见高度，可能不包含导航和状态栏，看Rom实现
    public static int getAppHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.y;
    }

    public static int getAppWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.x;
    }

    //屏幕的高度，包含状态栏，导航栏，看Rom实现
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    public static Activity context2Activity(View view) {
        return (Activity) view.getContext();
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isTablet() {
        return (Resources.getSystem().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    public static int getWindowWidth(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    public static int getWindowHeight(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * Return the navigation bar's height.
     *
     * @return the navigation bar's height
     */
    public static int getNavBarHeight() {
        Resources res = Resources.getSystem();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return res.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    public static void setWidthHeight(View target, int width, int height) {
        if (width <= 0 && height <= 0) {
            return;
        }
        ViewGroup.LayoutParams params = target.getLayoutParams();
        if (width > 0) {
            params.width = width;
        }
        if (height > 0) {
            params.height = height;
        }
        target.setLayoutParams(params);
    }

    public static void applyPopupSize(ViewGroup content, int maxWidth, int maxHeight) {
        applyPopupSize(content, maxWidth, maxHeight, null);
    }

    public static void applyPopupSize(final ViewGroup content, final int maxWidth, final int maxHeight, final Runnable afterApplySize) {
        content.post(() -> {
            ViewGroup.LayoutParams params = content.getLayoutParams();

            int w = content.getMeasuredWidth();

            if (maxWidth != 0) {
                params.width = Math.min(w, maxWidth);
            }
            if (maxHeight > 0) {
                if (params.height == FrameLayout.LayoutParams.MATCH_PARENT) {
                    params.height = maxHeight;
                } else {
                    int h = content.getMeasuredHeight();
                    params.height = Math.min(h, maxHeight);
                }
            }

            content.setLayoutParams(params);

            if (afterApplySize != null) {
                afterApplySize.run();
            }
        });
    }

    public static void setCursorDrawableColor(EditText et, int color) {
        //暂时没有找到有效的方法来动态设置cursor的颜色
    }

    public static BitmapDrawable createBitmapDrawable(Resources resources, int width, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, 20, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0, 0, bitmap.getWidth(), 4, paint);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0, 4, bitmap.getWidth(), 20, paint);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
        bitmapDrawable.setGravity(Gravity.BOTTOM);
        return bitmapDrawable;
    }

    public static StateListDrawable createSelector(Drawable defaultDrawable, Drawable focusDrawable) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, focusDrawable);
        stateListDrawable.addState(new int[]{}, defaultDrawable);
        return stateListDrawable;
    }

    public static boolean isInRect(float x, float y, Rect rect) {
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
    }

    /**
     * Return whether soft input is visible.
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSoftInputVisible(final Activity activity) {
        return getDecorViewInvisibleHeight(activity) > 0;
    }

    private static int sDecorViewDelta = 0;

    public static int getDecorViewInvisibleHeight(final Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
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
     * Return whether the navigation bar visible.
     * <p>Call it in onWindowFocusChanged will get right result.</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isNavBarVisible(Context context) {
        boolean isVisible = false;
        ViewGroup decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        for (int i = 0, count = decorView.getChildCount(); i < count; i++) {
            final View child = decorView.getChildAt(i);
            final int id = child.getId();
            if (id != View.NO_ID) {
                String resourceEntryName = context
                        .getResources()
                        .getResourceEntryName(id);
                if ("navigationBarBackground".equals(resourceEntryName)
                        && child.getVisibility() == View.VISIBLE) {
                    isVisible = true;
                    break;
                }
            }
        }
        if (isVisible) {
            int visibility = decorView.getSystemUiVisibility();
            isVisible = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
        }
        return isVisible;
    }

    public static boolean isNavBarVisible(Window window) {
        boolean isVisible = false;
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        for (int i = 0, count = decorView.getChildCount(); i < count; i++) {
            final View child = decorView.getChildAt(i);
            final int id = child.getId();
            if (id != View.NO_ID) {
                try {
                    String resourceEntryName = window.getContext().getResources().getResourceEntryName(id);
                    if ("navigationBarBackground".equals(resourceEntryName)
                            && child.getVisibility() == View.VISIBLE) {
                        isVisible = true;
                        break;
                    }
                } catch (Resources.NotFoundException e) {
                    break;
                }
            }
        }
        if (isVisible) {
            // 对于三星手机，android10以下非OneUI2的版本，比如 s8，note8 等设备上，
            // 导航栏显示存在bug："当用户隐藏导航栏时显示输入法的时候导航栏会跟随显示"，会导致隐藏输入法之后判断错误
            // 这个问题在 OneUI 2 & android 10 版本已修复
            if (FuckRomUtils.isSamsung()
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                try {
                    return Settings.Global.getInt(window.getContext().getContentResolver(), "navigationbar_hide_bar_enabled") == 0;
                } catch (Exception ignore) {
                }
            }

            int visibility = decorView.getSystemUiVisibility();
            isVisible = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
        }

        return isVisible;
    }

    public static void findAllEditText(ArrayList<EditText> list, ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof EditText && v.getVisibility() == View.VISIBLE) {
                list.add((EditText) v);
            } else if (v instanceof ViewGroup) {
                findAllEditText(list, (ViewGroup) v);
            }
        }
    }


    public static void moveDown(BasePopupView pv) {
        pv.getPopupContentView().animate().translationY(0)
                .setInterpolator(new OvershootInterpolator(0))
                .setDuration(200).start();

    }

    public static void moveUpToKeyboard(int keyboardHeight, BasePopupView pv) {
        if (!pv.popupInfo.isMoveUpToKeyboard) {
            return;
        }
        pv.post(() -> {
            //判断是否盖住输入框
            ArrayList<EditText> allEts = new ArrayList<>();
            findAllEditText(allEts, pv);
            EditText focusEt = null;
            for (EditText et : allEts) {
                if (et.isFocused()) {
                    focusEt = et;
                    break;
                }
            }

            int dy = 0;
            int popupHeight = pv.getPopupContentView().getHeight();
            int popupWidth = pv.getPopupContentView().getWidth();
            if (pv.getPopupImplView() != null) {
                popupHeight = Math.min(popupHeight, pv.getPopupImplView().getMeasuredHeight());
                popupWidth = Math.min(popupWidth, pv.getPopupImplView().getMeasuredWidth());
            }
            int windowHeight = getWindowHeight(pv.getContext());
            int focusEtTop = 0;
            int focusBottom = 0;
            if (focusEt != null) {
                int[] locations = new int[2];
                focusEt.getLocationInWindow(locations);
                focusEtTop = locations[1];
                focusBottom = focusEtTop + focusEt.getMeasuredHeight();
            }


            //执行上移
            if (pv instanceof FullScreenPopupView ||
                    (popupWidth == PopupUtils.getWindowWidth(pv.getContext()) &&
                            popupHeight == (PopupUtils.getWindowHeight(pv.getContext()) + PopupUtils.getStatusBarHeight()))
            ) {
                // 如果是全屏弹窗，特殊处理，只要输入框没被盖住，就不移动。
                if (focusBottom + keyboardHeight < windowHeight) {
                    return;
                }
            }
            if (pv instanceof FullScreenPopupView) {
                int overflowHeight = (focusBottom + keyboardHeight) - windowHeight;
                if (focusEt != null && overflowHeight > 0) {
                    dy = overflowHeight;
                }
            } else if (pv instanceof CenterPopupView) {
                int targetY = keyboardHeight - (windowHeight - popupHeight + getStatusBarHeight()) / 2; //上移到下边贴着输入法的高度

                if (focusEt != null && focusEtTop - targetY < 0) {
                    targetY += focusEtTop - targetY - getStatusBarHeight();//限制不能被状态栏遮住
                }
                dy = Math.max(0, targetY);
            } else if (pv instanceof BottomPopupView) {
                dy = keyboardHeight;
                if (focusEt != null && focusEtTop - dy < 0) {
                    dy += focusEtTop - dy - getStatusBarHeight();//限制不能被状态栏遮住
                }
            } else if (pv instanceof DrawerPopupView) {
                int overflowHeight = (focusBottom + keyboardHeight) - windowHeight;
                if (focusEt != null && overflowHeight > 0) {
                    dy = overflowHeight;
                }
            } /*else if (isTopPartShadow(pv)) {
            int overflowHeight = (focusBottom + keyboardHeight) - windowHeight;
            if (focusEt != null && overflowHeight > 0) {
                dy = overflowHeight;
            }
            if (dy != 0) {
                pv.getPopupImplView().animate().translationY(-dy)
                        .setDuration(200)
                        .setInterpolator(new OvershootInterpolator(0))
                        .start();
            }
            return;
        }*/
            //dy=0说明没有触发移动，有些弹窗有translationY，不能影响它们
            if (dy == 0 && pv.getPopupContentView().getTranslationY() != 0) {
                return;
            }
            pv.getPopupContentView().animate().translationY(-dy)
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator(0))
                    .start();
        });

    }

}
