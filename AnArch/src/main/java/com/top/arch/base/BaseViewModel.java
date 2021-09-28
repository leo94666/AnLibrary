package com.top.arch.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

/**
 * 基类
 */
public abstract class BaseViewModel<T> extends AndroidViewModel {

    public SingleLiveEvent<T> mEventHub = new SingleLiveEvent<>();

    public SingleLiveEvent<T> getEventHub() {
        return mEventHub;
    }

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }
}
