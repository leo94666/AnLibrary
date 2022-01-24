package com.top.ijkplayer.activity;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.top.ijkplayer.CameraHelper;
import com.top.ijkplayer.R;
import com.top.ijkplayer.VideoCodec;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener, Camera.PreviewCallback {


    private CameraHelper mCameraHelper;
    private VideoCodec videoCodec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mCameraHelper = new CameraHelper(1920, 1080);
        mCameraHelper.setPreviewCallback(this);
        TextureView textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                mCameraHelper.startPreview(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                mCameraHelper.stopPreview();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

            }
        });
        videoCodec = new VideoCodec();
        findViewById(R.id.btn_record).setOnClickListener(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        videoCodec.queueEncode(data);
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        if (videoCodec.isRecording()) {
            button.setText("开始录制");
            videoCodec.stopRecording();
        } else {
            button.setText("停止录制");
            videoCodec.startRecoding("/sdcard/a.mp4", mCameraHelper.getWidth(),
                    mCameraHelper.getHeight(), 90);
        }
    }


}