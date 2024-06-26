package com.leo.uilib.popup.impl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.leo.uilib.popup.Popup;
import com.leo.uilib.popup.R;
import com.leo.uilib.popup.core.IPopupListener;


/**
 * Description: 确定和取消的对话框
 * Create by dance, at 2018/12/16
 */
public class ConfirmPopupView extends CenterPopupView implements View.OnClickListener {
    IPopupListener popupListener;
    TextView tv_title, tv_content, tv_cancel, tv_confirm;
    EditText et_input;
    String title, content, hint, cancelText, confirmText;
    boolean isHideCancel = false;

    public ConfirmPopupView(@NonNull Context context) {
        super(context);
    }

    /**
     * 绑定已有布局
     *
     * @param layoutId 要求布局中必须包含的TextView以及id有：tv_title，tv_content，tv_cancel，tv_confirm
     * @return
     */
    public ConfirmPopupView bindLayout(int layoutId) {
        bindLayoutId = layoutId;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId != 0 ? bindLayoutId : Popup.confirmLayoutId;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);
        et_input = findViewById(R.id.et_input);

        if (bindLayoutId == 0) {
            applyPrimaryColor();
        }

        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(GONE);
        }

        if (!TextUtils.isEmpty(content)) {
            tv_content.setText(content);
        } else {
            tv_content.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(cancelText)) {
            tv_cancel.setText(cancelText);
        } else {
            tv_cancel.setText(getResources().getString(R.string.cancel));
        }
        if (!TextUtils.isEmpty(confirmText)) {
            tv_confirm.setText(confirmText);
        } else {
            tv_cancel.setText(getResources().getString(R.string.ok));
        }
        if (isHideCancel) {
            tv_cancel.setVisibility(GONE);
        }
        if (hint != null) {
            et_input.setVisibility(View.VISIBLE);
            tv_content.setVisibility(View.GONE);
            et_input.setHint(hint);
            if (!TextUtils.isEmpty(content)) {
                et_input.setText(content);
            }
        }
    }

    protected void applyPrimaryColor() {
        tv_cancel.setTextColor(Popup.getPrimaryColor());
        tv_confirm.setTextColor(Popup.getPrimaryColor());
    }

    public ConfirmPopupView setListener(IPopupListener confirmListener) {
        this.popupListener = confirmListener;
        return this;
    }

    public ConfirmPopupView setTitleContent(String title, String content, String hint) {
        this.title = title;
        this.content = content;
        this.hint = hint;
        return this;
    }

    public ConfirmPopupView setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public ConfirmPopupView setConfirmText(String confirmText) {
        this.confirmText = confirmText;
        return this;
    }

    public ConfirmPopupView setHintText(String hintText) {
        this.hint = hintText;
        return this;
    }

    public ConfirmPopupView hideCancelBtn() {
        isHideCancel = true;
        return this;
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
}
