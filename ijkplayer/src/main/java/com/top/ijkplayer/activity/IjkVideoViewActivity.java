package com.top.ijkplayer.activity;

import android.os.Bundle;
import android.widget.TableLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.top.ijkplayer.R;
import com.top.ijkplayer.widget.media.AndroidMediaController;
import com.top.ijkplayer.widget.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkVideoViewActivity extends AppCompatActivity {


    private IjkVideoView mVideoView;
    private TableLayout mHudView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_video_view);
        IjkMediaPlayer.loadLibrariesOnce(null);
        AndroidMediaController mMediaController = new AndroidMediaController(this, false);

        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        mHudView = (TableLayout) findViewById(R.id.hud_view);
        mVideoView = (IjkVideoView) findViewById(R.id.ijk_video_view);
        mVideoView.setMediaController(mMediaController);

        mVideoView.setHudView(mHudView);


        mVideoView.setVideoPath("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov");
        //mVideoView.setVideoPath("rtmp://media3.scctv.net/live/scctv_800");
        //mVideoView.setVideoPath("https://ebs-1306092442.cos.ap-nanjing.myqcloud.com/%E6%B5%8B%E8%AF%95%E7%97%85%E4%BE%8B/%E7%97%85%E4%BE%8B%E4%BD%A0%E5%A5%BD/tmp5BC4.mp4");
        mVideoView.start();
    }
}
