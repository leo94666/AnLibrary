package com.top.sample;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.top.androidx.superview.TextureVideoView;
import com.top.arch.base.BaseActivity;
import com.top.arch.base.BasePureActivity;
import com.top.arch.util.ImageUtils;
import com.top.arch.util.ScreenUtils;
import com.top.arch.utils.ScreenShotUtils;
import com.top.screenshot.ScreenShotAdvance;


public class ScreenShotActivity extends AppCompatActivity {

    RelativeLayout ll_parent;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_screenshot);

        TextureVideoView videoView = findViewById(R.id.texture_video);
        ll_parent= findViewById(R.id.ll_parent);
        videoView.setVideoPath("https://www.zhangxinxu.com/study/media/cat2.mp4");
        videoView.start();
        videoView.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        });
    }

    public void shot(View view) {
        Bitmap bitmap = ScreenShotAdvance.getInstance().screenShot(ll_parent);
        Log.i("","===================");
    }
}
