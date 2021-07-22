package com.top.arch.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.top.arch.app.AppManager;

import java.nio.ByteBuffer;



public abstract class BasePureActivity extends AppCompatActivity implements BaseInterface {

    private static final String TAG = "BaseActivity";

    public abstract int getLayout();

    public abstract void init(View root);

    public static final int EVENT_SCREENSHOT = 22;//截图事件
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private Image image;

    public abstract void onScreenShot(Bitmap bitmap);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(getLayout());
        init(this.getWindow().getDecorView());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }


    @Override
    public void setFullScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void exitFullScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void showLogToast(String msg) {

    }

    @Override
    public void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void showBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            //低版本sdk
            View v1 = getWindow().getDecorView();
            v1.setSystemUiVisibility(View.VISIBLE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult...requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (requestCode == EVENT_SCREENSHOT) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.e(TAG, "captureScreen...");
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            Log.e(TAG, "displayMetrics width=" + width + ", height=" + height);
            ImageReader mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
            VirtualDisplay virtualDisplay = null;

            try {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                virtualDisplay = mediaProjection.createVirtualDisplay("screen-mirror", width, height,
                        displayMetrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
            }catch (Exception e){
                Log.e(TAG, "截图出现异常：" + e.toString());
                if (virtualDisplay != null) {
                    virtualDisplay.release();
                }
                return;
            }

            new Handler().postDelayed(() -> {
                try {
                    image = mImageReader.acquireLatestImage();
                    if (image != null) {
                        final Image.Plane[] planes = image.getPlanes();
                        final ByteBuffer buffer = planes[0].getBuffer();
                        int width1 = image.getWidth();
                        int height1 = image.getHeight();
                        Log.e(TAG, "image width=" + width1 + ", height=" + height1);
                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * width1;
                        Bitmap bitmap = Bitmap.createBitmap(width1 + rowPadding / pixelStride, height1, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(buffer);
                        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
                        if (bitmap != null) {
                            Log.e(TAG, "屏幕截图成功!");
                            //BitmapUtil.saveBitmap(bitmap, "/sdcard/screenShot.png");
                            onScreenShot(bitmap);
                        }
                        bitmap.recycle();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "截图出现异常：" + e.toString());
                    onScreenShot(null);
                } finally {
                    if (image != null) {
                        image.close();
                    }
                    if (mImageReader != null) {
                        mImageReader.close();
                    }
                    //必须代码，否则出现BufferQueueProducer: [ImageReader] dequeueBuffer: BufferQueue has been abandoned
                    mImageReader.setOnImageAvailableListener(null, null);
                    if (mediaProjection!=null){
                        mediaProjection.stop();
                    }
                }
            }, 120);
        }
    }

    @RequiresApi(api = 28)
    public void takeScreenShot() {
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), EVENT_SCREENSHOT);
    }

}
