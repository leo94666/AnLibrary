package com.top.sample

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.top.androidx.pick.pickview.builder.TimePickerBuilder
import com.top.androidx.pick.pickview.listener.OnTimeSelectListener
import com.top.arch.Top
import com.top.arch.util.CrashUtils.init
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //SuperToast.create(this,"MMMM",Style.DURATION_VERY_SHORT).show()

        val externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        //click.setOn


        T


        tv.setOnClickListener {
            //时间选择器

            //时间选择器
            val pvTime =
                TimePickerBuilder(this@MainActivity,
                    OnTimeSelectListener { date, v ->
                        Toast.makeText(
                            this@MainActivity,
                            date.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }).build().show()
        }
    }
}