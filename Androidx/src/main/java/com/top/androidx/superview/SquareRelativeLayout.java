package com.top.androidx.superview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.top.androidx.R;

/**
 * 正方形的RelativeLayout
 * Aspect ratio: 宽高比，以宽为基准
 */
public class SquareRelativeLayout extends RelativeLayout {

    /**
     * 宽高比
     */
    private float AspectRatio = 1.0F;

    public SquareRelativeLayout(Context context) {
        super(context);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }


    public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SquareRelativeLayout);
        AspectRatio = typedArray.getFloat(R.styleable.SquareRelativeLayout_aspect_ratio, AspectRatio);
        typedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置自己测量结果
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), (int) (getDefaultSize(0, heightMeasureSpec) * AspectRatio));
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
        int min = Math.min(widthMeasureSpec, heightMeasureSpec);

        super.onMeasure(min, (int) (min * AspectRatio));
    }
}
