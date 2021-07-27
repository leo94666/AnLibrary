package com.top.screenshot;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ScreenShot {
    private static String TAG = "ScreenShotUtils";

    /**
     * 本实例
     */
    private static ScreenShot instance;

    /**
     * 私有的构造方法
     */
    private ScreenShot() {

    }

    /**
     * 单例的实例
     *
     * @return this
     */
    public static ScreenShot getInstance() {
        if (instance == null) {
            synchronized (ScreenShot.class) {
                if (instance == null) {
                    instance = new ScreenShot();
                }
            }
        }
        return instance;
    }

    public  Bitmap screenShot(@NonNull final Activity activity) {
        return screenShot(activity, false);
    }
    public  Bitmap screenShot(@NonNull  Activity activity, boolean isDeleteStatusBar){
        View decorView = activity.getWindow().getDecorView();
        Bitmap bmp = view2Bitmap(decorView);
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


    public Bitmap screenShot(ViewGroup view) {
        Bitmap parentBitmap = view2Bitmap(view);
        root = new ViewTreeNode<>(null);
        root.parent = null;
        traverse(root, view);
        ArrayList<ViewTreeNode<ViewBean>> leafNode = getLeafNode(root);
        for (ViewTreeNode<ViewBean> viewBeanViewTreeNode : leafNode) {
            if (viewBeanViewTreeNode.value.isHas()) {
                Rect rect = getRect(viewBeanViewTreeNode);
                Bitmap bitmap = getBitmap(viewBeanViewTreeNode);
                Log.i(TAG, "Rect: " + rect.toString());
                parentBitmap = merge(parentBitmap, rect.left, rect.top, bitmap);
            }
        }
        return parentBitmap;
    }

    public  Bitmap screenShot(ViewGroup view, Rect rect) {
        Bitmap bitmap = screenShot(view);
        return cut(bitmap,rect);
    }

    public  Bitmap screenShot(@NonNull final Activity activity, Rect rect) {
        Bitmap bitmap = screenShot(activity);
        return cut(bitmap,rect);
    }

    private static Bitmap cut(Bitmap parentBitmap, Rect rect) {
        Bitmap bitmap = Bitmap.createBitmap(parentBitmap, rect.left, rect.top, rect.width(), rect.height());
        return bitmap;
    }

    private Bitmap view2Bitmap(View view) {
        if (view == null) return null;
        Log.i(TAG, "view2Bitmap: " + view.toString());
        if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0) return null;
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


    private ViewTreeNode<ViewBean> root;

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

    private void traverse(ViewTreeNode<ViewBean> root, ViewGroup viewGroup) {
        if (root.value == null) {
            root.value = generate(viewGroup);
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = viewGroup.getChildAt(i);
            ViewBean viewBean = new ViewBean(child);
            ViewTreeNode<ViewBean> childNode = new ViewTreeNode<>(viewBean);
            root.addChildren(childNode);
            childNode.parent = root;
            if (child instanceof ViewGroup) {
                traverse(childNode, (ViewGroup) child);
            }
        }
    }


    private ArrayList<ViewTreeNode<ViewBean>> getLeafNode(ViewTreeNode<ViewBean> root) {
        ArrayList<ViewTreeNode<ViewBean>> data = new ArrayList<>();
        if (root.children == null) {
            data.add(root);
        } else {
            for (ViewTreeNode<ViewBean> child : root.children) {
                ArrayList<ViewTreeNode<ViewBean>> leafNode = getLeafNode(child);
                data.addAll(leafNode);
            }
        }
        return data;
    }


    private Rect getRect(ViewTreeNode<ViewBean> leaf) {
        return new Rect(getLeft(leaf), getTop(leaf), getLeft(leaf) + leaf.value.getWidth(), getTop(leaf) + leaf.value.getHeight());
    }

    private Bitmap getBitmap(ViewTreeNode<ViewBean> leaf) {
        return leaf.value.getBitmap();
    }

    private int getTop(ViewTreeNode<ViewBean> leaf) {
        if (leaf.parent != null) {
            int top = getTop(leaf.parent);
            return top + leaf.value.getTop();
        } else {
            return 0;
        }
    }

    private int getLeft(ViewTreeNode<ViewBean> leaf) {
        if (leaf.parent != null) {
            int left = getLeft(leaf.parent);
            return left + leaf.value.getView().getLeft();
        } else {
            return 0;
        }
    }


    private ViewBean generate(View view) {
        ViewBean viewBean = new ViewBean(view);
        return viewBean;
    }

    private  int getStatusBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

}