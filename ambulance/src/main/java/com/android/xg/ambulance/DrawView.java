package com.android.xg.ambulance;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DrawView extends View {

    private Context mContext;
    private Paint mPaint;
    //存储单条线段的point
    private ArrayList<float[]> mPaths;
    //存储多条线段Path
    private volatile ArrayList<ArrayList<float[]>> mAllPaths;

    public DrawView(Context context) {
        super(context);
        initView(context);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        setBackground(null);
        this.mContext = context;
        mPaths = new ArrayList<>();
        mAllPaths = new ArrayList<>();
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(6);
    }

    private void setPaint(String color, float strokeWidth) {
        mPaint.setColor(Color.parseColor(color));
        mPaint.setStrokeWidth(strokeWidth);
    }

    private void addPath(ActionMark msg) {
        if (mPaths != null) {
//            if (msg.getPoint()[0] == -1 || msg.getPoint()[1] == -1) {
//                //一笔开始
//                //mPaths.clear();
//            } else if (msg.getPoint()[0] == -2 || msg.getPoint()[1] == -2) {
//                //一笔结束
//                mAllPaths.add(mPaths);
//                //mAllPaths.addAll(mAllPaths);
//                mPaths.clear();
//            } else {
//                mPaths.add(msg.getPoint());
//            }
            mPaths.add(msg.getPoint());

            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        for (int j = 0; j < mPaths.size(); j++) {
            canvas.drawPoints(mPaths.get(j), mPaint);
            if (j != mPaths.size() - 1) {
                float x1 = mPaths.get(j)[0];
                float y1 = mPaths.get(j)[1];
                float x2 = mPaths.get(j + 1)[0];
                float y2 = mPaths.get(j + 1)[1];
                if (x1 == -1 || x1 == -1.0) {
                    continue;
                }
                if (x2 == -2 || x2 == -1.0) {
                    continue;
                }
                canvas.drawLine(x1, y1, x2, y2, mPaint);

            }
        }

//        for (int i = 0; i < mAllPaths.size(); i++) {
//            //获取所有的笔数
//            ArrayList<float[]> path = mAllPaths.get(i);
//            for (int j = 0; j < path.size(); j++) {
//                float[] point = path.get(j);
//                canvas.drawPoints(point, mPaint);
//                if (j != path.size() - 1) {
//                    float[] nextPoint = path.get(j + 1);
//                    canvas.drawLine(point[0], point[1], nextPoint[0], nextPoint[1], mPaint);
//                }
//            }
//        }
    }

    private void setPaint(ActionMark msg) {
        // setPaint(msg.getColoor(), msg.getStrokeWidth());
    }

    public void drawMark(@NotNull ActionMark msg) {
        setPaint(msg);
        addPath(msg);
        postInvalidate();
    }

    public void clearMark() {
        mPaths.clear();
        postInvalidate();
    }
}
