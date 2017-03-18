package com.guoyi.circle.adapter.holder;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.guoyi.circle.R;
import com.guoyi.circle.adapter.OnRecyclerViewListener;
import com.guoyi.circle.been.PostBean;
import com.guoyi.circle.mvp.presenter.CirclePresenter;
import com.guoyi.circle.ui.videolist.VideoListGlideModule;
import com.guoyi.circle.ui.videolist.model.VideoLoadMvpView;
import com.guoyi.circle.ui.videolist.target.VideoLoadTarget;
import com.guoyi.circle.ui.videolist.target.VideoProgressTarget;
import com.guoyi.circle.ui.videolist.widget.TextureVideoView;
import com.guoyi.circle.utils.StringUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Credit on 2017/3/10.
 */

public class VideoViewHolder extends BaseCircleHolder implements VideoLoadMvpView, ViewPropertyAnimatorListener {

    private static final String TAG = "VideoViewHolder";
    private FrameLayout mVideoBody;
    private TextureVideoView mVideoPlayer;
    private ImageView mIvVideoFrame;
    private CircularProgressBar mVideoProgress;
    private ImageView mIvVideoPlay;


    private final VideoProgressTarget progressTarget;
    private final VideoLoadTarget videoTarget;


    public VideoViewHolder(Context context, ViewGroup root, int layoutRes, int viewType, CirclePresenter circlePresenter, OnRecyclerViewListener l) {
        super(context, root, layoutRes, viewType, circlePresenter, l);

        mVideoPlayer.setAlpha(0);
        videoTarget = new VideoLoadTarget(this);
        progressTarget = new VideoProgressTarget(videoTarget, mVideoProgress);
    }


    @Override
    public void initViewStub() {
        if (mViewStub == null) {
            throw new IllegalArgumentException("viewStub is null...");
        }
        mViewStub.setLayoutResource(R.layout.layout_video);
        View subViw = mViewStub.inflate();
        mVideoBody = (FrameLayout) subViw.findViewById(R.id.videoBody);
        if (mVideoBody != null) {
            mVideoPlayer = (TextureVideoView) subViw.findViewById(R.id.video_player);
            mIvVideoFrame = (ImageView) subViw.findViewById(R.id.iv_video_frame);
            mVideoProgress = (CircularProgressBar) subViw.findViewById(R.id.video_progress);
            mIvVideoPlay = (ImageView) subViw.findViewById(R.id.iv_video_play);
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();
        mIvVideoPlay.setOnClickListener(this);
        mVideoPlayer.setOnClickListener(this);
        mVideoPlayer.setMediaPlayerCallback(new TextureVideoView.MediaPlayerCallback() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                Log.e(TAG, "在准备中..onPrepared: ");
            }

            @Override
            public void onStoped(MediaPlayer mp) {
                Log.e(TAG, "在完成中..onStoped: ");
                mdata.setVideoPaly(false);
                mVideoPlayer.stop();
                videoStopped();
            }

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.e(TAG, "在完成中..onCompletion: ");
            }

            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                Log.e(TAG, "在缓冲更新中..onBufferingUpdate: ");
            }

            @Override
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                Log.e(TAG, "在视频大小改变中..onVideoSizeChanged: ");
            }

            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                Log.e(TAG, "onInfo..width:" + i + "  height:" + i1);
                return false;
            }

            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
    }


    private void reset() {
        mVideoPlayer.stop();
        videoStopped();
    }

    @Override
    public void bindData(PostBean circleItem, int postion) {
        super.bindData(circleItem, postion);
        if (mdata != null) {
            reset();
            // load video cover photo
            Glide.with(itemView.getContext())
                    .load(StringUtils.getImageDefaultURL(mdata.getVideoImgUrl()))
                    .placeholder(new ColorDrawable(0xffdcdcdc))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mIvVideoFrame);
        }
    }

    private void cancelAlphaAnimate(View v) {
        ViewCompat.animate(v).cancel();
    }

    private void startAlphaAnimate(View v) {
        ViewCompat.animate(v).setListener(this).alpha(0f);
    }


    @Override
    public TextureVideoView getVideoView() {
        return mVideoPlayer;
    }

    @Override
    public void videoBeginning() {
        Log.e(TAG, "videoBeginning");
        mVideoPlayer.setAlpha(1.f);
        mIvVideoPlay.setVisibility(View.GONE);
        cancelAlphaAnimate(mIvVideoFrame);
        startAlphaAnimate(mIvVideoFrame);
    }

    @Override
    public void videoStopped() {
        Log.e(TAG, "videoStopped");
        cancelAlphaAnimate(mIvVideoFrame);
        mVideoPlayer.setAlpha(0);
        mIvVideoFrame.setAlpha(1.f);
        mIvVideoFrame.setVisibility(View.VISIBLE);
        mIvVideoPlay.setVisibility(View.VISIBLE);
        mIvVideoPlay.setImageResource(R.drawable.ic_video_play);


    }

    @Override
    public void videoPrepared(MediaPlayer player) {
        Log.e(TAG, "videoPrepared");
    }


    @Override
    public void videoResourceReady(String videoPath) {

        /**
         * 缓存视频到本地的回调
         */
        mdata.setLocalPath(videoPath);
        if (!TextUtils.isEmpty(videoPath)) {
            mVideoPlayer.setVideoPath(videoPath);
            if (!mdata.isVideoLoadSuccess()) {
                mdata.setVideoLoadSuccess(true);
                Log.e(TAG, "videoResourceReady>>>:" + videoPath + "\n state:" + mdata.isVideoPaly());
                if (mdata.isVideoPaly()) {
                    Log.e(TAG, "播放 ：" + videoPath);
                    mdata.setNODE_PALY(false);
                    mIvVideoPlay.setVisibility(View.GONE);
                    mVideoPlayer.start();
                }

            }
        } else {
            deactivate(null, 0);
        }
    }


    @Override
    public void setActive(View view, int i) {
       /* videoState = STATE_ACTIVED;
        if (videoLocalPath != null) {
            mVideoPlayer.setVideoPath(videoLocalPath);
            mVideoPlayer.start();
        }*/
        if (mdata != null && mdata.isVideoLoadSuccess() && !TextUtils.isEmpty(mdata.getLocalPath())) {
            if (mdata.isNODE_PALY()) {
                Log.e(TAG, "setActive: 播放 ");
                mdata.setVideoPaly(true);
                mdata.setNODE_PALY(false);
                mIvVideoPlay.setVisibility(View.GONE);
                mVideoPlayer.setVideoPath(mdata.getLocalPath());
                mVideoPlayer.start();
            }
        } else {
            Log.e(TAG, "setActive: 加载失败 ");
        }
    }

    @Override
    public void deactivate(View view, int i) {
        if (mdata != null) {
            mdata.setVideoPaly(false);
        }
        Log.e(TAG, "deactivate: ");
        mVideoPlayer.stop();
        videoStopped();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_video_play) {
            if (mdata != null) {
                if (!mdata.isVideoLoadSuccess() || !mdata.isVideoPaly()) {
                    mdata.setVideoPaly(true);
                    /**
                     * 视频正在加载中..
                     */
                    if (TextUtils.isEmpty(mdata.getLocalPath())) {
                        String defaultURL = StringUtils.getImageDefaultURL(mdata.getVideoUrl());
                        Log.e(TAG, "onClick: 视频正在加载中>>>>" + defaultURL);
                        // load video file
                        progressTarget.setModel(defaultURL);
                        Glide.with(mContext)
                                .using(VideoListGlideModule.getOkHttpUrlLoader(), InputStream.class)
                                .load(new GlideUrl(defaultURL))
                                .as(File.class)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(progressTarget);
                    } else {
                        Log.e(TAG, "onClick: 播放 。。。");
                        mdata.setNODE_PALY(false);
                        mIvVideoPlay.setVisibility(View.GONE);
                        mVideoPlayer.setVideoPath(mdata.getLocalPath());
                        mVideoPlayer.start();
                    }
                } else {
                    Log.e(TAG, "onClick: 暂停 。。。");
                    mdata.setVideoPaly(false);
                    if (btnRunable != null) {
                        mIvVideoPlay.removeCallbacks(btnRunable);
                    }
                    mVideoPlayer.stop();
                    videoStopped();
                }
            }
        } else if (v.getId() == R.id.video_player && mdata != null && mdata.isVideoPaly()) {
            if (mIvVideoPlay.getVisibility() == View.GONE) {
                //
                mIvVideoPlay.setImageResource(android.R.drawable.ic_media_pause);
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(300);

                btnRunable = new PalyBtnRunable();
                mIvVideoPlay.postDelayed(btnRunable, 3000);

                mIvVideoPlay.setVisibility(View.VISIBLE);
                mIvVideoPlay.startAnimation(animation);
            } else {
                mIvVideoPlay.setVisibility(View.GONE);
            }
        }
        super.onClick(v);
    }

    private PalyBtnRunable btnRunable;

    private class PalyBtnRunable implements Runnable {
        @Override
        public void run() {
            mIvVideoPlay.setVisibility(View.GONE);
        }
    }


    @Override
    public void onAnimationStart(View view) {

    }

    @Override
    public void onAnimationEnd(View view) {
        view.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationCancel(View view) {

    }

}
