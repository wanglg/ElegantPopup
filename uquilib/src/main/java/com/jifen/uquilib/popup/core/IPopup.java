package com.jifen.uquilib.popup.core;

/**
 * @Author: wangliugeng
 * @Date : 2020/3/10
 * @Email: leo3552@163.com
 * @Desciption:
 */
public interface IPopup {

    IPopup show();

    boolean isShow();

    boolean isDismiss();

    void dismiss();

    void toggle();
}
