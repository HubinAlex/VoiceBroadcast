package com.example.voicebroadcast;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

/**
 * Created by Administrator on 2019/3/19.
 */

public class BaseApp extends MultiDexApplication {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Application getContext() {
        return instance;
    }
}
