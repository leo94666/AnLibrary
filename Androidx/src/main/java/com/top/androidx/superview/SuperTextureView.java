package com.top.androidx.superview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

public class SuperTextureView extends TextureView implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, View.OnTouchListener {


    private String TAG="SuperTextureView";
    private Context mContext;
    private GestureDetector mGestureDetector;

    private OnMultiClickListener onMultiClickListener;


    public SuperTextureView(Context context) {
        super(context);
        initView(context);
    }

    public SuperTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SuperTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    public SuperTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);

    }

    private void initView(Context context) {
        this.mContext = context;
        mGestureDetector = new GestureDetector(mContext, this);
        mGestureDetector.setOnDoubleTapListener(this);

        setOnTouchListener(this);
    }


    public interface OnMultiClickListener {
        void onDoubleClick();
    }

    public void setOnMultiClickListener(OnMultiClickListener onMultiClickListener) {
        this.onMultiClickListener = onMultiClickListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.e(TAG,"===================onSingleTapConfirmed");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.e(TAG,"===================onDoubleTap");
        if (onMultiClickListener != null) {
            onMultiClickListener.onDoubleClick();
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.e(TAG,"===================onDoubleTapEvent");

        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.e(TAG,"===================onDown");

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.e(TAG,"===================onShowPress");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.e(TAG,"===================onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.e(TAG,"===================onScroll");

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.e(TAG,"===================onLongPress");


    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.e(TAG,"===================onFling");

        return false;
    }


}
