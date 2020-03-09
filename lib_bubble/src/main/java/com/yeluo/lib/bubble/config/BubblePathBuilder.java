package com.yeluo.lib.bubble.config;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * 气泡外部路径Builder
 *
 * @author yeluodev1226
 * @date 2020-03-07 11:40
 */
public class BubblePathBuilder {
    /**
     * 箭头顶点在所在边的位置
     */
    private float mCenterArrow = 0;

    /**
     * 左上角圆角半径
     */
    private float mLeftTopCornerRadius = 40;
    /**
     * 右上角圆角半径
     */
    private float mRightTopCornerRadius = 40;
    /**
     * 右下角圆角半径
     */
    private float mRightBottomCornerRadius = 40;
    /**
     * 左下角圆角半径
     */
    private float mLeftBottomCornerRadius = 40;

    /**
     * 突出部分的箭头的高, 也就是顶点到缺失的那一部分的线条的距离
     */
    private float mArrowHeight = 20;
    /**
     * 突出部分的箭头的宽, 也就是缺失的那一部分的线条的宽度
     */
    private float mArrowWidth = 40;
    /**
     * 针对mArrowType为{@link Constants#TYPE_POSITION_LEFT}和{@link Constants#TYPE_POSITION_RIGHT}的情况
     * 在{@link Constants#TYPE_POSITION_LEFT}情况下, mArrowOffset为箭头左侧到左侧边界的距离
     * 在{@link Constants#TYPE_POSITION_RIGHT}情况下, mArrowOffset为箭头右侧到右侧边界的距离
     */
    private float mArrowOffset = 100;
    /**
     * 想象一下, 你站在中心点, 正对着箭头所在的边的方向, 这个时候箭头在你的那个位置?
     * 左手边{@link Constants#TYPE_POSITION_LEFT},
     * 正前面{@link Constants#TYPE_POSITION_CENTER},
     * 右手边{@link Constants#TYPE_POSITION_RIGHT}
     */
    private int mArrowType = Constants.TYPE_POSITION_CENTER;
    /**
     * 箭头所在边的位置
     * 左边{@link Constants#TYPE_BUBBLE_LEFT},
     * 顶部{@link Constants#TYPE_BUBBLE_TOP},
     * 右边{@link Constants#TYPE_BUBBLE_RIGHT}
     * 底部{@link Constants#TYPE_BUBBLE_BOTTOM}
     */
    private int mBubbleType = Constants.TYPE_BUBBLE_BOTTOM;

    /**
     * 当前控件的宽度
     */
    private int mBubbleWidth;
    /**
     * 当前控件的高度
     */
    private int mBubbleHeight;

    /**
     * 气泡外部轮廓路径
     */
    private Path mClipPath = new Path();

    private BubblePathBuilder() {
    }

    public static BubblePathBuilder builder() {
        return new BubblePathBuilder();
    }

    /**
     * 设置统一的圆角半径
     *
     * @param cornerRadius 圆角半径
     * @return builder
     */
    public BubblePathBuilder setCornerRadius(float cornerRadius) {
        this.mLeftTopCornerRadius = cornerRadius;
        this.mRightTopCornerRadius = cornerRadius;
        this.mRightBottomCornerRadius = cornerRadius;
        this.mLeftBottomCornerRadius = cornerRadius;
        return this;
    }

    /**
     * 分别设置四个角的圆角半径
     *
     * @param lt 左上角圆角半径
     * @param rt 右上角圆角半径
     * @param rb 右下角圆角半径
     * @param lb 左下角圆角半径
     * @return builder
     */
    public BubblePathBuilder setCornerRadius(float lt, float rt, float rb, float lb) {
        this.mLeftTopCornerRadius = lt;
        this.mRightTopCornerRadius = rt;
        this.mRightBottomCornerRadius = rb;
        this.mLeftBottomCornerRadius = lb;
        return this;
    }

    /**
     * 箭头离所在边的直线距离
     *
     * @param arrowHeight 箭头高度
     * @return builder
     */
    public BubblePathBuilder setArrowHeight(float arrowHeight) {
        this.mArrowHeight = arrowHeight;
        return this;
    }

    /**
     * 箭头所在边缺失的线段长度
     *
     * @param arrowWidth 箭头宽度
     * @return builder
     */
    public BubblePathBuilder setArrowWidth(float arrowWidth) {
        this.mArrowWidth = arrowWidth;
        return this;
    }

    /**
     * 箭头离所在边顶点的偏移
     *
     * @param arrowOffset 偏移
     * @return builder
     */
    public BubblePathBuilder setArrowOffset(float arrowOffset) {
        this.mArrowOffset = arrowOffset;
        return this;
    }

    /**
     * 箭头摆放位置
     *
     * @param type 左手边{@link Constants#TYPE_POSITION_LEFT}
     *             正前面{@link Constants#TYPE_POSITION_CENTER}
     *             右手边{@link Constants#TYPE_POSITION_RIGHT}
     * @return builder
     */
    public BubblePathBuilder setArrowType(@PositionType int type) {
        this.mArrowType = type;
        return this;
    }

    /**
     * 气泡箭头的所在边位置
     *
     * @param type 左边{@link Constants#TYPE_BUBBLE_LEFT}
     *             顶部{@link Constants#TYPE_BUBBLE_TOP}
     *             右边{@link Constants#TYPE_BUBBLE_RIGHT}
     *             底部{@link Constants#TYPE_BUBBLE_BOTTOM}
     * @return builder
     */
    public BubblePathBuilder setBubbleType(@BubbleType int type) {
        this.mBubbleType = type;
        return this;
    }

    /**
     * 气泡宽度
     *
     * @param width 气泡宽度
     * @return builder
     */
    public BubblePathBuilder setBubbleWidth(int width) {
        this.mBubbleWidth = width;
        return this;
    }

    /**
     * 气泡高度
     *
     * @param height 气泡高度
     * @return builder
     */
    public BubblePathBuilder setBubbleHeight(int height) {
        this.mBubbleHeight = height;
        return this;
    }

    /**
     * 生成轮廓路径
     *
     * @return path
     */
    public Path create() {
        checkArgs();
        return generateBubblePath();
    }

    /**
     * TODO
     * 检查各个参数是否正常
     */
    private void checkArgs() {

    }

    /**
     * 生成目标Path
     *
     * @return path
     */
    private Path generateBubblePath() {
        mClipPath.reset();
        switch (mBubbleType) {
            case Constants.TYPE_BUBBLE_LEFT: {
                createLeftArrowPath();
                break;
            }
            case Constants.TYPE_BUBBLE_TOP: {
                createTopArrowPath();
                break;
            }
            case Constants.TYPE_BUBBLE_RIGHT: {
                createRightArrowPath();
                break;
            }
            case Constants.TYPE_BUBBLE_BOTTOM: {
                createBottomArrowPath();
                break;
            }
            default:
                break;
        }
        return mClipPath;
    }

    /**
     * 生成左边箭头的外部轮廓路径
     */
    private void createLeftArrowPath() {
        mCenterArrow = getArrowCenter();
        mClipPath.moveTo(mLeftTopCornerRadius + mArrowHeight, 0);
        mClipPath.lineTo(mBubbleWidth - mRightTopCornerRadius, 0);
        mClipPath.arcTo(mBubbleWidth - mRightTopCornerRadius * 2, 0, mBubbleWidth, mRightTopCornerRadius * 2, -90, 90, false);
        mClipPath.lineTo(mBubbleWidth, mBubbleHeight - mRightBottomCornerRadius);
        mClipPath.arcTo(mBubbleWidth - mRightBottomCornerRadius * 2, mBubbleHeight - mRightBottomCornerRadius * 2, mBubbleWidth, mBubbleHeight, 0, 90, false);
        mClipPath.lineTo(mLeftBottomCornerRadius + mArrowHeight, mBubbleHeight);
        mClipPath.arcTo(mArrowHeight, mBubbleHeight - mLeftBottomCornerRadius * 2, mArrowHeight + mLeftBottomCornerRadius * 2, mBubbleHeight, 90, 90, false);
        //+0是为了避免lint提示，x，left，right，width才属于同一方向，所以直接放height会提示
        mClipPath.lineTo(mArrowHeight + 0, mCenterArrow + mArrowWidth * 0.5f);
        mClipPath.lineTo(0, mCenterArrow);
        mClipPath.lineTo(mArrowHeight + 0, mCenterArrow - mArrowWidth * 0.5f);
        mClipPath.lineTo(mArrowHeight + 0, mLeftTopCornerRadius);
        mClipPath.arcTo(mArrowHeight, 0, mArrowHeight + mLeftTopCornerRadius * 2, mLeftTopCornerRadius * 2, 180, 90, false);
        mClipPath.close();
    }

    /**
     * 生成顶部箭头的外部轮廓路径
     */
    private void createTopArrowPath() {
        mCenterArrow = getArrowCenter();
        mClipPath.moveTo(mLeftTopCornerRadius, mArrowHeight);
        mClipPath.lineTo(mCenterArrow - mArrowWidth * 0.5f, mArrowHeight);
        mClipPath.lineTo(mCenterArrow, 0);
        mClipPath.lineTo(mCenterArrow + mArrowWidth * 0.5f, mArrowHeight);
        mClipPath.lineTo(mBubbleWidth - mRightTopCornerRadius, mArrowHeight);
        mClipPath.arcTo(mBubbleWidth - mRightTopCornerRadius * 2, mArrowHeight, mBubbleWidth, mArrowHeight + mRightTopCornerRadius * 2, -90, 90, false);
        mClipPath.lineTo(mBubbleWidth, mBubbleHeight - mRightBottomCornerRadius);
        mClipPath.arcTo(mBubbleWidth - mRightBottomCornerRadius * 2, mBubbleHeight - mRightBottomCornerRadius * 2, mBubbleWidth, mBubbleHeight, 0, 90, false);
        mClipPath.lineTo(mLeftBottomCornerRadius, mBubbleHeight);
        mClipPath.arcTo(0, mBubbleHeight - mLeftBottomCornerRadius * 2, mLeftBottomCornerRadius * 2, mBubbleHeight, 90, 90, false);
        mClipPath.lineTo(0, mLeftTopCornerRadius + mArrowHeight);
        mClipPath.arcTo(0, mArrowHeight, mLeftTopCornerRadius * 2, mArrowHeight + mLeftTopCornerRadius * 2, 180, 90, false);
        mClipPath.close();
    }

    /**
     * 生成右边箭头的外部轮廓路径
     */
    private void createRightArrowPath() {
        mCenterArrow = getArrowCenter();
        mClipPath.moveTo(mLeftTopCornerRadius, 0);
        mClipPath.lineTo(mBubbleWidth - mRightTopCornerRadius - mArrowHeight, 0);
        mClipPath.arcTo(mBubbleWidth - mRightTopCornerRadius * 2 - mArrowHeight, 0, mBubbleWidth - mArrowHeight, mRightTopCornerRadius * 2, -90, 90, false);
        mClipPath.lineTo(mBubbleWidth - mArrowHeight, mCenterArrow - mArrowWidth * 0.5f);
        mClipPath.lineTo(mBubbleWidth, mCenterArrow);
        mClipPath.lineTo(mBubbleWidth - mArrowHeight, mCenterArrow + mArrowWidth * 0.5f);
        mClipPath.lineTo(mBubbleWidth - mArrowHeight, mBubbleHeight - mRightBottomCornerRadius);
        mClipPath.arcTo(mBubbleWidth - mRightBottomCornerRadius * 2 - mArrowHeight, mBubbleHeight - mRightBottomCornerRadius * 2, mBubbleWidth - mArrowHeight, mBubbleHeight, 0, 90, false);
        mClipPath.lineTo(mLeftBottomCornerRadius, mBubbleHeight);
        mClipPath.arcTo(0, mBubbleHeight - mLeftBottomCornerRadius * 2, mLeftBottomCornerRadius * 2, mBubbleHeight, 90, 90, false);
        mClipPath.lineTo(0, mLeftTopCornerRadius);
        mClipPath.arcTo(0, 0, mLeftTopCornerRadius * 2, mLeftTopCornerRadius * 2, 180, 90, false);
        mClipPath.close();
    }

    /**
     * 生成底部箭头的外部轮廓路径
     */
    private void createBottomArrowPath() {
        mCenterArrow = getArrowCenter();
        mClipPath.moveTo(mLeftTopCornerRadius, 0);
        mClipPath.lineTo(mBubbleWidth - mRightTopCornerRadius, 0);
        mClipPath.arcTo(mBubbleWidth - mRightTopCornerRadius * 2, 0, mBubbleWidth, mRightTopCornerRadius * 2, -90, 90, false);
        mClipPath.lineTo(mBubbleWidth, mBubbleHeight - mRightBottomCornerRadius - mArrowHeight);
        mClipPath.arcTo(mBubbleWidth - mRightBottomCornerRadius * 2, mBubbleHeight - mRightBottomCornerRadius * 2 - mArrowHeight, mBubbleWidth, mBubbleHeight - mArrowHeight, 0, 90, false);
        mClipPath.lineTo(mCenterArrow + mArrowWidth * 0.5f, mBubbleHeight - mArrowHeight);
        mClipPath.lineTo(mCenterArrow, mBubbleHeight);
        mClipPath.lineTo(mCenterArrow - mArrowWidth * 0.5f, mBubbleHeight - mArrowHeight);
        mClipPath.lineTo(mLeftBottomCornerRadius, mBubbleHeight - mArrowHeight);
        mClipPath.arcTo(0, mBubbleHeight - mLeftBottomCornerRadius * 2 - mArrowHeight, mLeftBottomCornerRadius * 2, mBubbleHeight - mArrowHeight, 90, 90, false);
        mClipPath.lineTo(0, mLeftTopCornerRadius);
        mClipPath.arcTo(0, 0, mLeftTopCornerRadius * 2, mLeftTopCornerRadius * 2, 180, 90, false);
        mClipPath.close();
    }

    /**
     * 计算箭头顶点所在边的位置
     *
     * @return 位置
     */
    private float getArrowCenter() {
        final float baseLine = mBubbleType == Constants.TYPE_BUBBLE_BOTTOM || mBubbleType == Constants.TYPE_BUBBLE_TOP ? mBubbleWidth : mBubbleHeight;
        final float ratioForPosition = mArrowType == Constants.TYPE_POSITION_LEFT ? 1 : (mArrowType == Constants.TYPE_POSITION_CENTER ? 0 : -1);
        final float ratioFor = mBubbleType == Constants.TYPE_BUBBLE_LEFT || mBubbleType == Constants.TYPE_BUBBLE_BOTTOM ? 1 : -1;
        return baseLine * 0.5f + ratioFor * ratioForPosition * (baseLine * 0.5f - mArrowOffset - mArrowWidth * 0.5f);
    }
}
