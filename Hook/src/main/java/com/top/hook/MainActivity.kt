package com.top.hook

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AppCompatButton>(R.id.btn).setOnClickListener {
            Toast.makeText(this,toastMessage(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun toastMessage(): String? {
        return "我未被劫持"
    }
}