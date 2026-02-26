package com.leo.uilib.popup.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.leo.uilib.popup.Popup;
import com.leo.uilib.popup.animator.EmptyAnimator;
import com.leo.uilib.popup.animator.PopupAnimator;
import com.leo.uilib.popup.animator.ScaleAlphaAnimator;
import com.leo.uilib.popup.animator.ScrollScaleAnimator;
import com.leo.uilib.popup.animator.ShadowBgAnimator;
import com.leo.uilib.popup.animator.TranslateAlphaAnimator;
import com.leo.uilib.popup.animator.TranslateAnimator;
import com.leo.uilib.popup.core.IBack;
import com.leo.uilib.popup.core.IPopup;
import com.leo.uilib.popup.core.IPopupWindow;
import com.leo.uilib.popup.core.PopupInfo;
import com.leo.uilib.popup.core.PopupManager;
import com.leo.uilib.popup.enums.LaunchModel;
import com.leo.uilib.popup.enums.PopupAnimation;
import com.leo.uilib.popup.enums.PopupStatus;
import com.leo.uilib.popup.enums.PopupType;
import com.leo.uilib.popup.impl.dialog.ElegantHostDialog;
import com.leo.uilib.popup.util.KeyboardUtils;
import com.leo.uilib.popup.util.PopupUtils;
import com.leo.uilib.popup.util.navbar.NavigationBarObserver;
import com.leo.uilib.popup.util.navbar.OnNavigationBarListener;

import java.util.ArrayList;


public abstract class BasePopupView extends FrameLayout implements OnNavigationBarListener,
        IBack, IPopup, IPopupWindow, LifecycleObserver {
    public PopupInfo popupInfo;
    protected PopupAnimator popupContentAnimator;
    protected ShadowBgAnimator shadowBgAnimator;
    private int touchSlop;
    /**
     * 修改前的softmode
     */
    private int preSoftMode = -1;
    public PopupStatus popupStatus = PopupStatus.Dismiss;
    private boolean isCreated = false;
    Runnable dismissWithRunnable;
    Runnable dismissRunnable;

    public BasePopupView(@NonNull Context context) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //  添加Popup窗体内容View
        View contentView = LayoutInflater.from(context).inflate(getLayoutId(), this, false);
        // 事先隐藏，等测量完毕恢复，避免View影子跳动现象。
        contentView.setAlpha(0);
        addView(contentView);
    }

    public BasePopupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BasePopupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 执行初始化
     */
    protected void init() {
        if (popupStatus == PopupStatus.Showing) {
            return;
        }
        popupStatus = PopupStatus.Showing;

        //1. 初始化Popup
        if (!isCreated) {
            initPopupContent();
        }
        //apply size dynamic
        if (!(this instanceof FullScreenPopupView)) {
            PopupUtils.setWidthHeight(getTargetSizeView(),
                    (getMaxWidth() != 0 && getPopupWidth() > getMaxWidth()) ? getMaxWidth() : getPopupWidth(),
                    (getMaxHeight() != 0 && getPopupHeight() > getMaxHeight()) ? getMaxHeight() : getPopupHeight()
            );
        }
        if (!isCreated) {
            isCreated = true;
            initData(popupInfo.extObject);
            configViews();
            if (popupInfo.popupListener != null) {
                popupInfo.popupListener.onCreated();
            }
            requestData();
        }


        Runnable runnable = () -> {
            // 如果有导航栏，则不能覆盖导航栏，判断各种屏幕方向
//            applySize(false);
            getPopupContentView().setAlpha(1f);

            //2. 收集动画执行器
            collectAnimator();

            if (popupInfo.popupListener != null) {
                popupInfo.popupListener.beforeShow();
            }

            //3. 执行动画
            doShowAnimation();

            doAfterShow();

            //目前全屏弹窗快速弹出输入法有问题，暂时用这个方案
            if (!(BasePopupView.this instanceof FullScreenPopupView)) {
                focusAndProcessBackPress();
            }
        };
        if (popupInfo.immediateAdd) {
            runnable.run();
        } else {
            postDelayed(runnable, 50);
        }
    }

    private boolean hasMoveUp = false;

    private void collectAnimator() {
        if (popupContentAnimator == null) {
            // 优先使用自定义的动画器
            if (popupInfo.customAnimator != null) {
                popupContentAnimator = popupInfo.customAnimator;
                popupContentAnimator.targetView = getPopupContentView();
            } else {
                // 根据PopupInfo的popupAnimation字段来生成对应的动画执行器，如果popupAnimation字段为null，则返回null
                popupContentAnimator = genAnimatorByPopupType();
                if (popupContentAnimator == null) {
                    popupContentAnimator = getPopupAnimator();
                }
            }
            if (shadowBgAnimator == null) {
                shadowBgAnimator = new ShadowBgAnimator(this, getAnimationDuration(), getShadowBgColor(), getHostWindow(), popupInfo.navigationBarFollow);
            }
            if (popupInfo != null && popupInfo.hasShadowBg) {
                //3. 初始化动画执行器
                shadowBgAnimator.initAnimator();
            }
            if (popupContentAnimator != null) {
                popupContentAnimator.initAnimator();
            }
        }
    }

    @Override
    public void onNavigationBarChange(boolean show) {
//        if (!show) {
////            applyFull();
//            setSize();
//        } else {
////            applySize(true);
//            setSize();
//        }
        setSize();
        if (shadowBgAnimator != null) {
            shadowBgAnimator.onNavigationBarChange(show);
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    public void initData(Bundle jsonObject) {

    }

    protected void applyFull() {
        MarginLayoutParams params = (LayoutParams) getLayoutParams();
        params.topMargin = 0;
        params.leftMargin = 0;
        params.bottomMargin = 0;
        params.rightMargin = 0;
        setLayoutParams(params);
    }

    /**
     * 展示前判断是不是要拦截
     *
     * @return true拦截 false othrewise
     */
    protected boolean beforeShow() {
        boolean interceptShow = false;
        if (popupInfo != null) {
            if (popupInfo.launchModel == LaunchModel.DROP) {
                IPopup iPopup = PopupManager.getPopupManager().getTopPopup(getContext());
                if (iPopup != null) {
                    if (TextUtils.equals(iPopup.getClass().getName(), getClass().getName())) {
                        interceptShow = true;
                    }
                }
            } else if (popupInfo.launchModel == LaunchModel.LATEST) {
                IPopup iPopup = PopupManager.getPopupManager().getTopPopup(getContext());
                if (iPopup != null) {
                    if (TextUtils.equals(iPopup.getClass().getName(), getClass().getName())) {
                        iPopup.dismiss();
                    }
                }
            } else if (popupInfo.launchModel == LaunchModel.BUFFER) {
                if (popupInfo.priority == 0) {
                    popupInfo.priority = System.currentTimeMillis();
                }
                IPopup iPopup = PopupManager.getPopupManager().getTopCenterPopup(getContext());
                if (iPopup != null) {
                    PopupManager.getPopupManager().addQueue(getContext(), this);
                    interceptShow = true;
                }
            }
        }
        return interceptShow;
    }

//    protected void applySize(boolean isShowNavBar) {
//        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
//        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
//        boolean isNavBarShown = isShowNavBar || PopupUtils.isNavBarVisible(getContext());
//        if (rotation == 0) {
//            params.leftMargin = 0;
//            params.rightMargin = 0;
//            params.bottomMargin = isNavBarShown ? PopupUtils.getNavBarHeight() : 0;
//        } else if (rotation == 1) {
//            params.bottomMargin = 0;
//            params.rightMargin = isNavBarShown ? PopupUtils.getNavBarHeight() : 0;
//            params.leftMargin = 0;
//        } else if (rotation == 3) {
//            params.bottomMargin = 0;
//            params.leftMargin = 0;
//            params.rightMargin = isNavBarShown ? PopupUtils.getNavBarHeight() : 0;
//        }
//        setLayoutParams(params);
//    }

    @Override
    public BasePopupView showPopup() {
        if (popupInfo.isViewMode && getParent() != null) {
            return this;
        }
        if (popupStatus == PopupStatus.Showing || popupStatus == PopupStatus.Dismissing) {
            return this;
        }
        if (!popupInfo.isViewMode) {
            if (hostDialog != null && hostDialog.isShowing()) {
                return this;
            }
        }
        final Activity activity = (Activity) getContext();
        if (popupInfo.anchorView == null) {
            popupInfo.anchorView = (ViewGroup) activity.getWindow().getDecorView();
        }

        Runnable runnable = () -> {
            if (popupStatus == PopupStatus.Showing || popupStatus == PopupStatus.Dismissing) {
                return;
            }
            if (popupInfo.isViewMode && getParent() != null) {
                ((ViewGroup) getParent()).removeView(BasePopupView.this);
            }
            if (beforeShow()) {
                return;
            }
            if (popupInfo.popupListener != null) {
                popupInfo.popupListener.beforeShow();
            }
            attachToHost();
            PopupManager.getPopupManager().addPopup(getContext(), this);

            init();
        };
        //确保measured后使用
        if (popupInfo.immediateAdd) {
            runnable.run();
        } else {
            // 1. add PopupView to its decorView after measured.
            popupInfo.anchorView.post(runnable);
        }

        return this;
    }

    ElegantHostDialog hostDialog;

    private void attachToHost() {
        setSize();
        if (popupInfo.isViewMode) {
            popupInfo.anchorView.addView(BasePopupView.this);
        } else {
            if (hostDialog == null) {
                hostDialog = new ElegantHostDialog(getContext()).setContent(this);
            }
            if (!hostDialog.isShowing()) {
                hostDialog.show();
            }
        }
    }

    private void setSize() {
        int navHeight = 0;
        View decorView = PopupUtils.context2Activity(this).getWindow().getDecorView();
        if (decorView == popupInfo.anchorView) {
            //设置自己的大小，和Activity的contentView保持一致
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                View navBarView = decorView.findViewById(android.R.id.navigationBarBackground);
                if (navBarView != null) {
                    navHeight = PopupUtils.isLandscape(getContext()) && !PopupUtils.isTablet() ?
                            navBarView.getMeasuredWidth() : navBarView.getMeasuredHeight();
                }
            } else {
                navHeight = PopupUtils.isNavBarVisible(PopupUtils.context2Activity(this).getWindow()) ?
                        PopupUtils.getNavBarHeight() : 0;
            }
            View activityContent = getActivityContentView();
            if (getLayoutParams() == null) {
                ViewGroup.MarginLayoutParams params = new MarginLayoutParams(activityContent.getMeasuredWidth(),
                        decorView.getMeasuredHeight() -
                                (PopupUtils.isLandscape(getContext()) && !PopupUtils.isTablet() ? 0 : navHeight));
                if (PopupUtils.isLandscape(getContext())) {
                    params.leftMargin = getActivityContentLeft();
                }
                setLayoutParams(params);
            } else {
                ViewGroup.LayoutParams vp = getLayoutParams();
                vp.height = decorView.getMeasuredHeight() -
                        (PopupUtils.isLandscape(getContext()) && !PopupUtils.isTablet() ? 0 : navHeight);
                vp.width = activityContent.getMeasuredWidth();
                setLayoutParams(vp);
            }

        } else {
            if (getLayoutParams() == null) {
                ViewGroup.MarginLayoutParams params = new MarginLayoutParams(popupInfo.anchorView.getMeasuredWidth(),
                        popupInfo.anchorView.getMeasuredHeight());
                setLayoutParams(params);
            } else {
                ViewGroup.LayoutParams vp = getLayoutParams();
                vp.height = popupInfo.anchorView.getMeasuredHeight();
                vp.width = popupInfo.anchorView.getMeasuredWidth();
                setLayoutParams(vp);
            }

        }

    }

    public View getActivityContentView() {
        return PopupUtils.context2Activity(this).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    protected int getActivityContentLeft() {
        if (!PopupUtils.isLandscape(getContext())) {
            return 0;
        }
        //以Activity的content的left为准
        View decorView = PopupUtils.context2Activity(this).getWindow().getDecorView().findViewById(android.R.id.content);
        int[] loc = new int[2];
        decorView.getLocationInWindow(loc);
        return loc[0];
    }

    protected void detachFromHost() {
        if (popupInfo.isViewMode) {
            popupInfo.anchorView.removeView(this);
        } else {
            try {
                if (hostDialog != null) {
                    hostDialog.dismiss();
                    hostDialog = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        shadowBgAnimator = null;
        popupContentAnimator = null;
    }

    public Window getHostWindow() {
        if (popupInfo != null && popupInfo.isViewMode && getContext() instanceof Activity) {
            return ((Activity) getContext()).getWindow();
        }
        return hostDialog == null ? null : hostDialog.getWindow();
    }

    protected void doAfterShow() {
        removeCallbacks(doAfterShowTask);
        postDelayed(doAfterShowTask, getAnimationDuration());
    }

    private final Runnable doAfterShowTask = new Runnable() {
        @Override
        public void run() {
            popupStatus = PopupStatus.Show;
            onShow();
            if (BasePopupView.this instanceof FullScreenPopupView) {
                focusAndProcessBackPress();
            }
            if (popupInfo != null && popupInfo.popupListener != null) {
                popupInfo.popupListener.onShow();
            }
            int invisibleHeight = PopupUtils.getDecorViewInvisibleHeight((Activity) getContext());
            if (invisibleHeight > 0 && !hasMoveUp) {
                PopupUtils.moveUpToKeyboard(invisibleHeight, BasePopupView.this);
            }
            if (popupInfo.observeSoftKeyboard) {
                if (popupInfo.isViewMode) {
                    preSoftMode = getHostWindow().getAttributes().softInputMode;
                    getHostWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
                KeyboardUtils.registerSoftInputChangedListener(getHostWindow(), BasePopupView.this, height -> {
                    if (popupInfo.popupType == PopupType.AttachView) {
                        return;
                    }
                    if (height == 0) { // 说明对话框隐藏
                        PopupUtils.moveDown(BasePopupView.this);
                        hasMoveUp = false;
                    } else {
                        //when show keyboard, move up
                        PopupUtils.moveUpToKeyboard(height, BasePopupView.this);
                        hasMoveUp = true;
                    }
                });
            }
        }
    };

    private ShowSoftInputTask showSoftInputTask;

    public void focusAndProcessBackPress() {
        if (!popupInfo.isViewMode) {
            return;
        }
        if (popupInfo.isRequestFocus) {
            setFocusableInTouchMode(true);
            requestFocus();
        }
        // 此处焦点可能被内容的EditText抢走，也需要给EditText也设置返回按下监听
        setOnKeyListener(new BackPressListener());
        if (!popupInfo.autoFocusEditText) {
            showSoftInput(this);
        }

        //let all EditText can process back pressed.
        ArrayList<EditText> list = new ArrayList<>();
        PopupUtils.findAllEditText(list, (ViewGroup) getPopupContentView());
        for (int i = 0; i < list.size(); i++) {
            final EditText et = list.get(i);
            et.setOnKeyListener(new BackPressListener());
            if (i == 0 && popupInfo.autoFocusEditText) {
                et.setFocusable(true);
                et.setFocusableInTouchMode(true);
                et.requestFocus();
                showSoftInput(et);
            }
        }
    }

    protected void showSoftInput(View focusView) {
        if (popupInfo.autoOpenSoftInput) {
            if (showSoftInputTask == null) {
                showSoftInputTask = new ShowSoftInputTask(focusView);
            } else {
                removeCallbacks(showSoftInputTask);
            }
            postDelayed(showSoftInputTask, 10);
        }
    }

    protected void dismissOrHideSoftInput() {
        if (getContext() instanceof Activity) {
            if (KeyboardUtils.isSoftInputVisible((Activity) getContext())) {
                KeyboardUtils.hideSoftInput(BasePopupView.this);
            } else {
                dismiss();
            }
        }
    }

    static class ShowSoftInputTask implements Runnable {
        View focusView;
        boolean isDone = false;

        public ShowSoftInputTask(View focusView) {
            this.focusView = focusView;
        }

        @Override
        public void run() {
            if (focusView != null && !isDone) {
                isDone = true;
                KeyboardUtils.showSoftInput(focusView);
            }
        }
    }

    class BackPressListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                return onBack();
            }
            return false;
        }
    }

    @Override
    public boolean onBack() {
        if (popupInfo.popupType != null && !popupInfo.interceptTouchEvent) {
            return false;
        }
        if (popupInfo.isDismissOnBackPressed) {
            dismissOrHideSoftInput();
        }
        return true;
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        detachFromHost();
    }

    /**
     * 根据PopupInfo的popupAnimation字段来生成对应的内置的动画执行器
     */
    protected PopupAnimator genAnimatorByPopupType() {
        if (popupInfo == null || popupInfo.popupAnimation == null) {
            return null;
        }
        switch (popupInfo.popupAnimation) {
            case ScaleAlphaFromCenter:
            case ScaleAlphaFromLeftTop:
            case ScaleAlphaFromRightTop:
            case ScaleAlphaFromLeftBottom:
            case ScaleAlphaFromRightBottom:
                return new ScaleAlphaAnimator(getPopupContentView(), getAnimationDuration(), popupInfo.popupAnimation);

            case TranslateAlphaFromLeft:
            case TranslateAlphaFromTop:
            case TranslateAlphaFromRight:
            case TranslateAlphaFromBottom:
                return new TranslateAlphaAnimator(getPopupContentView(), getAnimationDuration(), popupInfo.popupAnimation);

            case TranslateFromLeft:
            case TranslateFromTop:
            case TranslateFromRight:
            case TranslateFromBottom:
                return new TranslateAnimator(getPopupContentView(), getAnimationDuration(), popupInfo.popupAnimation);
            case ScrollAlphaFromLeft:
            case ScrollAlphaFromLeftTop:
            case ScrollAlphaFromTop:
            case ScrollAlphaFromRightTop:
            case ScrollAlphaFromRight:
            case ScrollAlphaFromRightBottom:
            case ScrollAlphaFromBottom:
            case ScrollAlphaFromLeftBottom:
                return new ScrollScaleAnimator(getPopupContentView(), getAnimationDuration(), popupInfo.popupAnimation);
            default:
                return new EmptyAnimator();
        }
    }


    protected int getImplLayoutId() {
        return -1;
    }

    /**
     * 获取PopupAnimator，用于每种类型的PopupView自定义自己的动画器
     *
     * @return
     */
    protected PopupAnimator getPopupAnimator() {
        return null;
    }

    /**
     * 请使用onCreate，主要给弹窗内部用，不要去重写。
     */
    protected void initPopupContent() {
    }

    @Override
    public void configViews() {

    }

    /**
     * 执行显示动画：动画由2部分组成，一个是背景渐变动画，一个是Content的动画；
     * 背景动画由父类实现，Content由子类实现
     */
    protected void doShowAnimation() {
        if (popupInfo.hasShadowBg) {
            shadowBgAnimator.isZeroDuration = (popupInfo.popupAnimation == PopupAnimation.NoAnimation);
            shadowBgAnimator.animateShow();
        }
        if (popupContentAnimator != null) {
            popupContentAnimator.animateShow();
        }
    }

    /**
     * 执行消失动画：动画由2部分组成，一个是背景渐变动画，一个是Content的动画；
     * 背景动画由父类实现，Content由子类实现
     */
    protected void doDismissAnimation() {
        if (popupInfo.hasShadowBg && shadowBgAnimator != null) {
            shadowBgAnimator.animateDismiss();
        }
        if (popupContentAnimator != null) {
            popupContentAnimator.animateDismiss();
        }
    }

    /**
     * 获取内容View，本质上PopupView显示的内容都在这个View内部。
     * 而且我们对PopupView执行的动画，也是对它执行的动画
     *
     * @return
     */
    public View getPopupContentView() {
        return getChildAt(0);
    }

    public View getPopupImplView() {
        return ((ViewGroup) getPopupContentView()).getChildAt(0);
    }

    public int getAnimationDuration() {
        return popupInfo.popupAnimation == PopupAnimation.NoAnimation ? 10 : Popup.getAnimationDuration();
    }

    public int getShadowBgColor() {
        return popupInfo != null && popupInfo.shadowBgColor != 0 ? popupInfo.shadowBgColor : Popup.getShadowBgColor();
    }

    /**
     * 弹窗的最大宽度，一般用来限制布局宽度为wrap或者match时的最大宽度
     *
     * @return
     */
    protected int getMaxWidth() {
        return 0;
    }

    /**
     * 弹窗的最大高度，一般用来限制布局高度为wrap或者match时的最大宽度
     *
     * @return
     */
    protected int getMaxHeight() {
        return popupInfo.maxHeight;
    }

    /**
     * 弹窗的宽度，用来动态设定当前弹窗的宽度，受getMaxWidth()限制
     *
     * @return
     */
    protected int getPopupWidth() {
        return 0;
    }

    /**
     * 弹窗的高度，用来动态设定当前弹窗的高度，受getMaxHeight()限制
     *
     * @return
     */
    protected int getPopupHeight() {
        return 0;
    }

    protected View getTargetSizeView() {
        return getPopupContentView();
    }

    /**
     * 消失
     */
    @Override
    public void dismiss() {
        if (popupStatus == PopupStatus.Dismissing || popupStatus == PopupStatus.Dismiss) {
            return;
        }
        popupStatus = PopupStatus.Dismissing;
        if (popupInfo.autoOpenSoftInput) {
            KeyboardUtils.hideSoftInput(this);
        }
        if (hasMoveUp) {
            PopupUtils.moveDown(this);
        }
        clearFocus();
        doDismissAnimation();
        doAfterDismiss();
    }

    public void delayDismiss(long delay) {
        if (delay < 0) {
            delay = 0;
        }
        dismissRunnable = this::dismiss;
        postDelayed(dismissRunnable, delay);
    }

    public void delayDismissWith(long delay, Runnable runnable) {
        this.dismissWithRunnable = runnable;
        delayDismiss(delay);
    }

    protected void doAfterDismiss() {
        if (popupInfo.autoOpenSoftInput) {
            KeyboardUtils.hideSoftInput(this);
        }
        removeCallbacks(doAfterDismissTask);
        postDelayed(doAfterDismissTask, getAnimationDuration());
    }

    private final Runnable doAfterDismissTask = new Runnable() {
        @Override
        public void run() {
            onDismiss();
            if (popupInfo != null && popupInfo.popupListener != null) {
                popupInfo.popupListener.onDismiss();
            }
            if (dismissWithRunnable != null) {
                dismissWithRunnable.run();
                dismissWithRunnable = null;//no cache, avoid some bad edge effect.
            }
            if (dismissRunnable != null) {
                removeCallbacks(dismissRunnable);
                dismissRunnable = null;
            }
            popupStatus = PopupStatus.Dismiss;

            if (popupInfo != null && popupInfo.isRequestFocus && popupInfo.isViewMode) {
                IPopup popup = PopupManager.getPopupManager().getTopPopup(getContext());
                if (popup instanceof BasePopupView) {
                    ((BasePopupView) popup).focusAndProcessBackPress();
                } else if (getContext() != null && getContext() instanceof Activity) {
                    // 让根布局拿焦点，避免布局内RecyclerView类似布局获取焦点导致布局滚动
                    View needFocusView = ((Activity) getContext()).findViewById(android.R.id.content);
                    if (needFocusView != null) {
                        needFocusView.setFocusable(true);
                        needFocusView.setFocusableInTouchMode(true);
                    }
                }
            }

            // 移除弹窗，GameOver
            detachFromHost();

            //展示缓存的弹框
            if (getContext() instanceof Activity) {
                Activity activity = (Activity) getContext();
                if (!activity.isFinishing()) {
                    IPopup queuePopup = PopupManager.getPopupManager().getTopQueuePopup(getContext());
                    if (queuePopup != null) {
                        queuePopup.showPopup();
                    }
                }
            }

        }
    };


    public void dismissWith(Runnable runnable) {
        this.dismissWithRunnable = runnable;
        dismiss();
    }

    @Override
    public boolean isShow() {
        return popupStatus != PopupStatus.Dismiss;
    }

    @Override
    public boolean isDismiss() {
        return popupStatus == PopupStatus.Dismiss;
    }

    @Override
    public void toggle() {
        if (isShow()) {
            dismiss();
        } else {
            showPopup();
        }
    }

    /**
     * 消失动画执行完毕后执行
     */
    protected void onDismiss() {
        PopupManager.getPopupManager().removePopup(getContext(), this);
    }

    /**
     * 显示动画执行完毕后执行
     */
    protected void onShow() {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NavigationBarObserver.getInstance().register(getContext());
        NavigationBarObserver.getInstance().addOnNavigationBarListener(this);
        if (getContext() instanceof FragmentActivity) {
            ((FragmentActivity) getContext()).getLifecycle().addObserver(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(doAfterShowTask);
        removeCallbacks(doAfterDismissTask);
        if (showSoftInputTask != null) {
            removeCallbacks(showSoftInputTask);
        }
        popupStatus = PopupStatus.Dismiss;
        showSoftInputTask = null;
        if (hasMoveUp) {
            //隐藏软键盘
            if (KeyboardUtils.isSoftInputVisible((Activity) getContext())) {
                KeyboardUtils.hideSoftInput(BasePopupView.this);
            }
        }
        hasMoveUp = false;
        NavigationBarObserver.getInstance().removeOnNavigationBarListener(BasePopupView.this);
        if (popupInfo != null) {
            if (popupInfo.observeSoftKeyboard) {
                KeyboardUtils.removeLayoutChangeListener(getHostWindow(), BasePopupView.this);
            }
            if (popupInfo.isViewMode && popupInfo.observeSoftKeyboard && preSoftMode != -1) {
                //还原WindowSoftMode
                getHostWindow().setSoftInputMode(preSoftMode);
            }
        }
        if (getContext() instanceof FragmentActivity) {
            ((FragmentActivity) getContext()).getLifecycle().removeObserver(this);
        }
    }

    private float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (popupInfo != null && !popupInfo.interceptTouchEvent) {
            return super.onTouchEvent(event);
        }
        // 如果自己接触到了点击，并且不在PopupContentView范围内点击，则进行判断是否是点击事件,如果是，则dismiss
        Rect rect = new Rect();
        getPopupContentView().getGlobalVisibleRect(rect);
        if (!PopupUtils.isInRect(event.getX(), event.getY(), rect)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getX();
                    y = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float dx = event.getX() - x;
                    float dy = event.getY() - y;
                    float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                    if (distance < touchSlop && popupInfo.isDismissOnTouchOutside) {
                        dismiss();
                    }
                    x = 0;
                    y = 0;
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    @Override
    public PopupInfo getPopupInfo() {
        return popupInfo;
    }

    @Override
    public void setPopupInfo(PopupInfo popupInfo) {
        this.popupInfo = popupInfo;
    }

    public int getStatusBarBgColor() {
        return popupInfo != null && popupInfo.statusBarBgColor != 0 ? popupInfo.statusBarBgColor : Popup.getStatusBarBgColor();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int compareTo(IPopup o) {
        if (popupInfo == null || o == null || o.getPopupInfo() == null) {
            return 0;
        }
        long currentPriority = popupInfo.priority;
        long comparePriority = o.getPopupInfo().priority;
        return Long.compare(currentPriority, comparePriority);
    }
}
