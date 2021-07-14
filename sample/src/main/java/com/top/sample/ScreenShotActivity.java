package com.top.sample;

import android.graphics.Bitmap;
import android.os.IBinder;
import android.view.View;

import com.top.androidx.superview.TextureVideoView;
import com.top.arch.base.BasePureActivity;
import com.top.arch.util.ImageUtils;

public class ScreenShotActivity extends BasePureActivity {
    @Override
    public int getLayout() {
        return R.layout.activity_layout_screenshot;
    }

    @Override
    public void init(View root) {

        TextureVideoView videoView = root.findViewById(R.id.video_t);

        videoView.setVideoPath("https://www.zhangxinxu.com/study/media/cat2.mp4");
        videoView.start();
        videoView.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        });
    }

    @Override
    public void hideKeyboard(IBinder token) {

    }

    @Override
    public void setScreenSensor(boolean isAuto) {

    }

    @Override
    public void onScreenShot(Bitmap bitmap) {
        ImageUtils.save(bitmap,  "/sdcard/screenShot.png", Bitmap.CompressFormat.PNG);
    }


    public void shot(View view) {

        takeScreenShot();
    }
}
