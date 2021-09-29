package com.top;

import android.graphics.Bitmap;

import org.opencv.videoio.VideoCapture;

public class OpenCVKits {
    static {
        System.loadLibrary("openCVUtils");
    }


    public static native int[]  threshold(int[] pix, int w, int h);

    /**
     * 二值化
     * @param pix
     * @param w
     * @param h
     * @return
     */
    public static native int[] gray(int[] pix, int w, int h);

    private static native int[] test(int[] pix, int w, int h);

    public static Bitmap testDemo(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int[] resultPixes = test(pix, w, h);
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        result.setPixels(resultPixes, 0, w, 0, 0, w, h);
        return result;
    }

    //public static native double cv_ImageBinarization(int[] pixelsRaw,int w,int h);
    //public static native void fld_lines();
}
