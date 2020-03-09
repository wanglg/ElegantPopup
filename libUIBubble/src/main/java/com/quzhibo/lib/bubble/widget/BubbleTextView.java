package com.quzhibo.lib.bubble.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

import com.quzhibo.lib.bubble.R;
import com.quzhibo.lib.bubble.config.BubblePathBuilder;
import com.quzhibo.lib.bubble.config.ClipManager;
import com.quzhibo.lib.bubble.config.ClipPathManager;
import com.quzhibo.lib.bubble.config.Constants;

/**
 * 气泡文本控件
 *
 * @author yeluodev1226
 * @date 2020-03-06 19:52
 */
public class BubbleTextView extends AppCompatTextView {
    private final Paint clipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path clipPath = new Path();
    private final Path rectPath = new Path();

    private Bitmap clipBitmap;
    private Drawable drawable = null;
    private boolean requireShapeUpdate = true;
    private ClipManager clipManager = new ClipPathManager();

    private int mBubbleType;
    private int mArrowType;
    private int mCornerRadius;
    private int mLeftTopCornerRadius;
    private int mRightTopCornerRadius;
    private int mRightBottomCornerRadius;
    private int mLeftBottomCornerRadius;
    private int mArrowWidth;
    private int mArrowHeight;
    private int mArrowOffset;
    private boolean mUseCornerRadius;
    private boolean mFixPadding;

    public BubbleTextView(Context context) {
        super(context);
        init(context, null);
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        initAttrs(context, attrs);
        initPaint();

        setClipPathCreator(new ClipPathManager.ClipPathCreator() {
            @Override
            public Path createClipPath(int width, int height) {
                return BubblePathBuilder.builder()
                        .setBubbleType(mBubbleType)
                        .setArrowType(mArrowType)
                        .setArrowWidth(mArrowWidth)
                        .setArrowHeight(mArrowHeight)
                        .setArrowOffset(mArrowOffset)
                        .setBubbleWidth(width)
                        .setBubbleHeight(height)
                        .setCornerRadius(mLeftTopCornerRadius, mRightTopCornerRadius, mRightBottomCornerRadius, mLeftBottomCornerRadius)
                        .create();
            }

            @Override
            public boolean requiresBitmap() {
                return false;
            }
        });
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubbleFantasy);
        mBubbleType = typedArray.getInt(R.styleable.BubbleFantasy_bf_bubbleType, Constants.TYPE_BUBBLE_BOTTOM);
        mArrowType = typedArray.getInt(R.styleable.BubbleFantasy_bf_arrowType, Constants.TYPE_POSITION_CENTER);
        mArrowWidth = typedArray.getDimensionPixelSize(R.styleable.BubbleFantasy_bf_arrowWidth, 40);
        mArrowHeight = typedArray.getDimensionPixelSize(R.styleable.BubbleFantasy_bf_arrowHeight, 20);
        mArrowOffset = typedArray.getDimensionPixelSize(R.styleable.BubbleFantasy_bf_arrowOffset, 100);
        mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.BubbleFantasy_bf_cornerRadius, 40);
        mLeftTopCornerRadius = typedArray.getDimensionPixelSize(R.styleable.BubbleFantasy_bf_ltCornerRadius, 0);
        mRightTopCornerRadius = typedArray.getDimensionPixelSize(R.styleable.BubbleFantasy_bf_rtCornerRadius, 0);
        mRightBottomCornerRadius = typedArray.getDimensionPixelSize(R.styleable.BubbleFantasy_bf_rbCornerRadius, 0);
        mLeftBottomCornerRadius = typedArray.getDimensionPixelSize(R.styleable.BubbleFantasy_bf_lbCornerRadius, 0);
        mUseCornerRadius = typedArray.getBoolean(R.styleable.BubbleFantasy_bf_useCornerRadius, true);
        mFixPadding = typedArray.getBoolean(R.styleable.BubbleFantasy_bf_fixPadding, true);
        typedArray.recycle();

        if (mUseCornerRadius
                && mLeftTopCornerRadius == 0
                && mRightTopCornerRadius == 0
                && mRightBottomCornerRadius == 0
                && mLeftBottomCornerRadius == 0) {
            mLeftTopCornerRadius = mCornerRadius;
            mRightTopCornerRadius = mCornerRadius;
            mRightBottomCornerRadius = mCornerRadius;
            mLeftBottomCornerRadius = mCornerRadius;
        } else {
            mLeftTopCornerRadius = 0;
            mRightTopCornerRadius = 0;
            mRightBottomCornerRadius = 0;
            mLeftBottomCornerRadius = 0;
        }

        if (mFixPadding) {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }
    }

    private void initPaint() {
        clipPaint.setAntiAlias(true);
        clipPaint.setColor(Color.BLUE);
        clipPaint.setStyle(Paint.Style.FILL);
        clipPaint.setStrokeWidth(1);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            setLayerType(LAYER_TYPE_SOFTWARE, clipPaint);
        } else {
            clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        //TODO RelativeLayout中子View居中不会排除padding
        if (mFixPadding) {
            switch (mBubbleType) {
                case Constants.TYPE_BUBBLE_TOP:
                    top = mArrowHeight + top;
                    break;
                case Constants.TYPE_BUBBLE_RIGHT:
                    right = mArrowHeight + right;
                    break;
                case Constants.TYPE_BUBBLE_BOTTOM:
                    bottom = mArrowHeight + bottom;
                    break;
                case Constants.TYPE_BUBBLE_LEFT:
                    left = mArrowHeight + left;
                    break;
                default:
                    break;
            }
        }
        super.setPadding(left, top, right, bottom);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            requiresShapeUpdate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (requireShapeUpdate) {
            calculateLayout(canvas.getWidth(), canvas.getHeight());
            requireShapeUpdate = false;
        }
        if (requiresBitmap()) {
            clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(clipBitmap, 0, 0, clipPaint);
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                canvas.drawPath(clipPath, clipPaint);
            } else {
                canvas.drawPath(rectPath, clipPaint);
            }
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }
    }

    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                if (clipManager != null) {
                    final Path shadowConvexPath = clipManager.getShadowConvexPath();
                    if (shadowConvexPath != null) {
                        try {
                            outline.setConvexPath(shadowConvexPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        };
    }

    private void calculateLayout(int width, int height) {
        rectPath.reset();
        rectPath.addRect(0f, 0f, 1f * getWidth(), 1f * getHeight(), Path.Direction.CW);

        if (clipManager != null) {
            if (width > 0 && height > 0) {
                clipManager.setupClipLayout(width, height);
                clipPath.reset();
                clipPath.set(clipManager.createMask(width, height));

                if (requiresBitmap()) {
                    if (clipBitmap != null) {
                        clipBitmap.recycle();
                    }
                    clipBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    final Canvas canvas = new Canvas(clipBitmap);

                    if (drawable != null) {
                        drawable.setBounds(0, 0, width, height);
                        drawable.draw(canvas);
                    } else {
                        canvas.drawPath(clipPath, clipManager.getPaint());
                    }
                }

                //invert the path for android P
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                    rectPath.op(clipPath, Path.Op.DIFFERENCE);
                }

                //this needs to be fixed for 25.4.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ViewCompat.getElevation(this) > 0f) {
                    try {
                        setOutlineProvider(getOutlineProvider());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        postInvalidate();
    }

    public void setClipPathCreator(ClipPathManager.ClipPathCreator createClipPath) {
        ((ClipPathManager) clipManager).setClipPathCreator(createClipPath);
        requiresShapeUpdate();
    }

    public void requiresShapeUpdate() {
        requireShapeUpdate = true;
        postInvalidate();
    }

    private boolean requiresBitmap() {
        return isInEditMode() || (clipManager != null && clipManager.requiresBitmap()) || drawable != null;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        requiresShapeUpdate();
    }

    public void setDrawable(int resId) {
        setDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }
}
