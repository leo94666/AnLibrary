package com.top.androidx.graffiti;

public enum StrokeEditMode {

    EDIT_STROKE(1),
    EDIT_PHOTO(2);


    private int index = 0;
    StrokeEditMode(int index) {
        this.index=index;
    }
}
