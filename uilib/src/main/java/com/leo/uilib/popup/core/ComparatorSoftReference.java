package com.leo.uilib.popup.core;

import java.lang.ref.SoftReference;

/**
 * @Author: wangliugeng
 * @Date : 2020/4/23
 * @Email: leo3552@163.com
 * @Desciption:
 */
public class ComparatorSoftReference<T extends IPopup> extends SoftReference<T> implements Comparable<ComparatorSoftReference> {

    public ComparatorSoftReference(T referent) {
        super(referent);
    }

    @Override
    public int compareTo(ComparatorSoftReference o) {
        if (get() == null) {
            return -1;
        } else if (o.get() == null) {
            return 1;
        }
        IPopup popup1 = get();
        IPopup popup2 = (IPopup) o.get();
        return popup1.compareTo(popup2);
    }

}
