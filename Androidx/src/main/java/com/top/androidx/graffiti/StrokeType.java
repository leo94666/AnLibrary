package com.top.androidx.graffiti;

public enum StrokeType {

    STROKE_TYPE_ERASER(1),
    STROKE_TYPE_DRAW(2),
    STROKE_TYPE_LINE(3),
    STROKE_TYPE_CIRCLE(4),
    STROKE_TYPE_RECTANGLE(5),
    STROKE_TYPE_TEXT(6);

    private int index =0;
    StrokeType(int index) {
        this.index=index;
    }
}
