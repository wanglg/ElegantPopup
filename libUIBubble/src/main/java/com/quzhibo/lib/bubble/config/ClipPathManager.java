package com.quzhibo.lib.bubble.config;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.Nullable;

/**
 * 裁切工具实现类
 *
 * @author yeluodev1226
 * @date 2020-03-07 11:35
 */
public class ClipPathManager implements ClipManager {

    private final Path path = new Path();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ClipPathCreator clipPathCreator = null;

    public ClipPathManager() {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
    }

    @Override
    public Path createMask(int width, int height) {
        return path;
    }

    @Override
    public Path getShadowConvexPath() {
        return path;
    }

    @Override
    public void setupClipLayout(int width, int height) {
        path.reset();
        final Path clipPath = createClipPath(width, height);
        if (clipPath != null) {
            path.set(clipPath);
        }
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public boolean requiresBitmap() {
        return clipPathCreator != null && clipPathCreator.requiresBitmap();
    }

    @Nullable
    private Path createClipPath(int width, int height) {
        if (clipPathCreator != null) {
            return clipPathCreator.createClipPath(width, height);
        }
        return null;
    }

    /**
     * 设置生成裁切路径
     *
     * @param clipPathCreator 生成裁切路径接口
     */
    public void setClipPathCreator(ClipPathCreator clipPathCreator) {
        this.clipPathCreator = clipPathCreator;
    }

    /**
     * 裁切路径生成接口
     */
    public interface ClipPathCreator {
        /**
         * 根据画布大小生成裁切路径
         *
         * @param width  画布宽
         * @param height 画布高
         * @return path
         */
        Path createClipPath(int width, int height);

        /**
         * 是否需要绘制bitmap
         *
         * @return boolean
         */
        boolean requiresBitmap();
    }
}
