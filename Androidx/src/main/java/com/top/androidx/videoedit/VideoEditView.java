package com.top.androidx.videoedit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.top.androidx.R;

/**
 * @author leo
 * @version 1.0
 * @className VideoEditView
 * @description TODO
 * @date 2022/2/15 15:08
 **/
public class VideoEditView extends RelativeLayout {

    private Context mContext;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private LinearLayout mSeekBarLayout;
    private ImageView mPositionIcon;


    public VideoEditView(Context context) {
        super(context);
        init(context);
    }

    public VideoEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }


    public VideoEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public VideoEditView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);

    }

    private void init(Context context) {
        this.mContext = context;
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_video_edit, this);
        mRecyclerView = mRootView.findViewById(R.id.id_rv_id);
        mSeekBarLayout = (LinearLayout) findViewById(R.id.id_seekBarLayout);
        mPositionIcon = (ImageView) findViewById(R.id.positionIcon);
    }
}
