package com.top.screenshot;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

public class ScreenShotAdvance {
    private static String TAG = "ScreenShotUtils";

    /**
     * 本实例
     */
    private static ScreenShotAdvance instance;

    /**
     * 私有的构造方法
     */
    private ScreenShotAdvance() {

    }

    /**
     * 单例的实例
     *
     * @return this
     */
    public static ScreenShotAdvance getInstance() {
        if (instance == null) {
            synchronized (ScreenShotAdvance.class) {
                if (instance == null) {
                    instance = new ScreenShotAdvance();
                }
            }
        }
        return instance;
    }

    private Bitmap viewGroup2Bitmap(ViewGroup viewGroup) {
        Bitmap bitmap = view2Bitmap(viewGroup);
        boolean b = hasOverlap(viewGroup);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                Bitmap viewGroup2Bitmap = viewGroup2Bitmap((ViewGroup) childAt);
                if (bitmap != null) {
                    bitmap = merge(bitmap, childAt.getLeft(), childAt.getTop(), viewGroup2Bitmap);
                }
            } else {
                if (childAt instanceof TextureView) {
                    //此处需要获取TextureView的bitmap拼接到它的父ViewGroup上
                    Bitmap childBitmap = view2Bitmap(childAt);
                    if (bitmap != null) {
                        bitmap = merge(bitmap, childAt.getLeft(), childAt.getTop(), childBitmap);
                    }
                } else if (childAt instanceof SurfaceView) {

                } else {
                    //bitmap = view2Bitmap(childAt);

                }
            }
        }
        return bitmap;
    }


    private Bitmap view2Bitmap(View view) {
        if (view == null) return null;
        Log.i(TAG, "view2Bitmap: " + view.toString());
        if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0) return null;

        if (view instanceof SurfaceView) {
            return null;
        } else if (view instanceof TextureView) {
            return ((TextureView) view).getBitmap();
        } else {
            boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
            boolean willNotCacheDrawing = view.willNotCacheDrawing();
            view.setDrawingCacheEnabled(true);
            view.setWillNotCacheDrawing(false);
            Bitmap drawingCache = view.getDrawingCache();
            Bitmap bitmap;
            if (null == drawingCache || drawingCache.isRecycled()) {
                view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                view.buildDrawingCache();
                drawingCache = view.getDrawingCache();
                if (null == drawingCache || drawingCache.isRecycled()) {
                    bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(bitmap);
                    view.draw(canvas);
                } else {
                    bitmap = Bitmap.createBitmap(drawingCache);
                }
            } else {
                bitmap = Bitmap.createBitmap(drawingCache);
            }
            view.setWillNotCacheDrawing(willNotCacheDrawing);
            view.setDrawingCacheEnabled(drawingCacheEnabled);
            return bitmap;
        }
    }

    public Bitmap screenShot(ViewGroup view) {
        Bitmap bitmap = viewGroup2Bitmap(view);
        return bitmap;
    }

    /**
     * 判断是否有重叠部分
     *
     * @param viewGroup
     * @return
     */
    private boolean hasOverlap(ViewGroup viewGroup) {
        boolean hasOverlap = false;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            for (int j = i + 1; j < childCount - i; j++) {
                View childAt = viewGroup.getChildAt(i);
                View childAt1 = viewGroup.getChildAt(j);
                boolean overlap = isOverlap(getRect(childAt), getRect(childAt1));
                hasOverlap |= overlap;
            }
        }
        return hasOverlap;
    }


    private boolean isOverlap(Rect rect1, Rect rect2) {
        return !(rect2.bottom < rect1.top || rect1.bottom < rect2.top || rect1.left > rect2.right || rect2.left > rect1.right);
    }

    private Rect getRect(View view) {
        return new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getTop());
    }


    private Bitmap merge(Bitmap parentBitmap, int x, int y, Bitmap childBitmap) {
        int parentBitmapWidth = parentBitmap.getWidth();
        int parentBitmapHeight = parentBitmap.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(parentBitmapWidth, parentBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bitmap);
        cv.drawBitmap(parentBitmap, 0, 0, null);
        if (childBitmap != null) {
            cv.drawBitmap(childBitmap, x, y, null);
        }
        cv.save();
        cv.restore();
        return bitmap;
    }


    private int getStatusBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}