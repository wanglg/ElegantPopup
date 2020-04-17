package com.leo.uilib.popup.core;

/**
 * @Author: wangliugeng
 * @Date : 2019-09-06
 * @Email: leo3552@163.com
 * @Desciption:
 */
public interface IBack {
    /**
     * 处理back事件。
     *
     * @return True: 表示已经处理; False: 没有处理，让基类处理。
     */
    boolean onBack();
}
