package com.uq.uilib.popup.core;

import android.view.View;

/**
 * @Author: wangliugeng
 * @Date : 2020/3/10
 * @Email: leo3552@163.com
 * @Desciption:
 */
public interface IPopupListener extends View.OnClickListener{

    /**
     * 弹窗的onCreate方法执行完调用
     */
    void onCreated();

    /**
     * 在show之前执行，由于onCreated只执行一次，如果想多次更新数据可以在该方法中
     */
    void beforeShow();

    /**
     * 完全显示的时候执行
     */
    void onShow();

    /**
     * 完全消失的时候执行
     */
    void onDismiss();

    /**
     * 手动确认
     */
    void onConfirm();

    /**
     * 手动取消
     */
    void onCancel();

    /**
     * 点击外部区域
     */

    void onClickOutside();
}
