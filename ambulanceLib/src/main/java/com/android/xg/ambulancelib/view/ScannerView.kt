package com.android.xg.ambulancelib.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.android.xg.ambulancelib.R
import com.elab.libview.scannerview.YuvToRgbConverter
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import kotlinx.android.synthetic.main.view_scan.view.*
import java.util.*
import java.util.concurrent.Executors

/**
 * ELabMcAndroid
 * <p>
 * Created by 李阳 on 2020/9/17 0017
 * Copyright © 2020年 新广科技. All rights reserved.
 * <p>
 * Describe:扫码UI
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class ScannerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    RelativeLayout(context, attrs) {

    private val TAG = "ScannerView"


    private var mContext: Context? = null

    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private lateinit var bitmapBuffer: Bitmap

    private var ENCODE = 1111
    private lateinit var rootScannerView: View


    private var onScannerResultListener: ((success:Boolean,result:String) -> Unit)? = null
    private var onCloseScannerViewListener: (() -> Unit)? = null

    fun setOnScannerResultListener(onScannerResultListener: ((success:Boolean,result:String) -> Unit),onCloseScannerViewListener: (() -> Unit)){
        this.onScannerResultListener=onScannerResultListener
        this.onCloseScannerViewListener=onCloseScannerViewListener
    }




    init {
        initView(context)
        initListener()
    }

    private val mHandler: Handler = object : Handler() {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                ENCODE -> {
                    val bm = msg.obj as Bitmap
                    /*decodeAsyncTask = DecodeAsyncTask(this@ScanActivity)
                    decodeAsyncTask?.execute(bm)*/
                    val decodeFromPictureResult = decodeFromPicture(bm)
                    if (decodeFromPictureResult != null) {
                        //ToastUtils.showToast(this@ScanActivity, decodeFromPictureResult.text)
                        onScannerResultListener?.invoke(true,decodeFromPictureResult.text)
                    } else {
                        capture()
                    }
                }
            }
        }
    }

    private fun initView(context: Context) {
        mContext = context
        rootScannerView = LayoutInflater.from(mContext).inflate(R.layout.view_scan, this, true)
        initScan()
        initCameraX()
    }

    private fun initListener() {
        iv_close.setOnClickListener {
            onCloseScannerViewListener?.invoke()
        }
    }


    private fun initScan() {
        rootView.post {
            startTranslate()
        }
    }

    private fun startTranslate() {
        val toYDelta: Float = v_scanner_bar.width.toFloat()
        val translateAnimation = TranslateAnimation(0F, 0F, 0f, toYDelta) //根据控件高度设定动画始末位置
        translateAnimation.duration = 2000
        translateAnimation.repeatCount = Animation.INFINITE //无限循环
        translateAnimation.interpolator = DecelerateInterpolator()
        v_scanner_bar.startAnimation(translateAnimation)
        translateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                v_scanner_bar.clearAnimation()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun initCameraX() {
        view_finder.post {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(mContext!!)
            cameraProviderFuture.addListener(Runnable {
                // Camera provider is now guaranteed to be available
                val cameraProvider = cameraProviderFuture.get()
                // Set up the view finder use case to display camera preview
                val preview =
                    Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setTargetRotation(view_finder.display.rotation)
                        .build()

                // Create a new camera selector each time, enforcing lens facing
                val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(view_finder.display.rotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                val converter = YuvToRgbConverter(mContext!!)

                imageAnalysis.setAnalyzer(
                    Executors.newSingleThreadExecutor(),
                    ImageAnalysis.Analyzer { imageProxy ->
                        if (!::bitmapBuffer.isInitialized) {
                            // The image rotation and RGB image buffer are initialized only once
                            // the analyzer has started running
                            bitmapBuffer = Bitmap.createBitmap(
                                imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
                            )
                            capture()

                        }
                        // Convert the image to RGB and place it in our shared buffer
                        imageProxy.use {
                            converter.yuvToRgb(it.image!!, bitmapBuffer)
                        }
                    })


                // Apply declared configs to CameraX using the same lifecycle owner
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    mContext as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

                // Use the camera object to link our preview use case with the view
                preview.setSurfaceProvider(view_finder.createSurfaceProvider())
            }, ContextCompat.getMainExecutor(mContext!!))
        }

    }


    private fun decodeFromPicture(bitmap: Bitmap?): Result? {
        if (bitmap == null) return null
        val picWidth = bitmap.width
        val picHeight = bitmap.height
        val pix = IntArray(picWidth * picHeight)
        Log.e(
            TAG,
            "decodeFromPicture:图片大小： " + bitmap.byteCount / 1024 / 1024 + "M"
        )
        bitmap.getPixels(pix, 0, picWidth, 0, 0, picWidth, picHeight)
        //构造LuminanceSource对象
        val rgbLuminanceSource = RGBLuminanceSource(
            picWidth, picHeight, pix
        )
        val bb = BinaryBitmap(HybridBinarizer(rgbLuminanceSource))
        //因为解析的条码类型是二维码，所以这边用QRCodeReader最合适。
        val qrCodeReader = QRCodeReader()
        val hints: MutableMap<DecodeHintType, Any?> = EnumMap<DecodeHintType, Any>(
            DecodeHintType::class.java
        )
        hints[DecodeHintType.CHARACTER_SET] = "utf-8"
        hints[DecodeHintType.TRY_HARDER] = true
        var result: Result? = null
        return try {
            result = qrCodeReader.decode(bb, hints)
            result
        } catch (e: NotFoundException) {
            e.printStackTrace()
            null
        } catch (e: ChecksumException) {
            e.printStackTrace()
            null
        } catch (e: FormatException) {
            e.printStackTrace()
            null
        }
    }


    private fun capture() {
        val message = mHandler.obtainMessage(ENCODE, bitmapBuffer)
        mHandler.sendMessageDelayed(message, 500)
    }

}