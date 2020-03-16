package com.lxj.xpopupdemo.fragment;

import android.util.Log;
import android.view.View;

import com.lxj.xpopupdemo.R;
import com.lxj.xpopupdemo.custom.CustomAttachPopup2;
import com.lxj.xpopupdemo.custom.CustomDrawerPopupView;
import com.lxj.xpopupdemo.custom.CustomFullScreenPopup;
import com.lxj.xpopupdemo.custom.PagerBottomPopup;
import com.lxj.xpopupdemo.custom.PagerDrawerPopup;
import com.uq.uilib.popup.UPopup;
import com.uq.uilib.popup.core.SimplePopupListener;
import com.uq.uilib.popup.enums.PopupPosition;
import com.uq.uilib.popup.enums.PopupType;
import com.uq.uilib.popup.impl.LoadingPopupView;


/**
 * Description:
 * Create by lxj, at 2018/12/11
 */
public class QuickStartDemo extends BaseFragment implements View.OnClickListener {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_quickstart;
    }

    @Override
    public void init(View view) {
        view.findViewById(R.id.btnShowConfirm).setOnClickListener(this);
//        view.findViewById(R.id.btnBindLayout).setOnClickListener(this);
//        view.findViewById(R.id.btnShowPosition1).setOnClickListener(this);
//        view.findViewById(R.id.btnShowPosition2).setOnClickListener(this);
//        view.findViewById(R.id.btnShowInputConfirm).setOnClickListener(this);
//        view.findViewById(R.id.btnShowCenterList).setOnClickListener(this);
//        view.findViewById(R.id.btnShowCenterListWithCheck).setOnClickListener(this);
        view.findViewById(R.id.btnShowLoading).setOnClickListener(this);
//        view.findViewById(R.id.btnShowBottomList).setOnClickListener(this);
//        view.findViewById(R.id.btnShowBottomListWithCheck).setOnClickListener(this);
        view.findViewById(R.id.btnShowDrawerLeft).setOnClickListener(this);
        view.findViewById(R.id.btnShowDrawerRight).setOnClickListener(this);
//        view.findViewById(R.id.btnCustomBottomPopup).setOnClickListener(this);
        view.findViewById(R.id.btnPagerBottomPopup).setOnClickListener(this);
//        view.findViewById(R.id.btnCustomEditPopup).setOnClickListener(this);
        view.findViewById(R.id.btnFullScreenPopup).setOnClickListener(this);
//        view.findViewById(R.id.btnAttachPopup1).setOnClickListener(this);
//        view.findViewById(R.id.btnAttachPopup2).setOnClickListener(this);
        view.findViewById(R.id.tv1).setOnClickListener(this);
//        view.findViewById(R.id.tv2).setOnClickListener(this);
//        view.findViewById(R.id.tv3).setOnClickListener(this);
        drawerPopupView = new CustomDrawerPopupView(getContext());

    }

    CustomDrawerPopupView drawerPopupView;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShowConfirm: //带确认和取消按钮的弹窗
                new UPopup.Builder(getContext())
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
//                         .autoDismiss(false)
//                        .popupAnimation(PopupAnimation.NoAnimation)
                        .setListener(new SimplePopupListener() {
                            @Override
                            public void onCreated() {
                                Log.e("tag", "弹窗创建了");
                            }

                            @Override
                            public void onShow() {
                                Log.e("tag", "onShow");
                            }

                            @Override
                            public void onDismiss() {
                                Log.e("tag", "onDismiss");
                            }

                            //如果你自己想拦截返回按键事件，则重写这个方法，返回true即可
//                            @Override
//                            public boolean onBackPressed() {
//                                ToastUtils.showShort("我拦截的返回按键，按返回键XPopup不会关闭了");
//                                return true;
//                            }
                        }).asConfirm("我是标题", "床前明月光，疑是地上霜；举头望明月，低头思故乡。",
                        "取消", "确定"
                        , null, false)
                        .show();
                break;


            case R.id.btnShowLoading: //在中间弹出的Loading加载框
                final LoadingPopupView loadingPopup = (LoadingPopupView) new UPopup.Builder(getContext())
                        .hasShadowBg(false)
                        .asLoading("正在加载中")
                        .show();
                loadingPopup.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingPopup.setTitle("正在加载中啊啊啊");
                    }
                }, 1000);
                loadingPopup.delayDismissWith(3000, new Runnable() {
                    @Override
                    public void run() {
                        toast("我消失了！！！");
                    }
                });
                break;


            case R.id.btnPagerBottomPopup: //自定义的底部弹窗
                new UPopup.Builder(getContext())
                        .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                        .asCustom(new PagerBottomPopup(getContext()))
                        .show();
                break;
            case R.id.tv1:
                new UPopup.Builder(getContext())
                        .atView(v)
                        .popupType(PopupType.AttachView)
                        .popupPosition(PopupPosition.Top)
                        .hasShadowBg(false) // 去掉半透明背景
                        .asCustom(new CustomAttachPopup2(getContext()))
                        .show();
                break;
            case R.id.btnShowDrawerLeft: //像DrawerLayout一样的Drawer弹窗
                new UPopup.Builder(getContext())
//                        .asCustom(new CustomDrawerPopupView(getContext()))
//                        .hasShadowBg(false)
                        .asCustom(new PagerDrawerPopup(getContext()))
//                        .asCustom(new ListDrawerPopupView(getContext()))
                        .show();
                break;
            case R.id.btnShowDrawerRight:
                new UPopup.Builder(getContext())
                        .popupPosition(PopupPosition.Right)//右边
                        .hasStatusBarShadow(true) //启用状态栏阴影
                        .asCustom(drawerPopupView)
                        .show();
                break;
            case R.id.btnFullScreenPopup: //全屏弹窗，看起来像Activity
                new UPopup.Builder(getContext())
                        .hasStatusBarShadow(true)
                        .autoOpenSoftInput(true)
                        .asCustom(new CustomFullScreenPopup(getContext()))
                        .show();
                break;

        }
    }


}
