package com.top.arch.base;

import android.app.Application;

import com.top.arch.util.AnUtils;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AnUtils.init(this);
    }
}
