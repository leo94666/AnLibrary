package com.top.plateocr;

public class PlateRecognition {

    static {
        System.loadLibrary("");
    }

    public static native long InitPlateRecognizer();


}
