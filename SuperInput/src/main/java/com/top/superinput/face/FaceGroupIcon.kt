package com.top.superinput.face

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.top.superinput.R

public class FaceGroupIcon : RelativeLayout {


    private var faceTabIcon: AppCompatImageView? = null


    constructor(context: Context?) : super(context) {
        initView(context)
    }


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)

    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)

    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)

    }


    private fun initView(context: Context?) {
        var view = LayoutInflater.from(context).inflate(R.layout.face_group_icon, this)
        faceTabIcon = findViewById(R.id.face_group_tab_icon)
    }

    fun setFaceTabIcon(bitmap: Bitmap?) {
        faceTabIcon?.setImageBitmap(bitmap)
    }
}