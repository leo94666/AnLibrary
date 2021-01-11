package com.top.sample

import android.content.Context
import android.graphics.Color
import android.graphics.Color.GREEN
import android.os.Bundle
import android.os.Environment
import android.view.Gravity.CENTER
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.top.androidx.supertoast.Style
import com.top.androidx.supertoast.Style.DURATION_VERY_SHORT
import com.top.androidx.supertoast.SuperActivityToast
import com.top.androidx.supertoast.SuperToast
import com.top.androidx.supertoast.utils.PaletteUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //SuperToast.create(this,"MMMM",Style.DURATION_VERY_SHORT).show()

        val externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Toast.makeText(this,externalFilesDir?.absolutePath,Toast.LENGTH_LONG).show()
        Toast.makeText(this,"!!!!!!!!!!!",Toast.LENGTH_LONG).show()
        tv.setOnClickListener {


            SuperActivityToast.create(
                this,
                Style(),
                Style.TYPE_BUTTON
            )
                .setButtonText("UNDO")
                .setButtonIconResource(R.drawable.ic_launcher_background)
                .setOnButtonClickListener("good_tag_name", null) { view, token -> {} }
                .setProgressBarColor(Color.WHITE)
                .setText("Email deleted")
                .setDuration(Style.DURATION_LONG)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PURPLE))
                .setAnimations(Style.ANIMATIONS_POP).show()


        }







    }
}