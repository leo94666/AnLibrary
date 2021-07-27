package com.top.arch.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class ScreenShotUtils {

    private static String TAG = "ScreenShotUtils";


    public static Bitmap screenShot(@NonNull final Activity activity, boolean isDeleteStatusBar, boolean has) {
        View decorView = activity.getWindow().getDecorView();
        Bitmap bmp;
        if (has) {
            bmp = viewGroup2Bitmap((ViewGroup) decorView);
        } else {
            bmp = view2Bitmap(decorView);
        }
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (isDeleteStatusBar) {
            int statusBarHeight = getStatusBarHeight(activity);
            return Bitmap.createBitmap(
                    bmp,
                    0,
                    statusBarHeight,
                    dm.widthPixels,
                    dm.heightPixels - statusBarHeight
            );
        } else {
            return Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels);
        }
    }

    public static Bitmap screenShot(@NonNull final Activity activity, boolean has) {
        return screenShot(activity, true, has);
    }


    public static Bitmap screenShot(@NonNull final View view, boolean has) {
        Bitmap bmp;
        if (has) {
            bmp = viewGroup2Bitmap((ViewGroup) view);
        } else {
            bmp = view2Bitmap(view);
        }
        return bmp;
    }

    public static Bitmap screenShot(@NonNull final View view, boolean has, Rect rect) {
        Bitmap bitmap = screenShot(view, has);
        return cut(bitmap, rect);
    }


    private static Bitmap view2Bitmap(final View view) {
        if (view == null) return null;
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

    private static Bitmap viewGroup2Bitmap(ViewGroup viewGroup) {
        Bitmap parentBitmap = view2Bitmap(viewGroup);
        Bitmap newBitmap = null;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = viewGroup.getChildAt(i);
            int top = child.getTop();
            int left = child.getLeft();
            if (child instanceof SurfaceView) {
                // ((SurfaceView) child).getB
            } else if (child instanceof TextureView) {
                Bitmap bitmap = ((TextureView) child).getBitmap();
                newBitmap = merge(parentBitmap, top, left, bitmap);
            } else if (child instanceof ViewGroup) {
                Bitmap bitmap = viewGroup2Bitmap((ViewGroup) child);
                newBitmap = merge(parentBitmap, top, left, bitmap);
            }
        }
        return newBitmap;
    }


    private static Bitmap merge(Bitmap parentBitmap, int x, int y, Bitmap childBitmap) {
        int parentBitmapWidth = parentBitmap.getWidth();
        int parentBitmapHeight = parentBitmap.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(parentBitmapWidth, parentBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bitmap);
        cv.drawBitmap(parentBitmap, 0, 0, null);
        cv.drawBitmap(childBitmap, x, y, null);
        cv.save();
        cv.restore();
        return bitmap;
    }

    private static Bitmap cut(Bitmap parentBitmap, Rect rect) {
        Bitmap bitmap = Bitmap.createBitmap(parentBitmap, rect.left, rect.top, rect.width(), rect.height());
        return bitmap;
    }


    private static int getStatusBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
