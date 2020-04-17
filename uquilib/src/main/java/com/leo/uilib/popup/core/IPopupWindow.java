package com.leo.uilib.popup.core;

import org.json.JSONObject;

/**
 * @Author: wangliugeng
 * @Date : 2020/3/21
 * @Email: leo3552@163.com
 * @Desciption:
 */
public interface IPopupWindow {

    int getLayoutId();

    void initData(JSONObject jsonObject);

    void configViews();

    void requestData();
}
