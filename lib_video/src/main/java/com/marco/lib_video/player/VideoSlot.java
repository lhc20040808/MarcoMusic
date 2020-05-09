package com.marco.lib_video.player;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.marco.lib_video.player.view.VideoFullDialog;
import com.marco.lib_video.player.view.VideoView;
import com.marco.lib_video.utils.Utils;

/**
 * 视频的业务逻辑层
 * 负责处理全屏和小屏的切换
 */
class VideoSlot implements VideoView.VideoPlayerListener {

    private Context context;
    private VideoView videoView;
    private ViewGroup parentView;
    private String videoUrl;
    private VideoSlotListener videoSlotListener;

    public VideoSlot(String videoUrl, VideoSlotListener videoSlotListener) {
        this.videoUrl = videoUrl;
        this.videoSlotListener = videoSlotListener;
        this.parentView = videoSlotListener.getParent();
        this.context = parentView.getContext();
        initVideoView();
    }

    private void initVideoView() {
        videoView = new VideoView(context);
        if (videoUrl != null) {
            videoView.setUrl(videoUrl);
            videoView.setVideoPlayerListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(context);
        paddingView.setBackgroundColor(context.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(videoView.getLayoutParams());
        parentView.addView(paddingView);
        parentView.addView(videoView);
    }

    @Override
    public void onClickFullScreenBtn() {
        int startY = Utils.getLocationY(videoView);
        parentView.removeView(videoView);
        VideoFullDialog dialog = new VideoFullDialog(context, videoView, videoView.getCurPosition(), startY);
        dialog.setListener(new VideoFullDialog.VideoFullListener() {
            @Override
            public void onClose(int position) {
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                backToSmallMode(0);
            }
        });
        dialog.show();
    }

    private void backToSmallMode(int position) {
        if (videoView.getParent() == null) {
            parentView.addView(videoView);
        }
        videoView.setTranslationY(0);
        videoView.isShowFullBtn(true);
        videoView.mute(true);
        videoView.setVideoPlayerListener(this);
        videoView.seekAndResume(position);
        //TODO 恢复音乐播放
    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onVideoLoadSuccess() {
        if (videoSlotListener != null) {
            videoSlotListener.onVideoLoadSuccess();
        }
    }

    @Override
    public void onVideoLoadFailed() {
        if (videoSlotListener != null) {
            videoSlotListener.onVideoLoadFailed();
        }
    }

    @Override
    public void onVideoLoadComplete() {
        if (videoSlotListener != null) {
            videoSlotListener.onVideoLoadComplete();
        }
    }

    public interface VideoSlotListener {
        void onVideoLoadFailed();

        void onVideoLoadComplete();

        void onVideoLoadSuccess();

        ViewGroup getParent();
    }
}
