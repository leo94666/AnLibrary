package com.top.superinput.layout;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.top.superinput.base.BaseInputFragment;
import com.top.superinput.layout.InputLayoutUI;
import com.top.superinput.more.InputMoreActionUnit;

public class SuperInputLayout extends InputLayoutUI {
    public SuperInputLayout(Context context) {
        super(context);
    }

    public SuperInputLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperInputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SuperInputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
