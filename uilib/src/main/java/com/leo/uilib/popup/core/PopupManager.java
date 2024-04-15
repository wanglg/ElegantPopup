package com.leo.uilib.popup.core;

import android.content.Context;

import androidx.annotation.Nullable;

import com.leo.uilib.popup.enums.PopupType;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.PriorityQueue;


/**
 * @Author: wangliugeng
 * @Date : 2020/3/20
 * @Email: leo3552@163.com
 * @Desciption:
 */
public class PopupManager {
    private LinkedHashMap<String, List<SoftReference<IPopup>>> linkedHashMap;
    private LinkedHashMap<String, PriorityQueue<ComparatorSoftReference<IPopup>>> readyQueue;

    private PopupManager() {
        linkedHashMap = new LinkedHashMap<>();
        readyQueue = new LinkedHashMap<>();
    }

    private static final class PopupManagerHolder {
        static final PopupManager popupManager = new PopupManager();
    }

    public static PopupManager getPopupManager() {
        return PopupManagerHolder.popupManager;
    }

    public void addPopup(Context context, IPopup iPopup) {
        if (context == null) {
            return;
        }
        String key = context.hashCode() + "";
        List<SoftReference<IPopup>> list = linkedHashMap.get(key);
        if (list == null) {
            list = new ArrayList<>();
            list.add(new SoftReference<>(iPopup));
            linkedHashMap.put(key, list);
        } else {
            list.add(new SoftReference<>(iPopup));
        }

    }

    public void addQueue(Context context, IPopup iPopup) {
        if (context == null) {
            return;
        }
        String key = context.hashCode() + "";
        PriorityQueue<ComparatorSoftReference<IPopup>> queue = readyQueue.get(key);
        if (queue == null) {
            queue = new PriorityQueue<>();
            queue.offer(new ComparatorSoftReference<>(iPopup));
            readyQueue.put(key, queue);
        } else {
            queue.offer(new ComparatorSoftReference<>(iPopup));
            readyQueue.put(key, queue);
        }
    }

    @Nullable
    public IPopup getTopQueuePopup(Context context) {
        if (context == null) {
            return null;
        }
        String key = context.hashCode() + "";
        PriorityQueue<ComparatorSoftReference<IPopup>> popupQueue = readyQueue.get(key);
        if (popupQueue == null || popupQueue.isEmpty()) {
            return null;
        }
        return popupQueue.poll().get();
    }

    public void removePopup(Context context, IPopup iPopup) {
        if (context == null) {
            return;
        }
        String key = context.hashCode() + "";
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
        String key = context.hashCode() + "";
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

    @Nullable
    public IPopup getTopCenterPopup(Context context) {
        if (context == null) {
            return null;
        }
        String key = context.hashCode() + "";
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
            if (last.getPopupInfo() == null || last.getPopupInfo().popupType != PopupType.Center) {
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

    public IPopup getPopupById(Context context, int id) {
        IPopup iPopup = null;
        if (context != null && id != 0) {
            String key = context.hashCode() + "";
            List<SoftReference<IPopup>> popupList = linkedHashMap.get(key);
            if (popupList != null && popupList.size() > 0) {
                for (int i = 0; i < popupList.size(); i++) {
                    IPopup last = popupList.get(i).get();
                    if (last == null || last.getPopupInfo() == null) {
                        continue;
                    }
                    if (last.isDismiss()) {
                        continue;
                    }
                    if (last.getPopupInfo().id == id) {
                        iPopup = last;
                        break;
                    }
                }
            }
        }

        return iPopup;

    }

    public List<IPopup> getAllCenterPopup(Context context) {
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
                    if (last.getPopupInfo() != null && last.getPopupInfo().popupType == PopupType.Center) {
                        result.add(last);
                    }

                }
            }
        }
        return result;
    }

    public void dismissAllPopup(Context context) {
        if (context == null) {
            return;
        }
        String key = context.hashCode() + "";
        for (IPopup popup : getAllPopup(context)) {
            popup.dismiss();
        }
        PriorityQueue<ComparatorSoftReference<IPopup>> queue = readyQueue.get(key);
        if (queue != null) {
            queue.clear();
            readyQueue.remove(key);
        }
    }
}
