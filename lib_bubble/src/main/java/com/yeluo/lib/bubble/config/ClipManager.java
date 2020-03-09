package com.yeluo.lib.bubble.config;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * 裁切工具接口
 *
 * @author yeluodev1226
 * @date 2020-03-07 11:31
 */
public interface ClipManager {
    /**
     * 获取裁切path
     *
     * @return path
     */
    Path createMask(int width, int height);

    /**
     * 获取裁切path
     *
     * @return path
     */
    Path getShadowConvexPath();

    /**
     * 设置裁切path
     *
     * @param width  宽
     * @param height 高
     */
    void setupClipLayout(int width, int height);

    /**
     * 获取画笔
     *
     * @return paint
     */
    Paint getPaint();

    /**
     * 是否需要绘制bitmap
     *
     * @return boolean
     */
    boolean requiresBitmap();
}
