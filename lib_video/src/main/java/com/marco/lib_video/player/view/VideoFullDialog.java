package com.marco.lib_video.player.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.marco.lib_video.R;
import com.marco.lib_video.utils.Utils;

public class VideoFullDialog extends Dialog implements VideoView.VideoPlayerListener {

    private VideoFullListener listener;
    private VideoView videoView;
    private int position;
    private RelativeLayout contentView;
    private int startY;
    private int deltaY;
    private boolean hasSeek;

    public VideoFullDialog(@NonNull Context context, VideoView videoView, int position) {
        super(context, R.style.dialog_full_screen);
        this.videoView = videoView;
        this.position = position;
        this.startY = -1;
        this.deltaY = -1;
        this.hasSeek = false;
    }

    public VideoFullDialog(@NonNull Context context, VideoView videoView, int position, int startY) {
        super(context, R.style.dialog_full_screen);
        this.videoView = videoView;
        this.position = position;
        this.startY = startY;
        this.deltaY = -1;
        this.hasSeek = false;
    }

    public void setListener(VideoFullListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_video_layout);
        contentView = findViewById(R.id.root_view);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideo();
            }
        });
        contentView.setVisibility(View.INVISIBLE);
        videoView.setVideoPlayerListener(this);
        videoView.mute(false);
        contentView.addView(videoView);
        contentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                contentView.getViewTreeObserver().removeOnPreDrawListener(this);
                prepareAnim();
                runEnterAnim();
                return true;
            }
        });
    }

    /**
     * 计算移动距离，设置videoView位置
     */
    private void prepareAnim() {
        int endY = Utils.getLocationY(videoView);
        if (startY != -1) {
            deltaY = startY - endY;
            videoView.setTranslationY(deltaY);
        }
    }

    /**
     * VideoView入场动画
     */
    private void runEnterAnim() {
        if (deltaY != -1) {
            videoView.animate()
                    .setDuration(200)
                    .setInterpolator(new LinearInterpolator())
                    .translationY(0)
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            contentView.setVisibility(View.VISIBLE);
                        }
                    }).start();
        }
    }

    /**
     * VideoView出场动画
     */
    private void runExitAnim() {
        if (deltaY != -1) {
            videoView.animate()
                    .setDuration(200)
                    .setInterpolator(new LinearInterpolator())
                    .translationY(deltaY)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                            if (listener != null) {
                                listener.onClose(position);
                            }
                        }
                    }).start();
        }

    }

    @Override
    public void onBackPressed() {
        onClickBackBtn();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        videoView.isShowFullBtn(false);
        if (hasFocus) {
            //为了适配某些手机不执行seekandresume中的播放方法
            if (hasSeek) {
                videoView.resume();
            } else {
                videoView.seekAndResume(position);
            }
        } else {
            position = videoView.getCurPosition();
            videoView.pause(false);
        }
    }

    @Override
    public void onClickFullScreenBtn() {

    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {
        runExitAnim();
    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onVideoLoadSuccess() {
        if (videoView != null) {
            videoView.resume();
        }
    }

    @Override
    public void onVideoLoadFailed() {

    }

    @Override
    public void onVideoLoadComplete() {
        dismiss();
        if (listener != null) {
            listener.playComplete();
        }
    }

    @Override
    public void dismiss() {
        contentView.removeView(videoView);
        super.dismiss();
    }

    public interface VideoFullListener {
        void onClose(int position);

        void playComplete();
    }
}
