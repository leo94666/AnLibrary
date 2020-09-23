package com.top.arch.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;



public abstract class BaseXFragment<B extends ViewDataBinding> extends Fragment implements BaseInterface {

    protected B mDataBinding;

    @Override
    public abstract int getLayout();

    @Override
    public abstract void init(View view);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, getLayout(), container, false);
        mDataBinding.setLifecycleOwner(this);
        View rootView = mDataBinding.getRoot();
        init(rootView);
        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDataBinding != null)
            mDataBinding.unbind();
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    @Override
    public void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void setScreenSensor(boolean isAuto) {
        if (isAuto) {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        } else {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }
    }

    /**
     * @param msg
     */
    @Override
    public void showLogToast(String msg) {
        //Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        //ToastUtils.showToast(getContext(), msg);
    }


}
