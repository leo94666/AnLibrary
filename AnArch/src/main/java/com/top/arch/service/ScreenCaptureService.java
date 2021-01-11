package com.top.arch.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.top.arch.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class ScreenCaptureService extends Service {

    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;

    private boolean running;
    private int width = 720;
    private int height = 1080;
    private int dpi;
    private String mSavePath = "";
    private String mSaveDirPath = "";

    public static String SAVE_DIR_PATH = "Save_Dir_Path";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    private void initScreenCapture() {
        if (mediaProjection == null || running) {
            return;
        }

        try {
            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
            }

            mSavePath = mSaveDirPath + System.currentTimeMillis() + ".mp4";

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setVideoSize(width, height);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            mediaRecorder.setVideoEncodingBitRate(8 * 1024 * 1024);
            mediaRecorder.setVideoFrameRate(35);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaRecorder.setOutputFile(new File(mSavePath));
            } else {
                mediaRecorder.setOutputFile(mSavePath);
            }
            mediaRecorder.prepare();
            mediaRecorder.setOnErrorListener((mr, what, extra) -> {

            });

        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
