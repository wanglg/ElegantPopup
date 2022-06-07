package com.leo.uilib.popup.impl.dialog;

import android.content.Context;
import android.os.Build;
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
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
    }
}
