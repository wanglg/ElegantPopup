package com.quzhibo.liveui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.noober.background.drawable.DrawableCreator;
import com.quzhibo.lib.bubble.config.BubblePathBuilder;
import com.quzhibo.lib.bubble.config.BubbleType;
import com.quzhibo.lib.bubble.config.ClipPathManager;
import com.quzhibo.lib.bubble.config.Constants;
import com.quzhibo.lib.bubble.config.PositionType;
import com.quzhibo.lib.bubble.widget.BubbleRelativeLayout;

/**
 * demo主界面
 *
 * @author yeluodev1226
 */
public class MainActivity extends AppCompatActivity {

    private RadioGroup mBubbleTypeRadioGroup;
    private RadioGroup mArrowTypeRadioGroup;
    private AppCompatSeekBar mArrowOffsetSeekBar;
    private AppCompatSeekBar mArrowWidthSeekBar;
    private AppCompatSeekBar mArrowHeightSeekBar;
    private AppCompatCheckBox mEnableCornerCheckBox;
    private AppCompatCheckBox mUseSameCornerCheckBox;
    private RadioGroup mCornerRadiusRadioGroup;
    private RadioButton mCornerRadiusLeftTopCheckBox;
    private RadioButton mCornerRadiusRightTopCheckBox;
    private RadioButton mCornerRadiusRightBottomCheckBox;
    private RadioButton mCornerRadiusLeftBottomCheckBox;
    private AppCompatSeekBar mCornerRadiusSeekBar;
    private RadioGroup mBackgroundRadioGroup;
    private EditText mImgUrlEditText;
    private Button mSubmitButton;
    private BubbleRelativeLayout mBubbleLayout;

    @BubbleType
    private int mBubbleType = Constants.TYPE_BUBBLE_LEFT;
    @PositionType
    private int mArrowType = Constants.TYPE_POSITION_LEFT;

    private int mArrowOffset = 0;
    private int mArrowOffsetMin = 0;
    private int mArrowOffsetMax = 0;

    private int mArrowWidth = 40;
    private int mArrowWidthMax = 0;

    private int mArrowHeight = 20;
    private int mArrowHeightMax = 0;

    private boolean mEnableCorner = true;
    private boolean mUseSameCorner = true;
    private int mSelectedCorner = 0;
    private int mCornerRadius = 40;
    private int mLeftTopCornerRadius = 40;
    private int mRightTopCornerRadius = 40;
    private int mRightBottomCornerRadius = 40;
    private int mLeftBottomCornerRadius = 40;

    private String mImgUrl = "https://uquliveimg.qutoutiao.net/expression_bin.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBubbleLayout = findViewById(R.id.bubbleLayout);

        initBubbleType();
        initArrowType();
        initArrowOffset();
        initArrowWidth();
        initArrowHeight();
        initCornerRadius();
        initBackground();

        refreshBubble();
    }

    /**
     * 计算箭头相关属性值
     *
     * @param width  控件宽
     * @param height 控件高
     */
    @SuppressWarnings({"ConstantConditions", "SuspiciousNameCombination"})
    private void calculateArrowAttrs(int width, int height) {
        int temp;
        if (mBubbleType == Constants.TYPE_BUBBLE_LEFT || mBubbleType == Constants.TYPE_BUBBLE_RIGHT) {
            temp = width;
            width = height;
            height = temp;
        }
        do {
            if (!mEnableCorner) {
                mArrowOffsetMin = 0;
                mArrowOffsetMax = width - mArrowWidth;
                mArrowWidthMax = width;
                mArrowHeightMax = height;
                break;
            }
            if (mUseSameCorner) {
                mArrowOffsetMin = mCornerRadius;
                mArrowOffsetMax = width - mCornerRadius * 2 - mArrowWidth;
                mArrowWidthMax = width - mCornerRadius * 2;
                mArrowHeightMax = height - mCornerRadius * 2;
                break;
            }
            switch (mBubbleType) {
                case Constants.TYPE_BUBBLE_LEFT:
                    mArrowOffsetMin = mArrowType == Constants.TYPE_POSITION_LEFT ? mLeftBottomCornerRadius : mLeftTopCornerRadius;
                    mArrowOffsetMax = width - mLeftTopCornerRadius - mLeftBottomCornerRadius - mArrowWidth;
                    mArrowWidthMax = width - mLeftTopCornerRadius - mLeftBottomCornerRadius;
                    mArrowHeightMax = height - Math.max(mRightTopCornerRadius, mRightBottomCornerRadius) - Math.max(mLeftTopCornerRadius, mLeftBottomCornerRadius);
                    break;
                case Constants.TYPE_BUBBLE_RIGHT:
                    mArrowOffsetMin = mArrowType == Constants.TYPE_POSITION_LEFT ? mRightTopCornerRadius : mRightBottomCornerRadius;
                    mArrowOffsetMax = width - mRightTopCornerRadius - mRightBottomCornerRadius - mArrowWidth;
                    mArrowWidthMax = width - mRightTopCornerRadius - mRightBottomCornerRadius;
                    mArrowHeightMax = height - Math.max(mRightTopCornerRadius, mRightBottomCornerRadius) - Math.max(mLeftTopCornerRadius, mLeftBottomCornerRadius);
                    break;
                case Constants.TYPE_BUBBLE_TOP:
                    mArrowOffsetMin = mArrowType == Constants.TYPE_POSITION_LEFT ? mLeftTopCornerRadius : mRightTopCornerRadius;
                    mArrowOffsetMax = width - mLeftTopCornerRadius - mRightTopCornerRadius - mArrowWidth;
                    mArrowWidthMax = width - mLeftTopCornerRadius - mRightTopCornerRadius;
                    mArrowHeightMax = height - Math.max(mLeftTopCornerRadius, mRightTopCornerRadius) - Math.max(mLeftBottomCornerRadius, mRightBottomCornerRadius);
                    break;
                case Constants.TYPE_BUBBLE_BOTTOM:
                    mArrowOffsetMin = mArrowType == Constants.TYPE_POSITION_LEFT ? mRightBottomCornerRadius : mLeftBottomCornerRadius;
                    mArrowOffsetMax = width - mLeftBottomCornerRadius - mRightBottomCornerRadius - mArrowWidth;
                    mArrowWidthMax = width - mLeftBottomCornerRadius - mRightBottomCornerRadius;
                    mArrowHeightMax = height - Math.max(mLeftTopCornerRadius, mRightTopCornerRadius) - Math.max(mLeftBottomCornerRadius, mRightBottomCornerRadius);
                    break;
                default:
                    break;
            }
        } while (false);

        mArrowOffsetSeekBar.setMax(mArrowOffsetMax);
        mArrowOffset = Math.max(Math.min(mArrowOffset, mArrowOffsetMax), 0);
        mArrowOffsetSeekBar.setProgress(mArrowOffset);

        mArrowWidthSeekBar.setMax(mArrowWidthMax);
        mArrowWidth = Math.min(mArrowWidth, mArrowWidthMax);
        mArrowWidthSeekBar.setProgress(mArrowWidth);

        mArrowHeightSeekBar.setMax(mArrowHeightMax);
        mArrowHeight = Math.min(mArrowHeight, mArrowHeightMax);
        mArrowHeightSeekBar.setProgress(mArrowHeight);

        height -= mArrowHeight;
        mCornerRadiusSeekBar.setMax(Math.min(width, height) / 2);
    }

    /**
     * 刷新控件
     */
    private void refreshBubble() {
        mBubbleLayout.setClipPathCreator(new ClipPathManager.ClipPathCreator() {
            @Override
            public Path createClipPath(int width, int height) {
                calculateArrowAttrs(width, height);
                BubblePathBuilder builder = BubblePathBuilder.builder()
                        .setBubbleType(mBubbleType)
                        .setArrowType(mArrowType)
                        .setArrowWidth(mArrowWidth)
                        .setArrowHeight(mArrowHeight)
                        .setArrowOffset(mArrowOffset + mArrowOffsetMin)
                        .setBubbleWidth(width)
                        .setBubbleHeight(height);
                if (!mEnableCorner) {
                    builder.setCornerRadius(0);
                } else if (mUseSameCorner) {
                    builder.setCornerRadius(mCornerRadius);
                    mCornerRadiusSeekBar.setProgress(mCornerRadius);
                } else {
                    builder.setCornerRadius(mLeftTopCornerRadius, mRightTopCornerRadius,
                            mRightBottomCornerRadius, mLeftBottomCornerRadius);
                    switch (mSelectedCorner) {
                        case 0:
                            mCornerRadiusSeekBar.setProgress(mLeftTopCornerRadius);
                            break;
                        case 1:
                            mCornerRadiusSeekBar.setProgress(mRightTopCornerRadius);
                            break;
                        case 2:
                            mCornerRadiusSeekBar.setProgress(mRightBottomCornerRadius);
                            break;
                        case 3:
                            mCornerRadiusSeekBar.setProgress(mLeftBottomCornerRadius);
                            break;
                        default:
                            break;
                    }
                }
                return builder.create();
            }

            @Override
            public boolean requiresBitmap() {
                return false;
            }
        });
    }

    /**
     * 气泡箭头方向
     */
    private void initBubbleType() {
        mBubbleTypeRadioGroup = findViewById(R.id.bubbleTypeRadioGroup);
        mBubbleTypeRadioGroup.check(R.id.bubbleTypeLeftRadioButton);
        mBubbleTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.bubbleTypeLeftRadioButton:
                        mBubbleType = Constants.TYPE_BUBBLE_LEFT;
                        Toast.makeText(MainActivity.this, "左", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bubbleTypeTopRadioButton:
                        mBubbleType = Constants.TYPE_BUBBLE_TOP;
                        Toast.makeText(MainActivity.this, "上", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bubbleTypeRightRadioButton:
                        mBubbleType = Constants.TYPE_BUBBLE_RIGHT;
                        Toast.makeText(MainActivity.this, "右", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bubbleTypeBottomRadioButton:
                        mBubbleType = Constants.TYPE_BUBBLE_BOTTOM;
                        Toast.makeText(MainActivity.this, "下", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                refreshBubble();
            }
        });

    }

    /**
     * 气泡箭头对齐
     */
    private void initArrowType() {
        mArrowTypeRadioGroup = findViewById(R.id.arrowTypeRadioGroup);
        mArrowTypeRadioGroup.check(R.id.arrowTypeLeftRadioButton);
        mArrowTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.arrowTypeLeftRadioButton:
                        mArrowType = Constants.TYPE_POSITION_LEFT;
                        Toast.makeText(MainActivity.this, "左对齐", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.arrowTypeCenterRadioButton:
                        mArrowType = Constants.TYPE_POSITION_CENTER;
                        Toast.makeText(MainActivity.this, "居中", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.arrowTypeRightRadioButton:
                        mArrowType = Constants.TYPE_POSITION_RIGHT;
                        Toast.makeText(MainActivity.this, "右对齐", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                refreshBubble();
            }
        });
    }

    /**
     * 气泡箭头偏移
     */
    private void initArrowOffset() {
        mArrowOffsetSeekBar = findViewById(R.id.arrowOffsetSeekBar);
        mArrowOffsetSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mArrowOffset = progress;
                refreshBubble();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * 气泡箭头宽度
     */
    private void initArrowWidth() {
        mArrowWidthSeekBar = findViewById(R.id.arrowWidthSeekBar);
        mArrowWidthSeekBar.setProgress(mArrowWidth);
        mArrowWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mArrowWidth = progress;
                refreshBubble();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * 气泡箭头高度
     */
    private void initArrowHeight() {
        mArrowHeightSeekBar = findViewById(R.id.arrowHeightSeekBar);
        mArrowHeightSeekBar.setProgress(mArrowHeight);
        mArrowHeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mArrowHeight = progress;
                refreshBubble();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * 圆角设置
     */
    private void initCornerRadius() {
        mEnableCornerCheckBox = findViewById(R.id.enableRoundCornerCheckBox);
        mUseSameCornerCheckBox = findViewById(R.id.useSameCornerCheckBox);
        mCornerRadiusRadioGroup = findViewById(R.id.cornerRadiusRadioGroup);
        mCornerRadiusLeftTopCheckBox = findViewById(R.id.cornerRadiusLeftTopRadioButton);
        mCornerRadiusRightTopCheckBox = findViewById(R.id.cornerRadiusRightTopRadioButton);
        mCornerRadiusRightBottomCheckBox = findViewById(R.id.cornerRadiusRightBottomRadioButton);
        mCornerRadiusLeftBottomCheckBox = findViewById(R.id.cornerRadiusLeftBottomRadioButton);
        mCornerRadiusSeekBar = findViewById(R.id.cornerRadiusSeekBar);

        mEnableCornerCheckBox.setChecked(mEnableCorner);
        mUseSameCornerCheckBox.setChecked(mUseSameCorner);
        mCornerRadiusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.cornerRadiusLeftTopRadioButton:
                        mSelectedCorner = 0;
                        mCornerRadiusSeekBar.setProgress(mLeftTopCornerRadius);
                        break;
                    case R.id.cornerRadiusRightTopRadioButton:
                        mSelectedCorner = 1;
                        mCornerRadiusSeekBar.setProgress(mRightTopCornerRadius);
                        break;
                    case R.id.cornerRadiusRightBottomRadioButton:
                        mSelectedCorner = 2;
                        mCornerRadiusSeekBar.setProgress(mRightBottomCornerRadius);
                        break;
                    case R.id.cornerRadiusLeftBottomRadioButton:
                        mSelectedCorner = 3;
                        mCornerRadiusSeekBar.setProgress(mLeftBottomCornerRadius);
                        break;
                    default:
                        break;
                }
                updateCorner();
                refreshBubble();
            }
        });


        mEnableCornerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mEnableCorner = b;
                updateCorner();
                refreshBubble();
            }
        });
        mUseSameCornerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mUseSameCorner = b;
                updateCorner();
                refreshBubble();
            }
        });
        mCornerRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (mEnableCorner) {
                    if (mUseSameCorner) {
                        mCornerRadius = progress;
                    } else {
                        switch (mSelectedCorner) {
                            case 0:
                                mLeftTopCornerRadius = progress;
                                break;
                            case 1:
                                mRightTopCornerRadius = progress;
                                break;
                            case 2:
                                mRightBottomCornerRadius = progress;
                                break;
                            case 3:
                                mLeftBottomCornerRadius = progress;
                                break;
                            default:
                                break;
                        }
                    }
                }
                refreshBubble();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        updateCorner();
    }

    private void updateCorner() {
        if (mEnableCorner) {
            mUseSameCornerCheckBox.setEnabled(true);
            mCornerRadiusSeekBar.setEnabled(true);
            if (mUseSameCorner) {
                mCornerRadiusLeftTopCheckBox.setEnabled(false);
                mCornerRadiusRightTopCheckBox.setEnabled(false);
                mCornerRadiusRightBottomCheckBox.setEnabled(false);
                mCornerRadiusLeftBottomCheckBox.setEnabled(false);
                mCornerRadiusSeekBar.setProgress(mCornerRadius);
            } else {
                mCornerRadiusRadioGroup.setEnabled(true);
                mCornerRadiusLeftTopCheckBox.setEnabled(true);
                mCornerRadiusRightTopCheckBox.setEnabled(true);
                mCornerRadiusRightBottomCheckBox.setEnabled(true);
                mCornerRadiusLeftBottomCheckBox.setEnabled(true);
                switch (mSelectedCorner) {
                    case 0:
                        mCornerRadiusRadioGroup.check(R.id.cornerRadiusLeftTopRadioButton);
                        mCornerRadiusSeekBar.setProgress(mLeftTopCornerRadius);
                        break;
                    case 1:
                        mCornerRadiusRadioGroup.check(R.id.cornerRadiusRightTopRadioButton);
                        mCornerRadiusSeekBar.setProgress(mRightTopCornerRadius);
                        break;
                    case 2:
                        mCornerRadiusRadioGroup.check(R.id.cornerRadiusRightBottomRadioButton);
                        mCornerRadiusSeekBar.setProgress(mRightBottomCornerRadius);
                        break;
                    case 3:
                        mCornerRadiusRadioGroup.check(R.id.cornerRadiusLeftBottomRadioButton);
                        mCornerRadiusSeekBar.setProgress(mLeftBottomCornerRadius);
                        break;
                    default:
                        break;
                }
            }
        } else {
            mUseSameCornerCheckBox.setEnabled(false);
            mCornerRadiusSeekBar.setEnabled(false);
            mCornerRadiusLeftTopCheckBox.setEnabled(false);
            mCornerRadiusRightTopCheckBox.setEnabled(false);
            mCornerRadiusRightBottomCheckBox.setEnabled(false);
            mCornerRadiusLeftBottomCheckBox.setEnabled(false);
        }
    }

    private void initBackground() {
        mBackgroundRadioGroup = findViewById(R.id.backgroundRadioGroup);
        mImgUrlEditText = findViewById(R.id.imgUrlEditText);
        mSubmitButton = findViewById(R.id.submitButton);
        mBackgroundRadioGroup.check(R.id.solidRadioButton);
        mImgUrlEditText.setEnabled(false);
        mSubmitButton.setEnabled(false);
        mBubbleLayout.setBackgroundColor(0xff_005C97);
        mBackgroundRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.solidRadioButton:
                        mImgUrlEditText.setEnabled(false);
                        mSubmitButton.setEnabled(false);
                        mBubbleLayout.setBackgroundColor(0xff_005C97);
                        break;
                    case R.id.gradientRadioButton:
                        mImgUrlEditText.setEnabled(false);
                        mSubmitButton.setEnabled(false);
                        Drawable gradientDrawable = new DrawableCreator.Builder()
                                .setGradientColor(0xff_c471ed, 0xff_f64f59, 0xff_12c2e9)
                                .setGradientAngle(0)
                                .build();
                        mBubbleLayout.setBackground(gradientDrawable);
                        break;
                    case R.id.localRadioButton:
                        mImgUrlEditText.setEnabled(false);
                        mSubmitButton.setEnabled(false);
                        mBubbleLayout.setBackgroundResource(R.mipmap.jaychou);
                        break;
                    case R.id.networkRadioButton:
                        mImgUrlEditText.setEnabled(true);
                        mSubmitButton.setEnabled(true);
                        Glide.with(MainActivity.this)
                                .asDrawable()
                                .load("https://uquliveimg.qutoutiao.net/expression_bin.png")
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        mBubbleLayout.setBackground(resource);
                                    }
                                });
                        break;
                    default:
                        break;
                }
            }
        });

        mImgUrlEditText.setText(mImgUrl);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mImgUrlEditText.getText())) {
                    mImgUrl = "https://uquliveimg.qutoutiao.net/expression_bin.png";
                } else {
                    mImgUrl = mImgUrlEditText.getText().toString();
                }
                Glide.with(MainActivity.this)
                        .asDrawable()
                        .load(mImgUrl)
                        .centerCrop()
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                mBubbleLayout.setBackground(resource);
                            }
                        });
            }
        });
    }
}
