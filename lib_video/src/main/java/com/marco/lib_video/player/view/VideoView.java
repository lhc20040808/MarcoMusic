package com.marco.lib_video.player.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.marco.lib_video.R;

import java.io.IOException;

public class VideoView extends RelativeLayout implements View.OnClickListener, TextureView.SurfaceTextureListener
        , MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    public enum State {
        IDLE, PLAYING, PAUSED, ERROR
    }

    private static final String TAG = VideoView.class.getSimpleName();
    private static final int RETRY_MAX_COUNT = 3;

    private int mVideoWidth;
    private int mVideoHeight;
    private RelativeLayout mPlayerView;
    private TextureView mVideoView;
    private Button mMiniPlayBtn;
    private ImageView mFullBtn;
    private ImageView mLoadingBar;
    private MediaPlayer mediaPlayer;

    /**
     * 当前播放状态
     */
    private State state;
    /**
     * 是否静音
     */
    private boolean isMute;
    /**
     * 短暂暂停为false，比如接电话
     * 仅当用户点击暂停或者视频播放完毕才为true
     */
    private boolean isRealPause;
    private boolean isComplete;

    private VideoPlayerListener listener;
    private ScreenEventReceiver screenEventReceiver;

    private String url;
    private int mCurrentCount;

    public VideoView(Context context) {
        this(context, null);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidthAndHeight();
        initViews();
        initSmallMode();
        registerBroadcastReceiver();
    }

    /**
     * 获取屏幕宽度，计算出视频高度，宽高比为16:9
     */
    private void initWidthAndHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mVideoWidth = dm.widthPixels;//视频宽度等于屏幕宽度
        mVideoHeight = (int) (mVideoWidth * 9f / 16);
    }

    private void initViews() {
        mPlayerView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.video_player_layout, this);
        mMiniPlayBtn = mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        mFullBtn = mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        mLoadingBar = mPlayerView.findViewById(R.id.loading_bar);
        mVideoView = mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setSurfaceTextureListener(this);
    }

    private void initSmallMode() {
        LayoutParams params = new LayoutParams(mVideoWidth, mVideoHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(params);
    }

    private void registerBroadcastReceiver() {
        if (screenEventReceiver == null) {
            screenEventReceiver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(screenEventReceiver, filter);
        }
    }

    private void unregisterBroadcastReceiver() {
        if (screenEventReceiver != null) {
            getContext().unregisterReceiver(screenEventReceiver);
        }
    }

    private void setCurState(State state) {
        this.state = state;
    }

    private void showPlayOrPauseView(boolean isPlay) {
        mFullBtn.setVisibility(isPlay ? View.VISIBLE : View.GONE);
        mMiniPlayBtn.setVisibility(isPlay ? View.GONE : View.VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
    }

    private void showLoadingView() {
        mFullBtn.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingBar.getBackground();
        anim.start();
        mMiniPlayBtn.setVisibility(View.GONE);
    }

    private void showPlayView() {
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
    }

    private void checkMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    public void setVideoPlayerListener(VideoPlayerListener listener) {
        this.listener = listener;
    }

    /**
     * 设置静音状态
     */
    public void mute(boolean isMute) {
        this.isMute = isMute;
        if (mediaPlayer != null) {
            float volume = isMute ? 0 : 1;
            mediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 是否正在播放
     */
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public boolean isRealPause() {
        return isRealPause;
    }

    public void setRealPause(boolean realPause) {
        isRealPause = realPause;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public void load() {
        if (state != State.IDLE) {
            return;
        }

        setCurState(State.PLAYING);
        showLoadingView();
        try {
            checkMediaPlayer();
            mute(true);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        if (state != State.PAUSED) {
            return;
        }

        if (!isPlaying()) {
            setCurState(State.PLAYING);
            setRealPause(false);
            setComplete(false);
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.start();
            showPlayOrPauseView(true);
        } else {
            showPlayOrPauseView(false);
        }
    }

    public void pause() {
        pause(true);
    }

    public void pause(boolean isShowPlay) {
        if (state != State.PLAYING) {
            return;
        }
        setCurState(State.PAUSED);
        if (isPlaying()) {
            mediaPlayer.pause();
        }

        if (isShowPlay) {
            showPlayOrPauseView(false);
        }
    }

    public void stop() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        setCurState(State.IDLE);
        if (mCurrentCount < RETRY_MAX_COUNT) { //满足重新加载的条件
            mCurrentCount += 1;
            load();
        } else {
            showPlayOrPauseView(false); //显示暂停状态
        }
    }

    public void seekAndResume(int position) {
        if (mediaPlayer != null) {
            showPlayOrPauseView(true);
            setCurState(State.PLAYING);
            setRealPause(false);
            setComplete(false);
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        }
    }

    public void seekAndPause(int position) {
        if (state != State.PLAYING) {
            return;
        }
        showPlayOrPauseView(false);
        setCurState(State.PAUSED);
        if (isPlaying()) {
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.pause();
                }
            });
        }
    }

    /**
     * 回到播放最初状态
     */
    public void playBack() {
        setCurState(State.PAUSED);
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
        this.showPlayOrPauseView(false);
    }

    public void destroy() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        setCurState(State.IDLE);
        mCurrentCount = 0;
        setComplete(false);
        setRealPause(false);
        unregisterBroadcastReceiver();
        showPlayOrPauseView(false); //除了播放和loading外其余任何状态都显示pause
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCurPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            if (isRealPause() || isComplete()) {
                //主动暂停或者播放结束，不恢复视频播放
                Log.d(TAG, "the video is real pause or complete,not resume");
                pause();
            } else {
                //被动暂停，恢复播放
                Log.d(TAG, "the video is visible now, resume the video");
                resume();
            }
        } else {
            Log.d(TAG, "the video is not visible now, resume the video");
            pause();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Surface videoSurface = new Surface(surface);
        checkMediaPlayer();
        mediaPlayer.setSurface(videoSurface);
        load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onClick(View v) {
        if (v == mMiniPlayBtn) {
            //播放按钮
            if (state == State.PAUSED) {
                resume();
            } else {
                load();
            }
        } else if (v == mFullBtn) {
            if (listener != null) {
                listener.onClickFullScreenBtn();
            }
        } else if (v == mVideoView) {
            if (listener != null) {
                listener.onClickVideo();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playBack();
        setRealPause(true);
        setComplete(true);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setCurState(State.ERROR);
        if (mCurrentCount >= RETRY_MAX_COUNT) {
            showPlayOrPauseView(false);
            if (listener != null) {
                listener.onVideoLoadFailed();
            }
        }
        stop();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        showPlayView();
        if (mediaPlayer != null) {
            mCurrentCount = 0;
            resume();
            if (listener != null) {
                listener.onVideoLoadSuccess();
            }
        }
    }

    /**
     * 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Intent.ACTION_USER_PRESENT:
                        //处理亮屏事件
                        if (isRealPause() || isComplete()) {
                            pause();
                        } else {
                            resume();
                        }
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        if (state == State.PLAYING) {
                            pause();
                        }
                        break;
                }

            }
        }
    }

    public interface VideoPlayerListener {

        void onClickFullScreenBtn();

        void onClickVideo();

        void onClickBackBtn();

        void onClickPlay();

        void onVideoLoadSuccess();

        void onVideoLoadFailed();

        void onVideoLoadComplete();
    }
}
