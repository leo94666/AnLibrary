package com.top.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


public class GifActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

//    fun generateGIF(): ByteArray? {
//        val bitmaps: ArrayList<Bitmap> = adapter.getBitmapArray()
//        val bos = ByteArrayOutputStream()
//        val encoder = AnimatedGifEncoder()
//        encoder.start(bos)
//        for (bitmap in bitmaps) {
//            encoder.addFrame(bitmap)
//        }
//        encoder.finish()
//        return bos.toByteArray()
//    }
//
//    fun saveGif() {
//        try {
//            val outStream = FileOutputStream("/sdcard/test.gif")
//            outStream.write(generateGIF())
//            outStream.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

}