package com.top.sample;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

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

        ll_parent= findViewById(R.id.ll_parent);

        TextureVideoView videoView = findViewById(R.id.texture_video);
        videoView.setVideoPath("https://media.w3.org/2010/05/sintel/trailer.mp4");
        videoView.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        });


        VideoView viewVideo = findViewById(R.id.video_view);
        viewVideo.setVideoPath("https://www.w3school.com.cn/example/html5/mov_bbb.mp4");
        viewVideo.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        });


        TextureVideoView rl_texture_viewVideo = findViewById(R.id.rl_texture_video);
        rl_texture_viewVideo.setVideoPath("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        rl_texture_viewVideo.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        });

        VideoView rl_video_viewVideo = findViewById(R.id.rl_video_view);
        rl_video_viewVideo.setVideoPath("https://www.zhangxinxu.com/study/media/cat2.mp4");
        rl_video_viewVideo.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        });

    }

    public void shot(View view) {
        Bitmap bitmap = ScreenShotAdvance.getInstance().screenShot(ll_parent);
        Log.i("","===================");
    }
}
