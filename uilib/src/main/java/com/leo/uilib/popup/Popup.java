package com.leo.uilib.popup;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.leo.uilib.popup.animator.PopupAnimator;
import com.leo.uilib.popup.core.IPopup;
import com.leo.uilib.popup.core.IPopupListener;
import com.leo.uilib.popup.core.PopupInfo;
import com.leo.uilib.popup.enums.LaunchModel;
import com.leo.uilib.popup.enums.PopupAnimation;
import com.leo.uilib.popup.enums.PopupPosition;
import com.leo.uilib.popup.enums.PopupType;
import com.leo.uilib.popup.impl.BottomPopupView;
import com.leo.uilib.popup.impl.CenterPopupView;
import com.leo.uilib.popup.impl.ConfirmPopupView;
import com.leo.uilib.popup.impl.LoadingPopupView;


public class Popup {
    private Popup() {
    }

    /**
     * 全局弹窗的设置
     **/
    private static int primaryColor = Color.parseColor("#121212");
    private static int animationDuration = 360;
    public static int statusBarShadowColor = Color.parseColor("#55000000");
    private static int shadowBgColor = Color.parseColor("#9F000000");
    private static int statusBarBgColor = Color.parseColor("#55000000");

    public static void setShadowBgColor(int color) {
        shadowBgColor = color;
    }

    public static int getShadowBgColor() {
        return shadowBgColor;
    }

    public static int getStatusBarBgColor() {
        return statusBarBgColor;
    }

    /**
     * 全局确认提醒弹框布局
     */
    public static int confirmLayoutId = R.layout.elegant_popup_center_impl_confirm;
    public static float window_width_scale = 0.78f;

    /**
     * 设置主色调
     *
     * @param color
     */
    public static void setPrimaryColor(int color) {
        primaryColor = color;
    }

    public static int getPrimaryColor() {
        return primaryColor;
    }

    public static void setAnimationDuration(int duration) {
        if (duration >= 0) {
            animationDuration = duration;
        }
    }

    public static int getAnimationDuration() {
        return animationDuration;
    }

    public static class Builder {
        private final PopupInfo popupInfo = new PopupInfo();
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder popupType(PopupType popupType) {
            this.popupInfo.popupType = popupType;
            return this;
        }

        /**
         * 设置按下返回键是否关闭弹窗，默认为true
         *
         * @param isDismissOnBackPressed
         * @return
         */
        public Builder dismissOnBackPressed(Boolean isDismissOnBackPressed) {
            this.popupInfo.isDismissOnBackPressed = isDismissOnBackPressed;
            return this;
        }

        /**
         * 设置点击弹窗外面是否关闭弹窗，默认为true
         *
         * @param isDismissOnTouchOutside
         * @return
         */
        public Builder dismissOnTouchOutside(Boolean isDismissOnTouchOutside) {
            this.popupInfo.isDismissOnTouchOutside = isDismissOnTouchOutside;
            return this;
        }

        /**
         * 设置当操作完毕后是否自动关闭弹窗，默认为true。比如：点击Confirm弹窗的确认按钮默认是关闭弹窗，如果为false，则不关闭
         *
         * @param autoDismiss
         * @return
         */
        public Builder autoDismiss(Boolean autoDismiss) {
            this.popupInfo.autoDismiss = autoDismiss;
            return this;
        }

        /**
         * 弹窗是否有半透明背景遮罩，默认是true
         *
         * @param hasShadowBg
         * @return
         */
        public Builder hasShadowBg(Boolean hasShadowBg) {
            this.popupInfo.hasShadowBg = hasShadowBg;
            return this;
        }

        /**
         * 设置弹窗依附的View
         * isViewMode 自动设置true
         *
         * @param atView
         * @return
         */
        public Builder atView(View atView) {
            this.popupInfo.atView = atView;
            this.popupInfo.isViewMode = true;
            return this;
        }

        /**
         * popup弹框的锚点，即根布局，不设置的话默认是decorView
         *
         * @param anchorView
         * @return
         */
        public Builder anchorView(ViewGroup anchorView) {
            this.popupInfo.anchorView = anchorView;
            return this;
        }

        public Builder setPriority(long priority) {
            this.popupInfo.priority = priority;
            return this;
        }

        /**
         * 为弹窗设置内置的动画器，默认情况下，已经为每种弹窗设置了效果最佳的动画器；如果你不喜欢，仍然可以修改。
         *
         * @param popupAnimation
         * @return
         */
        public Builder popupAnimation(PopupAnimation popupAnimation) {
            this.popupInfo.popupAnimation = popupAnimation;
            return this;
        }

        public Builder observeSoftKeyboard(boolean observe) {
            this.popupInfo.observeSoftKeyboard = observe;
            return this;
        }

        public Builder setExtObject(Bundle extObject) {
            this.popupInfo.extObject = extObject;
            return this;
        }

        /**
         * 自定义弹窗动画器
         *
         * @param customAnimator
         * @return
         */
        public Builder customAnimator(PopupAnimator customAnimator) {
            this.popupInfo.customAnimator = customAnimator;
            return this;
        }

        public Builder setListener(IPopupListener listener) {
            this.popupInfo.popupListener = listener;
            return this;
        }

        /**
         * 设置最大宽度，如果重写了弹窗的getMaxWidth，则以重写的为准
         *
         * @param maxWidth
         * @return
         */
        public Builder maxWidth(int maxWidth) {
            this.popupInfo.maxWidth = maxWidth;
            return this;
        }

        /**
         * 设置最大高度，如果重写了弹窗的getMaxHeight，则以重写的为准
         *
         * @param maxHeight
         * @return
         */
        public Builder maxHeight(int maxHeight) {
            this.popupInfo.maxHeight = maxHeight;
            return this;
        }

        /**
         * 是否自动打开输入法，当弹窗包含输入框时很有用，默认为false
         *
         * @param autoOpenSoftInput
         * @return
         */
        public Builder autoOpenSoftInput(Boolean autoOpenSoftInput) {
            this.popupInfo.autoOpenSoftInput = autoOpenSoftInput;
            return this;
        }

        /**
         * 当弹出输入法时，弹窗是否要移动到输入法之上，默认为true。如果不移动，弹窗很有可能被输入法盖住
         *
         * @param isMoveUpToKeyboard
         * @return
         */
        public Builder moveUpToKeyboard(Boolean isMoveUpToKeyboard) {
            this.popupInfo.isMoveUpToKeyboard = isMoveUpToKeyboard;
            return this;
        }

        /**
         * 设置弹窗出现在目标的什么位置，有四种取值：Left，Right，Top，Bottom。这种手动设置位置的行为
         * 只对Attach弹窗和Drawer弹窗生效。
         *
         * @param popupPosition
         * @return
         */
        public Builder popupPosition(PopupPosition popupPosition) {
            this.popupInfo.popupPosition = popupPosition;
            return this;
        }

        /**
         * 是否拦截返回触摸返回事件，默认拦截
         *
         * @param \true 拦截 \false otherwise
         * @return
         */
        public Builder interceptTouchEvent(boolean intercept) {
            this.popupInfo.interceptTouchEvent = intercept;
            return this;
        }

        /**
         * 设置是否给StatusBar添加阴影，目前对Drawer弹窗生效。如果你的Drawer的背景是白色，建议设置为true，因为状态栏文字的颜色也往往
         * 是白色，会导致状态栏文字看不清；如果Drawer的背景色不是白色，则忽略即可
         *
         * @param hasStatusBarShadow
         * @return
         */
        public Builder hasStatusBarShadow(boolean hasStatusBarShadow) {
            this.popupInfo.hasStatusBarShadow = hasStatusBarShadow;
            return this;
        }

        /**
         * 弹窗在x方向的偏移量，对所有弹窗生效，单位是px
         *
         * @param offsetX
         * @return
         */
        public Builder offsetX(int offsetX) {
            this.popupInfo.offsetX = offsetX;
            return this;
        }

        /**
         * 弹窗在y方向的偏移量，对所有弹窗生效，单位是px
         *
         * @param offsetY
         * @return
         */
        public Builder offsetY(int offsetY) {
            this.popupInfo.offsetY = offsetY;
            return this;
        }

        /**
         * 是否启用拖拽，比如：Bottom弹窗默认是带手势拖拽效果的，如果禁用则不能拖拽
         *
         * @param enableDrag
         * @return
         */
        public Builder enableDrag(boolean enableDrag) {
            this.popupInfo.enableDrag = enableDrag;
            return this;
        }

        /**
         * 是否水平居中，默认情况下Attach弹窗依靠着目标的左边或者右边，如果isCenterHorizontal为true，则与目标水平居中对齐
         *
         * @param isCenterHorizontal
         * @return
         */
        public Builder isCenterHorizontal(boolean isCenterHorizontal) {
            this.popupInfo.isCenterHorizontal = isCenterHorizontal;
            return this;
        }

        /**
         * 是否抢占焦点，默认情况下弹窗会抢占焦点，目的是为了能处理返回按键事件。如果为false，则不在抢焦点，但也无法响应返回按键了
         *
         * @param isRequestFocus 默认为true
         * @return
         */
        public Builder isRequestFocus(boolean isRequestFocus) {
            this.popupInfo.isRequestFocus = isRequestFocus;
            return this;
        }

        public Builder immediateAdd() {
            this.popupInfo.immediateAdd = true;
            return this;
        }

        public Builder shadowBgColor(int shadowBgColor) {
            this.popupInfo.shadowBgColor = shadowBgColor;
            return this;
        }

        public Builder statusBarBgColor(int statusBarBgColor) {
            this.popupInfo.statusBarBgColor = statusBarBgColor;
            return this;
        }

        /**
         * 是否让弹窗内的输入框自动获取焦点，默认是true。
         *
         * @param autoFocusEditText
         * @return
         */
        public Builder autoFocusEditText(boolean autoFocusEditText) {
            this.popupInfo.autoFocusEditText = autoFocusEditText;
            return this;
        }

        public Builder launchModel(LaunchModel launchModel) {
            this.popupInfo.launchModel = launchModel;
            return this;
        }

        public Builder isViewMode(boolean viewMode) {
            this.popupInfo.isViewMode = viewMode;
            return this;
        }

        public Builder setId(int id) {
            this.popupInfo.id = id;
            return this;
        }
        /****************************************** 便捷方法 ****************************************/
        /**
         * 显示确认和取消对话框
         *
         * @param title          对话框标题，传空串会隐藏标题
         * @param content        对话框内容
         * @param cancelBtnText  取消按钮的文字内容
         * @param confirmBtnText 确认按钮的文字内容
         * @param popupListener  点击取消的监听器
         * @param isHideCancel   是否隐藏取消按钮
         * @return
         */
        public ConfirmPopupView asConfirm(String title, String content, String hint, String cancelBtnText, String confirmBtnText, IPopupListener popupListener, boolean isHideCancel) {
            popupType(PopupType.Center);
            ConfirmPopupView popupView = new ConfirmPopupView(this.context);
            popupView.setTitleContent(title, content, hint);
            popupView.setCancelText(cancelBtnText);
            popupView.setConfirmText(confirmBtnText);
            popupView.setListener(popupListener);
            if (isHideCancel) {
                popupView.hideCancelBtn();
            }
            popupView.setPopupInfo(this.popupInfo);
            return popupView;
        }

        public ConfirmPopupView asConfirm(String title, String content, String cancelBtnText, String confirmBtnText, IPopupListener popupListener, boolean isHideCancel) {

            return asConfirm(title, content, null, cancelBtnText, confirmBtnText, popupListener, isHideCancel);
        }

        public ConfirmPopupView asConfirm(String title, String content) {

            return asConfirm(title, content, null, null, null, null, false);
        }

        //
        public ConfirmPopupView asConfirm(String title, String content, IPopupListener popupListener) {
            return asConfirm(title, content, null, null, popupListener, false);
        }

        public LoadingPopupView asLoading(String title) {
            popupType(PopupType.Center);
            LoadingPopupView popupView = new LoadingPopupView(this.context)
                    .setTitle(title);
            popupView.popupInfo = this.popupInfo;
            return popupView;
        }

        public IPopup asCustom(IPopup popupView) {
            if (popupView instanceof CenterPopupView) {
                popupType(PopupType.Center);
            } else if (popupView instanceof BottomPopupView) {
                popupType(PopupType.Bottom);
            }
            popupView.setPopupInfo(this.popupInfo);
            return popupView;
        }
    }
}
