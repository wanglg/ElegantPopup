package com.leo.uilib.popup.core;

import android.content.Context;

import androidx.annotation.Nullable;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * @Author: wangliugeng
 * @Date : 2020/3/20
 * @Email: leo3552@163.com
 * @Desciption:
 */
public class PopupManager {
    private static PopupManager popupManager;
    private LinkedHashMap<String, List<SoftReference<IPopup>>> linkedHashMap;

    private PopupManager() {
        linkedHashMap = new LinkedHashMap<>();
    }

    public static PopupManager getPopupManager() {
        if (popupManager == null) {
            synchronized (PopupManager.class) {
                if (popupManager == null) {
                    popupManager = new PopupManager();
                }
            }
        }
        return popupManager;
    }

    public void addPopup(Context context, IPopup iPopup) {
        if (context == null) {
            return;
        }
        String key = context.getClass().getName();
        List<SoftReference<IPopup>> list = linkedHashMap.get(key);
        if (list == null) {
            list = new ArrayList<>();
            list.add(new SoftReference<>(iPopup));
            linkedHashMap.put(key, list);
        } else {
            list.add(new SoftReference<>(iPopup));
        }

    }

    public void removePopup(Context context, IPopup iPopup) {
        if (context == null) {
            return;
        }
        String key = context.getClass().getName();
        List<SoftReference<IPopup>> mTask = linkedHashMap.get(key);
        if (mTask != null) {
            Iterator<SoftReference<IPopup>> iterator = mTask.iterator();
            while (iterator.hasNext()) {
                SoftReference<IPopup> item = iterator.next();
                if (item == null) {
                    continue;
                }
                IPopup itemPopup = item.get();
                if (itemPopup != null && itemPopup == iPopup) {
                    iterator.remove();
                }
            }
//            for (int i = mTask.size() - 1; i >= 0; i--) {
//                SoftReference<IPopup> item = mTask.get(i);
//                if (item == null) {
//                    continue;
//                }
//                IPopup itemPopup = item.get();
//                if (itemPopup != null && itemPopup == iPopup) {
//                    mTask.remove(i);
//                }
//            }
            if (mTask.size() == 0) {
                linkedHashMap.remove(key);
            }
        }

    }

    @Nullable
    public IPopup getTopPopup(Context context) {
        if (context == null) {
            return null;
        }
        String key = context.getClass().getName();
        List<SoftReference<IPopup>> popupList = linkedHashMap.get(key);
        if (popupList == null || popupList.isEmpty()) {
            return null;
        }
        for (int i = popupList.size() - 1; i >= 0; i--) {
            IPopup last = popupList.get(i).get();
            if (last == null) {
                continue;
            }
            if (last.isDismiss()) {
                continue;
            }
            return last;
        }
        return null;
    }

    public List<IPopup> getAllPopup(Context context) {
        List<IPopup> result = new ArrayList<>();
        if (context != null) {
            String key = context.hashCode() + "";
            List<SoftReference<IPopup>> popupList = linkedHashMap.get(key);
            if (popupList != null && popupList.size() > 0) {
                for (int i = 0; i < popupList.size(); i++) {
                    IPopup last = popupList.get(i).get();
                    if (last == null) {
                        continue;
                    }
                    if (last.isDismiss()) {
                        continue;
                    }
                    result.add(last);
                }
            }


        }
        return result;
    }

    public void dismissAllPopup(Context context) {
        if (context == null) {
            return;
        }
        for (IPopup popup : getAllPopup(context)) {
            popup.dismiss();
        }
    }
}
