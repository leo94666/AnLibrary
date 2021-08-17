package com.top.ijkplayer.widget.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.top.ijkplayer.R;

public class BibibiliMediaControllerView extends MediaController {

    private Context mContext;
    private View mRoot;
    private static final int sDefaultTimeout = 3000;

    public BibibiliMediaControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BibibiliMediaControllerView(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public BibibiliMediaControllerView(Context context) {
        super(context);
    }

    private void initView(Context context) {
        this.mContext=context;

    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.media_controller, null);

        initControllerView(mRoot);

        return mRoot;
    }

    private void initControllerView(View mRoot) {

    }




    @Override
    public void showOnce(View view) {

    }
}
