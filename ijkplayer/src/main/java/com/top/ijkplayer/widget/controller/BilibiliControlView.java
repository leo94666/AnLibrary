package com.top.ijkplayer.widget.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.top.ijkplayer.R;

public class BilibiliControlView extends FrameLayout {

    public BilibiliControlView(@NonNull Context context) {
        //super(context);
        this(context,null);
    }

    public BilibiliControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        //super(context, attrs);
        this(context, attrs, 0);
    }

    public BilibiliControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        //super(context, attrs, defStyleAttr);
        this(context, attrs, defStyleAttr,0);

    }

    public BilibiliControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(getContext(), R.layout.control_view_bilibili, this);

    }




}
