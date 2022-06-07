package com.leo.uilib.popup.core;

import android.os.Bundle;

import org.json.JSONObject;

/**
 * @Author: wangliugeng
 * @Date : 2020/3/21
 * @Email: leo3552@163.com
 * @Desciption:
 */
public interface IPopupWindow {

    int getLayoutId();

    void initData(Bundle extData);

    void configViews();

    void requestData();
}
