package com.top.androidx.graffiti.bean;

import android.graphics.Bitmap;

import com.top.androidx.graffiti.StrokeEditMode;
import com.top.androidx.graffiti.StrokeType;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class SketchData {
    public List<PhotoRecord> photoRecordList;
    public List<StrokeRecord> strokeRecordList;
    public List<StrokeRecord> strokeRedoList;
    public Bitmap thumbnailBM;//缩略图文件
    public Bitmap backgroundBM;
    public StrokeType strokeType;
    public StrokeEditMode editMode;

    public SketchData() {
        strokeRecordList = new ArrayList<>();
        photoRecordList = new ArrayList<>();
        strokeRedoList = new ArrayList<>();
        backgroundBM = null;
        thumbnailBM = null;
        strokeType = StrokeType.STROKE_TYPE_DRAW;
        editMode = StrokeEditMode.EDIT_STROKE;
    }

}
