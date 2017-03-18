package com.guoyi.circle.ui.videolist.model;

import android.media.MediaPlayer;

import com.guoyi.circle.ui.videolist.widget.TextureVideoView;


/**
 * @author Wayne
 */
public interface VideoLoadMvpView {

    TextureVideoView getVideoView();

    void videoBeginning();

    void videoStopped();

    void videoPrepared(MediaPlayer player);

    void videoResourceReady(String videoPath);
}
