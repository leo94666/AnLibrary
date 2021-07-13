package com.top.androidx.superview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import com.top.androidx.R;

public class CircleView extends View {


    private float radius;
    private int color;
    private Paint mTextPain;                        //初始化画笔
    private int radiusMax;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadAttrs(context,attrs);
        configPaint();
    }



    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttrs(context,attrs);
        configPaint();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadAttrs(context,attrs);
        configPaint();
    }

    private void loadAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        radius = typedArray.getDimension(R.styleable.CircleView_android_radius, 1);
        color = typedArray.getColor(R.styleable.CircleView_android_color,Color.RED);
        typedArray.recycle();
    }

    private void configPaint() {
        mTextPain = new Paint();
        mTextPain.setColor(color);            //设置画笔颜色为白色
        mTextPain.setAntiAlias(true);               //开启抗锯齿，平滑文字和圆弧的边缘
        mTextPain.setTextAlign(Paint.Align.CENTER); //设置文本位于相对于原点的中间
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth() / 2;
        int height = getHeight() / 2;
        radiusMax = Math.min(width,height);

        canvas.drawCircle(width,height,radius,mTextPain);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }



    public void setRadius(@FloatRange(from = 0f, to = 1f) float percent){
        this.radius=radiusMax*percent;
        invalidate();
    }

    public void setColor(String color){
        this.color=Color.parseColor(color);
        mTextPain.setColor(Color.parseColor(color));            //设置画笔颜色为白色
        invalidate();
    }
}
