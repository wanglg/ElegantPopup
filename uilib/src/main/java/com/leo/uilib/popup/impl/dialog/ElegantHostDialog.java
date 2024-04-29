package com.leo.uilib.popup.impl.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.leo.uilib.popup.R;
import com.leo.uilib.popup.impl.BasePopupView;

/**
 * @author leo
 */
public class ElegantHostDialog extends AppCompatDialog {
    BasePopupView contentView;

    public ElegantHostDialog(@NonNull Context context) {
        super(context, R.style.Popup_TransparentDialog);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public ElegantHostDialog setContent(BasePopupView view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        this.contentView = view;
        setContentView(this.contentView);
        return this;
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (isFuckVIVORoom()) { //VIVO的部分机型需要做特殊处理，Fuck
//            event.setLocation(event.getX(), event.getY() + PopupUtils.getStatusBarHeight());
//        }
//        return super.dispatchTouchEvent(event);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.getAttributes().format = PixelFormat.TRANSPARENT;
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(option);
            window.setBackgroundDrawable(null);

            //remove status bar shadow
            if (Build.VERSION.SDK_INT == 19) {  //解决4.4上状态栏闪烁的问题
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else if (Build.VERSION.SDK_INT == 20) {
                setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
            } else if (Build.VERSION.SDK_INT >= 21) {
                setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); //尝试兼容部分手机上的状态栏空白问题
            }
        }

    }

    public void setWindowFlag(final int bits, boolean on) {
        WindowManager.LayoutParams winParams = getWindow().getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        getWindow().setAttributes(winParams);
    }
//
//    void setUiFlags(Boolean fullscreen) {
//        Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        if (fullscreen) {
//            winParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        } else {
//            winParams.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        }
//        win.setAttributes(winParams);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            View decorView = getWindow().getDecorView();
//            if (decorView != null) {
//                //TODO 根据具体要求修改
//                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.
//                        SYSTEM_UI_FLAG_LAYOUT_STABLE;
//                decorView.setSystemUiVisibility(fullscreen ? getFullscreenUiFlags() :
//                        option);
//            }
//        }
//    }
//
//    private int getFullscreenUiFlags() {
//        int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        }
//
//        return flags;
//    }
}
