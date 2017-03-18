package com.guoyi.circle.activity;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.guoyi.circle.R;

public class VideoPreviewActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener
        , OnClickListener, OnCompletionListener {

    private static final String TAG = "VideoPreviewActivity";
    private String path;
    private TextureView surfaceView;

    private MediaPlayer mediaPlayer;
    private ImageView imagePlay;

    private Toolbar toolbar;

    public static final String VIDEO_PATH = "path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg_preview);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        surfaceView = (TextureView) findViewById(R.id.preview_video);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        RelativeLayout preview_video_parent = (RelativeLayout) findViewById(R.id.preview_video_parent);
        LayoutParams layoutParams = (LayoutParams) preview_video_parent
                .getLayoutParams();
        layoutParams.width = displaymetrics.widthPixels;
        layoutParams.height = displaymetrics.widthPixels;
        preview_video_parent.setLayoutParams(layoutParams);

        surfaceView.setSurfaceTextureListener(this);
        surfaceView.setOnClickListener(this);
        path = getIntent().getStringExtra(VIDEO_PATH);
        imagePlay = (ImageView) findViewById(R.id.previre_play);
        imagePlay.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("查看视频");
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

    }

    @Override
    protected void onStop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imagePlay.setVisibility(View.GONE);
        }
        super.onStop();
    }

    private void prepare(Surface surface) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
            mediaPlayer.setDataSource(path);
            // 把视频画面输出到Surface
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
        } catch (Exception e) {
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
                                          int arg2) {
        prepare(new Surface(arg0));
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
                                            int arg2) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.previre_play) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            imagePlay.setVisibility(View.GONE);
        } else if (id == R.id.preview_video) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                imagePlay.setVisibility(View.VISIBLE);
            }
        }
    }


    private void stop() {
        mediaPlayer.stop();
    }


    @Override
    public void onBackPressed() {
        stop();
        super.onBackPressed();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        imagePlay.setVisibility(View.VISIBLE);
    }
}
