package com.top.screenshot;

import android.graphics.Bitmap;
import android.view.TextureView;
import android.view.View;

public class ViewBean {

    private View view;
    private int top;
    private int left;
    private int width;
    private int height;

    private boolean has = false;

    private Bitmap bitmap = null;

    public ViewBean(View view) {
        this.view = view;
        this.left = view.getLeft();
        this.top = view.getTop();
        this.width = view.getWidth();
        this.height = view.getHeight();

        if (view instanceof TextureView) {
            has = true;
            bitmap = ((TextureView) view).getBitmap();
        }
    }


    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHas() {
        return has;
    }

    public void setHas(boolean has) {
        this.has = has;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
