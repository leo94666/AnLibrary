package com.top.sample

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.top.androidx.graffiti.SketchActivity
import com.top.androidx.graffiti.bean.SketchData
import kotlinx.android.synthetic.main.activity_layout_scket.*

class ScketchActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_scket)
        sketch_view.setSketchData(SketchData())

        sketch_view.setBackgroundByPath(Environment.getExternalStorageDirectory().toString()+"/111111.png")


//        sketch_view.setBackgroundByPath("file:///android_asset/bg.png")

    }

    fun go(view: View) {
        SketchActivity.startSketchActivity(this)
    }
}