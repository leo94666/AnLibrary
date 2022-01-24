package com.top.ijkplayer;


import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.util.List;


public class CameraHelper implements Camera.PreviewCallback {

    private static final String TAG = "CameraHelper";
    private int width;
    private int height;
    private int mCameraId;
    private Camera mCamera;
    private byte[] buffer;
    private Camera.PreviewCallback mPreviewCallback;
    private SurfaceTexture mSurfaceTexture;

    public CameraHelper(int width, int height) {
        this.width = width;
        this.height = height;
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        stopPreview();
        startPreview(mSurfaceTexture);
    }

    public int getCameraId() {
        return mCameraId;
    }

    public void stopPreview() {
        if (mCamera != null) {
            //预览数据回调接口
            mCamera.setPreviewCallback(null);
            //停止预览
            mCamera.stopPreview();
            //释放摄像头
            mCamera.release();
            mCamera = null;
        }
    }

    public void startPreview(SurfaceTexture surfaceTexture) {
        stopPreview();
        try {
            mSurfaceTexture = surfaceTexture;
            //获得camera对象
            mCamera = Camera.open(mCameraId);
            //配置camera的属性
            Camera.Parameters parameters = mCamera.getParameters();
            //设置预览数据格式为nv21
            parameters.setPreviewFormat(ImageFormat.NV21);
            boolean isSupportSize = false;
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                if (supportedPreviewSize.width == width && supportedPreviewSize.height == height) {
                    isSupportSize = true;
                    break;
                }
            }
            if (!isSupportSize) {
                Camera.Size size = supportedPreviewSizes.get(0);
                width = size.width;
                height = size.height;
            }
            //这是摄像头宽、高
            parameters.setPreviewSize(width, height);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCamera.setDisplayOrientation(90);
            // 设置摄像头 图像传感器的角度、方向
            mCamera.setParameters(parameters);
            buffer = new byte[width * height * 3 / 2];
            //数据缓存区
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(this);
            //设置预览画面
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // data数据依然是倒的
        if (null != mPreviewCallback) {
            mPreviewCallback.onPreviewFrame(data, camera);
        }
        camera.addCallbackBuffer(buffer);
    }





}
