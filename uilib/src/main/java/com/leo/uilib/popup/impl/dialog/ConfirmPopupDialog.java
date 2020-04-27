package com.leo.uilib.popup.impl.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.leo.uilib.popup.R;
import com.leo.uilib.popup.core.IPopup;
import com.leo.uilib.popup.core.IPopupListener;

import org.json.JSONObject;

/**
 * @Author: wangliugeng
 * @Date : 2020/3/21
 * @Email: leo3552@163.com
 * @Desciption:
 */
public class ConfirmPopupDialog extends BasePopupDialog implements View.OnClickListener {


    private IPopupListener popupListener;
    TextView tv_title, tv_content, tv_cancel, tv_confirm;
    String title, content, hint, cancelText, confirmText;

    public ConfirmPopupDialog(Context context) {
        super(context, R.style.UPopupDialogStyle);
    }

    @Override
    public int getLayoutId() {
        return R.layout._xpopup_center_impl_confirm;
    }

    @Override
    public void initData(JSONObject jsonObject) {
        popupListener = popupInfo.popupListener;
    }

    @Override
    public void configViews() {
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);

        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content)) {
            tv_content.setText(content);
        } else {
            tv_content.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(cancelText)) {
            tv_cancel.setText(cancelText);
        }
        if (!TextUtils.isEmpty(confirmText)) {
            tv_confirm.setText(confirmText);
        }

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onClick(View v) {
        if (v == tv_cancel) {
            if (popupListener != null) {
                popupListener.onCancel();
            }
            dismiss();
        } else if (v == tv_confirm) {
            if (popupListener != null) {
                popupListener.onConfirm();
            }
            if (popupInfo.autoDismiss) {
                dismiss();
            }
        }
    }

    public IPopup setTitleContent(String title, String content, String hint) {
        this.title = title;
        this.content = content;
        this.hint = hint;
        return this;
    }

    public IPopup setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public IPopup setConfirmText(String confirmText) {
        this.confirmText = confirmText;
        return this;
    }
}
