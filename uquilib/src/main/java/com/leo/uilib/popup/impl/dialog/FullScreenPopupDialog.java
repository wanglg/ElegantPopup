package com.leo.uilib.popup.impl.dialog;

import android.content.Context;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.leo.uilib.popup.R;
import com.leo.uilib.popup.util.PopupUtils;

import org.json.JSONObject;

/**
 * @Author: wangliugeng
 * @Date : 2020/3/21
 * @Email: leo3552@163.com
 * @Desciption:
 */
public abstract class FullScreenPopupDialog extends BasePopupDialog {
    public FullScreenPopupDialog(Context context) {
        super(context, R.style.FullScreenDialog);
    }


    @Override
    public void initData(JSONObject jsonObject) {

    }

    @Override
    public void configViews() {

    }

    @Override
    public void requestData() {

    }

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

    @Override
    protected int getMaxWidth() {
        return PopupUtils.getWindowWidth(mContext);
    }
}
