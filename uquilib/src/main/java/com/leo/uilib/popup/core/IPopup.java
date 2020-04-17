package com.leo.uilib.popup.core;

/**
 * @Author: wangliugeng
 * @Date : 2020/3/10
 * @Email: leo3552@163.com
 * @Desciption: 窗口控制器
 */
public interface IPopup {

    IPopup showPopup();

    boolean isShow();

    boolean isDismiss();

    void dismiss();

    void toggle();

    PopupInfo getPopupInfo();

    void setPopupInfo(PopupInfo popupInfo);
}
