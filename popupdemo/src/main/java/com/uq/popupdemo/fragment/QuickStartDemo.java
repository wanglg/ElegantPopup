package com.uq.popupdemo.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.leo.uilib.popup.Popup;
import com.leo.uilib.popup.core.SimplePopupListener;
import com.leo.uilib.popup.enums.LaunchModel;
import com.leo.uilib.popup.enums.PopupPosition;
import com.leo.uilib.popup.impl.LoadingPopupView;
import com.uq.popupdemo.R;
import com.uq.popupdemo.custom.CustomAttachPopup2;
import com.uq.popupdemo.custom.CustomDrawerPopupView;
import com.uq.popupdemo.custom.CustomFullScreenDialog;
import com.uq.popupdemo.custom.CustomFullScreenPopup;
import com.uq.popupdemo.custom.CustomToplPopup;
import com.uq.popupdemo.custom.PagerBottomDiloag;
import com.uq.popupdemo.custom.PagerBottomPopup;
import com.uq.popupdemo.custom.PagerDrawerPopup;


public class QuickStartDemo extends BaseFragment implements View.OnClickListener {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_quickstart;
    }

    PagerBottomPopup pagerBottomPopup;

    @Override
    public void init(View view) {
        view.findViewById(R.id.btnShowConfirm).setOnClickListener(this);
        view.findViewById(R.id.btnPositionMsg).setOnClickListener(this);
        view.findViewById(R.id.btnShowLoading).setOnClickListener(this);
        view.findViewById(R.id.btnShowDrawerLeft).setOnClickListener(this);
        view.findViewById(R.id.btnShowDrawerRight).setOnClickListener(this);
        view.findViewById(R.id.btnPagerBottomPopup).setOnClickListener(this);
        view.findViewById(R.id.btnPagerBottomDialog).setOnClickListener(this);
        view.findViewById(R.id.btnFullScreenPopup).setOnClickListener(this);
        view.findViewById(R.id.btnFullScreenDialog).setOnClickListener(this);
        view.findViewById(R.id.launchDrop).setOnClickListener(this);
        view.findViewById(R.id.launchLatest).setOnClickListener(this);
        view.findViewById(R.id.launchBuffer).setOnClickListener(this);
        view.findViewById(R.id.tv1).setOnClickListener(this);
        view.findViewById(R.id.reuseDialog).setOnClickListener(this);
        drawerPopupView = new CustomDrawerPopupView(getContext());

    }

    CustomDrawerPopupView drawerPopupView;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShowConfirm: //带确认和取消按钮的弹窗
//                new Popup.Builder(getContext())
//                        .isViewMode(false)
//                        .shadowBgColor(Color.parseColor("#33000000"))
//                        .asConfirm(
//                                "我是标题",
//                                "我是内容。",
//                                "取消",
//                                "确定"
//                                , null,
//                                false)
//                        .showPopup();

                new Popup.Builder(getContext())
                        .isViewMode(false)
                        .shadowBgColor(Color.parseColor("#33000000"))
                        .asConfirm(
                                "我是标题",
                                "我是内容。",
                                "我是hint",
                                "取消",
                                "确定"
                                , null,
                                false)
                        .showPopup();
                break;


            case R.id.btnShowLoading: //在中间弹出的Loading加载框
                final LoadingPopupView loadingPopup = (LoadingPopupView) new Popup.Builder(getContext())
                        .hasShadowBg(false)
                        .asLoading("正在加载中")
                        .showPopup();
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
                new Popup.Builder(getContext())
//                        .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                        .enableDrag(false)
//                        .shadowBgColor(Color.parseColor("#33000000"))
                        .asCustom(new PagerBottomPopup(getContext()))
                        .showPopup();
                break;
            case R.id.btnPositionMsg: //自定义的底部弹窗
                new Popup.Builder(getContext())
                        .hasShadowBg(false)
                        .offsetY(200)
                        .offsetX(100)
                        .popupPosition(PopupPosition.Top_Right)
                        .asCustom(new CustomToplPopup(getContext()))
                        .showPopup();
                break;
            case R.id.btnPagerBottomDialog: //自定义的底部弹窗
                new Popup.Builder(getContext())
//                        .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                        .asCustom(new PagerBottomDiloag(getContext()))
                        .showPopup();
//                PagerBottomSheetDiloag diloag=new PagerBottomSheetDiloag(getContext());
//                diloag.show();
                break;
            case R.id.reuseDialog: //自定义的底部弹窗
                if (pagerBottomPopup == null) {
                    pagerBottomPopup = new PagerBottomPopup(getContext());
                }
                new Popup.Builder(getContext())
                        .enableDrag(true)
                        .asCustom(pagerBottomPopup)
                        .showPopup();

                break;
            case R.id.tv1:
                new Popup.Builder(getContext())
                        .atView(v)
//                        .popupType(PopupType.AttachView)
                        .interceptTouchEvent(false)
                        .popupPosition(PopupPosition.Top)
                        .hasShadowBg(false) // 去掉半透明背景
                        .asCustom(new CustomAttachPopup2(getContext()))
                        .showPopup();
                break;
            case R.id.btnShowDrawerLeft: //像DrawerLayout一样的Drawer弹窗
                new Popup.Builder(getContext())
//                        .asCustom(new CustomDrawerPopupView(getContext()))
                        .hasShadowBg(false)
                        .asCustom(new PagerDrawerPopup(getContext()))
//                        .asCustom(new ListDrawerPopupView(getContext()))
                        .showPopup();
                break;
            case R.id.btnShowDrawerRight:
                new Popup.Builder(getContext())
                        .popupPosition(PopupPosition.Right)//右边
                        .hasStatusBarShadow(true) //启用状态栏阴影
                        .hasShadowBg(true)
                        .asCustom(drawerPopupView)
                        .showPopup();
                break;
            case R.id.btnFullScreenPopup: //全屏弹窗，看起来像Activity
                new Popup.Builder(getContext())
                        .hasStatusBarShadow(true)
                        .autoOpenSoftInput(true)
                        .asCustom(new CustomFullScreenPopup(getContext()))
                        .showPopup();
                break;
            case R.id.btnFullScreenDialog: //全屏弹窗，看起来像Activity
                new Popup.Builder(getContext())
                        .asCustom(new CustomFullScreenDialog(getContext()))
                        .showPopup();
                break;
            case R.id.launchDrop: //全屏弹窗，看起来像Activity
                for (int i = 0; i < 10; i++) {
                    new Popup.Builder(getContext())
                            .dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false)
                            .isViewMode(false)
                            .launchModel(LaunchModel.DROP)
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
                            }).asConfirm("我是标题", "床前明月光，疑是地上霜；举头望明月，低头思故乡。",
                                    "取消", "确定"
                                    , null, false)
                            .showPopup();
                }
                break;
            case R.id.launchLatest: //全屏弹窗，看起来像Activity
                for (int i = 0; i < 10; i++) {
                    new Popup.Builder(getContext())
                            .dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false)
                            .isViewMode(false)
                            .launchModel(LaunchModel.LATEST)
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
                            }).asConfirm("我是标题", "床前明月光，疑是地上霜；举头望明月，低头思故乡。",
                                    "取消", "确定"
                                    , null, false)
                            .showPopup();
                }
                break;
            case R.id.launchBuffer:
                for (int i = 0; i < 5; i++) {
                    new Popup.Builder(getContext())
                            .launchModel(LaunchModel.BUFFER)
                            .setPriority(i == 2 ? -1 : i)
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
                            }).asConfirm("BUFFER" + i, "床前明月光，疑是地上霜；举头望明月，低头思故乡。",
                                    "取消", "确定"
                                    , null, false)
                            .showPopup();
                }
                break;
            default:
                break;

        }
    }


}
