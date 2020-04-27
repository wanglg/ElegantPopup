/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leo.uilib.popup.impl.dialog;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.leo.uilib.popup.R;
import com.leo.uilib.popup.util.PopupUtils;


/**
 * Base class for {@link android.app.Dialog}s styled as a bottom sheet.
 */
public abstract class BottomSheetPopupDialog extends BasePopupDialog {

    private static final String TAG = "BottomSheet";
    private LinearLayout mRootView;
    private OnBottomSheetShowListener mOnBottomSheetShowListener;
    private ElegantBottomSheetBehavior<LinearLayout> mBehavior;
    private boolean mAnimateToCancel = false;
    private boolean mAnimateToDismiss = false;


    public BottomSheetPopupDialog(Context context) {
        this(context, R.style.BottomDialogStyle);
    }

    public BottomSheetPopupDialog(Context context, int style) {
        super(context, style);
    }

    @Override
    protected void initView(Context context) {
        ViewGroup container = (ViewGroup) View.inflate(context, R.layout._xpopup_bottom_sheet_dialog, null);
        mRootView = container.findViewById(R.id.bottom_sheet);
        mBehavior = new ElegantBottomSheetBehavior<>();
        mBehavior.setHideable(cancelable);
        mBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (mAnimateToCancel) {
                        // cancel() invoked
                        cancel();
                    } else if (mAnimateToDismiss) {
                        // dismiss() invoked but it it not triggered by cancel()
                        dismiss();
                    } else {
                        // drag to cancel
                        cancel();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        mBehavior.setPeekHeight(0);
        mBehavior.setAllowDrag(true);
        mBehavior.setSkipCollapsed(true);
        mRootView.setOnClickListener(v -> {
            dismiss();
        });
        CoordinatorLayout.LayoutParams rootViewLp = (CoordinatorLayout.LayoutParams) mRootView.getLayoutParams();
        rootViewLp.setBehavior(mBehavior);
        View addView = LayoutInflater.from(context).inflate(getLayoutId(), null, false);
        addView.setClickable(true);
        addContentView(addView);
        super.setContentView(container, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onSetCancelable(boolean cancelable) {
        super.onSetCancelable(cancelable);
        mBehavior.setHideable(cancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        ViewCompat.requestApplyInsets(mRootView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        mBehavior.setAllowDrag(popupInfo.enableDrag);
        View view = mRootView.getChildAt(0);
        if (view != null && getMaxHeight() > 0) {
            LinearLayout.LayoutParams vl = (LinearLayout.LayoutParams) view.getLayoutParams();
            vl.height = getMaxHeight();
        }
    }

    @Override
    public void cancel() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            mAnimateToCancel = false;
            super.cancel();
        } else {
            mAnimateToCancel = true;
            mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void dismiss() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            mAnimateToDismiss = false;
            super.dismiss();
        } else {
            mAnimateToDismiss = true;
            mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void setOnBottomSheetShowListener(OnBottomSheetShowListener onBottomSheetShowListener) {
        mOnBottomSheetShowListener = onBottomSheetShowListener;
    }


    public LinearLayout getRootView() {
        return mRootView;
    }

    public ElegantBottomSheetBehavior<LinearLayout> getBehavior() {
        return mBehavior;
    }

    @Override
    public void show() {
        super.show();
        if (mOnBottomSheetShowListener != null) {
            mOnBottomSheetShowListener.onShow();
        }
        if (mBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            mRootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            },300);
        }
        mAnimateToCancel = false;
        mAnimateToDismiss = false;
    }

    public interface OnBottomSheetShowListener {
        void onShow();
    }

    @Override
    public void setContentView(View view) {
        throw new IllegalStateException(
                "Use addContentView(View, ConstraintLayout.LayoutParams) for replacement");
    }

    @Override
    public void setContentView(int layoutResId) {
        throw new IllegalStateException(
                "Use addContentView(int) for replacement");
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        throw new IllegalStateException(
                "Use addContentView(View, LinearLayout.LayoutParams) for replacement");
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        throw new IllegalStateException(
                "Use addContentView(View, LinearLayout.LayoutParams) for replacement");
    }

    public void addContentView(View view, LinearLayout.LayoutParams layoutParams) {
        mRootView.addView(view, layoutParams);
    }

    public void addContentView(View view) {
        mRootView.addView(view, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void addContentView(int layoutResId) {
        LayoutInflater.from(mRootView.getContext()).inflate(layoutResId, mRootView, true);
    }

    @Override
    protected int getMaxWidth() {
        return PopupUtils.getWindowWidth(getContext());
    }


}
