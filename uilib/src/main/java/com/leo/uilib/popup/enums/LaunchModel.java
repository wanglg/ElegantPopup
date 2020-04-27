package com.leo.uilib.popup.enums;

/**
 * @Author: wangliugeng
 * @Date : 2020/4/16
 * @Email: leo3552@163.com
 * @Desciption:
 */
public enum LaunchModel {
    /**
     * 默认一直加到最上层
     */
    DEFAULT,
    /**
     * 同一类型窗体，后加入做丢弃处理
     */
    DROP,
    /**
     * 同一类型窗体，先加入的丢弃处理
     */
    LATEST,
    /**
     * 当前存在窗体时，缓存后加入的窗体
     */
    BUFFER
}
