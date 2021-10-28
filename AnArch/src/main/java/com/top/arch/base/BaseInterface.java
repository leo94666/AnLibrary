package com.top.arch.base;

import android.os.IBinder;
import android.view.View;

public interface BaseInterface {


    abstract int getLayout();

    abstract void init(View root);

    void keepScreenOn();

    void setFullScreen();

    void exitFullScreen();

    void hideBottomUIMenu();

    void showBottomUIMenu();

    void hideKeyboard(IBinder token);

    void setScreenSensor(boolean isAuto);

    void showLogToast(String msg);
}
