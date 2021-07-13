package com.top.androidx.graffiti;

public enum StrokeColor {

    STROKE_COLOR_BLACK("#ff000000"),
    STROKE_COLOR_RED("#ffff4444"),
    STROKE_COLOR_YELLOW("#ffff00"),
    STROKE_COLOR_GREEN("#008000"),
    STROKE_COLOR_PURPLE("#800080");


    private String color = "#ff000000";
    StrokeColor(String color) {
        this.color=color;
    }

    public String getColor() {
        return color;
    }
}
