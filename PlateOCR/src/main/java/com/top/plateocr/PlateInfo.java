package com.top.plateocr;

import android.graphics.Bitmap;

public class PlateInfo {

    /**
     * 车牌号
     */
    public String plateName;

    /**
     * 车牌号图片
     */
    public Bitmap bitmap;

    public PlateInfo() {
    }

    public PlateInfo(String plateName, Bitmap bitmap) {
        this.plateName = plateName;
        this.bitmap = bitmap;
    }
}
