package com.uq.popupdemo;

import android.app.Application;
import android.content.Context;


public class ElegantPopupApp extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
