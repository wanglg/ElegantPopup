package com.leo.uilib.popup.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.leo.uilib.popup.UPopup;
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
import com.leo.uilib.popup.util.KeyboardUtils;
import com.leo.uilib.popup.util.XPopupUtils;
import com.leo.uilib.popup.util.navbar.NavigationBarObserver;
import com.leo.uilib.popup.util.navbar.OnNavigationBarListener;

import org.json.JSONObject;

import java.util.ArrayList;


public abstract class BasePopupView extends FrameLayout implements OnNavigationBarListener,
        IBack, IPopup, IPopupWindow {
    public PopupInfo popupInfo;
    protected PopupAnimator popupContentAnimator;
    protected ShadowBgAnimator shadowBgAnimator;
    private int touchSlop;
    public PopupStatus popupStatus = PopupStatus.Dismiss;
    private boolean isCreated = false;
    Runnable dismissWithRunnable;
    Runnable dismissRunnable;

    public BasePopupView(@NonNull Context context) {
        super(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        shadowBgAnimator = new ShadowBgAnimator(this);
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
        NavigationBarObserver.getInstance().register(getContext());
        NavigationBarObserver.getInstance().addOnNavigationBarListener(this);

        //1. 初始化Popup
        if (!isCreated) {
            initPopupContent();
        }
        //apply size dynamic
        if (!(this instanceof FullScreenPopupView)) {
            XPopupUtils.setWidthHeight(getTargetSizeView(),
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
            applySize(false);
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

            //3. 初始化动画执行器
            shadowBgAnimator.initAnimator();
            if (popupContentAnimator != null) {
                popupContentAnimator.initAnimator();
            }
        }
    }

    @Override
    public void onNavigationBarChange(boolean show) {
        if (!show) {
            applyFull();
        } else {
            applySize(true);
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    public void initData(JSONObject jsonObject) {

    }

    protected void applyFull() {
        FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.topMargin = 0;
        params.leftMargin = 0;
        params.bottomMargin = 0;
        params.rightMargin = 0;
        setLayoutParams(params);
    }

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
            }
        }
        return interceptShow;
    }

    protected void applySize(boolean isShowNavBar) {
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        boolean isNavBarShown = isShowNavBar || XPopupUtils.isNavBarVisible(getContext());
        if (rotation == 0) {
            params.leftMargin = 0;
            params.rightMargin = 0;
            params.bottomMargin = isNavBarShown ? XPopupUtils.getNavBarHeight() : 0;
        } else if (rotation == 1) {
            params.bottomMargin = 0;
            params.rightMargin = isNavBarShown ? XPopupUtils.getNavBarHeight() : 0;
            params.leftMargin = 0;
        } else if (rotation == 3) {
            params.bottomMargin = 0;
            params.leftMargin = 0;
            params.rightMargin = isNavBarShown ? XPopupUtils.getNavBarHeight() : 0;
        }
        setLayoutParams(params);
    }

    @Override
    public BasePopupView showPopup() {
        if (getParent() != null) {
            return this;
        }
        final Activity activity = (Activity) getContext();
        if (popupInfo.decorView == null) {
            popupInfo.decorView = (ViewGroup) activity.getWindow().getDecorView();
        }

        Runnable runnable = () -> {
            if (getParent() != null) {
                ((ViewGroup) getParent()).removeView(BasePopupView.this);
            }
            if (beforeShow()) {
                return;
            }
            if (popupInfo.observeSoftKeyboard) {
                KeyboardUtils.registerSoftInputChangedListener(this, height -> {
                    if (popupInfo.popupType == PopupType.AttachView) {
                        return;
                    }
                    if (height == 0) { // 说明对话框隐藏
                        XPopupUtils.moveDown(BasePopupView.this);
                        hasMoveUp = false;
                    } else {
                        //when show keyboard, move up
                        XPopupUtils.moveUpToKeyboard(height, BasePopupView.this);
                        hasMoveUp = true;
                    }
                });
            }

            if (popupInfo.popupListener != null) {
                popupInfo.popupListener.beforeShow();
            }
            popupInfo.decorView.addView(BasePopupView.this, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            PopupManager.getPopupManager().addPopup(getContext(), this);
            //2. do init，game start.

            init();
        };
        //确保measured后使用
        if (popupInfo.immediateAdd) {
            runnable.run();
        } else {
            // 1. add PopupView to its decorView after measured.
            popupInfo.decorView.post(runnable);
        }

        return this;
    }

    protected void doAfterShow() {
        removeCallbacks(doAfterShowTask);
        postDelayed(doAfterShowTask, getAnimationDuration());
    }

    private Runnable doAfterShowTask = new Runnable() {
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
            if (XPopupUtils.getDecorViewInvisibleHeight((Activity) getContext()) > 0 && !hasMoveUp) {
                XPopupUtils.moveUpToKeyboard(XPopupUtils.getDecorViewInvisibleHeight((Activity) getContext()), BasePopupView.this);
            }
        }
    };

    private ShowSoftInputTask showSoftInputTask;

    public void focusAndProcessBackPress() {
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
        XPopupUtils.findAllEditText(list, (ViewGroup) getPopupContentView());
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

    class ShowSoftInputTask implements Runnable {
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
        if (popupInfo.popupType != null && popupInfo.popupType == PopupType.AttachView) {
            return false;
        }
        if (popupInfo.isDismissOnBackPressed) {
            dismissOrHideSoftInput();
        }
        return true;
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
                return new ScaleAlphaAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case TranslateAlphaFromLeft:
            case TranslateAlphaFromTop:
            case TranslateAlphaFromRight:
            case TranslateAlphaFromBottom:
                return new TranslateAlphaAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case TranslateFromLeft:
            case TranslateFromTop:
            case TranslateFromRight:
            case TranslateFromBottom:
                return new TranslateAnimator(getPopupContentView(), popupInfo.popupAnimation);

            case ScrollAlphaFromLeft:
            case ScrollAlphaFromLeftTop:
            case ScrollAlphaFromTop:
            case ScrollAlphaFromRightTop:
            case ScrollAlphaFromRight:
            case ScrollAlphaFromRightBottom:
            case ScrollAlphaFromBottom:
            case ScrollAlphaFromLeftBottom:
                return new ScrollScaleAnimator(getPopupContentView(), popupInfo.popupAnimation);
            case NoAnimation:
                return new EmptyAnimator();
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
        if (popupInfo.hasShadowBg) {
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
        return popupInfo.popupAnimation == PopupAnimation.NoAnimation ? 10 : UPopup.getAnimationDuration();
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

    private Runnable doAfterDismissTask = new Runnable() {
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
            NavigationBarObserver.getInstance().removeOnNavigationBarListener(BasePopupView.this);

            if (popupInfo != null && popupInfo.isRequestFocus) {
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
            if (popupInfo != null && popupInfo.decorView != null) {
                popupInfo.decorView.removeView(BasePopupView.this);
                if (popupInfo.observeSoftKeyboard) {
                    KeyboardUtils.removeLayoutChangeListener(BasePopupView.this);
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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(doAfterShowTask);
        removeCallbacks(doAfterDismissTask);
        if (popupInfo.observeSoftKeyboard) {
            KeyboardUtils.removeLayoutChangeListener(BasePopupView.this);
        }
        if (showSoftInputTask != null) {
            removeCallbacks(showSoftInputTask);
        }
        popupStatus = PopupStatus.Dismiss;
        showSoftInputTask = null;
        hasMoveUp = false;
    }

    private float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (popupInfo.popupType != null && popupInfo.popupType == PopupType.AttachView) {
            return super.onTouchEvent(event);
        }
        // 如果自己接触到了点击，并且不在PopupContentView范围内点击，则进行判断是否是点击事件,如果是，则dismiss
        Rect rect = new Rect();
        getPopupContentView().getGlobalVisibleRect(rect);
        if (!XPopupUtils.isInRect(event.getX(), event.getY(), rect)) {
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


    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
