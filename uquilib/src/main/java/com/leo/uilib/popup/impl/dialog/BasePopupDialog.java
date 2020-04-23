package com.leo.uilib.popup.impl.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.appcompat.app.AppCompatDialog;

import com.leo.uilib.popup.core.IPopup;
import com.leo.uilib.popup.core.IPopupWindow;
import com.leo.uilib.popup.core.PopupInfo;
import com.leo.uilib.popup.core.PopupManager;
import com.leo.uilib.popup.util.PopupUtils;

/**
 * @Author: wangliugeng
 * @Date : 2020/3/19
 * @Email: leo3552@163.com
 * @Desciption:
 */
public abstract class BasePopupDialog extends AppCompatDialog implements IPopup,
        DialogInterface.OnShowListener, DialogInterface.OnDismissListener, IPopupWindow {
    public PopupInfo popupInfo;
    public Context mContext;
    private View mView;
    boolean cancelable = true;

    public BasePopupDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        initView(context);
        setOnShowListener(this);
        setOnDismissListener(this);
    }

    protected void initView(Context context) {
        mView = LayoutInflater.from(context).inflate(getLayoutId(), null, false);
        setContentView(mView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData(popupInfo.extObject);
        configViews();
        requestData();
    }

    @Override
    public IPopup showPopup() {
        try {
            show();
        } catch (Exception e) {
            Log.e("s_popup", e.getMessage());
        }
        return this;
    }

    @Override
    public boolean isShow() {
        return isShowing();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (popupInfo != null) {
            setCanceledOnTouchOutside(popupInfo.isDismissOnTouchOutside);
            setCancelable(popupInfo.isDismissOnBackPressed);
        }
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            ViewGroup.LayoutParams vl = dialogWindow.getAttributes();
            if (popupInfo != null) {
                if (popupInfo.maxWidth > 0) {
                    vl.width = popupInfo.maxWidth;
                } else {
                    vl.width = getMaxWidth();
                }
                if (popupInfo.maxHeight > 0) {

                }
                if (popupInfo.hasShadowBg) {
                    dialogWindow.setDimAmount(0.5f);
                } else {
                    dialogWindow.setDimAmount(0);
                }

            }

        }

    }

    /**
     * 弹窗的最大宽度，一般用来限制布局宽度为wrap或者match时的最大宽度
     *
     * @return
     */
    protected int getMaxWidth() {
        return PopupUtils.dp2px(getContext(), 288);
    }

    @Override
    public boolean isDismiss() {
        return !isShowing();
    }

    @Override
    public void toggle() {
        if (isShow()) {
            dismiss();
        } else {
            showPopup();
        }
    }

    @Override
    public PopupInfo getPopupInfo() {
        return popupInfo;
    }

    @Override
    public void setPopupInfo(PopupInfo popupInfo) {
        this.popupInfo = popupInfo;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        PopupManager.getPopupManager().addPopup(getContext(), this);
        if (popupInfo != null && popupInfo.popupListener != null) {
            popupInfo.popupListener.onShow();
        }
        onShow();
    }

    public void onShow() {
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        PopupManager.getPopupManager().removePopup(getContext(), this);
        if (popupInfo != null && popupInfo.popupListener != null) {
            popupInfo.popupListener.onDismiss();
        }
        onDismiss();
    }

    public void onDismiss() {

    }

    protected int getMaxHeight() {
        return popupInfo.maxHeight;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (this.cancelable != cancelable) {
            this.cancelable = cancelable;
            onSetCancelable(cancelable);
        }
    }

    protected void onSetCancelable(boolean cancelable) {

    }

    @Override
    public int compareTo(IPopup o) {
        if (popupInfo == null || o == null || o.getPopupInfo() == null) {
            return 0;
        }
        long currentPriority = popupInfo.priority;
        long comparePriority = o.getPopupInfo().priority;
        return Long.compare(currentPriority, comparePriority);
    }
}
