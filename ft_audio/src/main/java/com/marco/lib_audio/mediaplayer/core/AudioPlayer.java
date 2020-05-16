package com.marco.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.marco.lib_audio.app.AudioHelper;
import com.marco.lib_audio.mediaplayer.event.AudioCompleteEvent;
import com.marco.lib_audio.mediaplayer.event.AudioErrorEvent;
import com.marco.lib_audio.mediaplayer.event.AudioLoadEvent;
import com.marco.lib_audio.mediaplayer.event.AudioPauseEvent;
import com.marco.lib_audio.mediaplayer.event.AudioProgressEvent;
import com.marco.lib_audio.mediaplayer.event.AudioReleaseEvent;
import com.marco.lib_audio.mediaplayer.event.AudioStartEvent;
import com.marco.lib_model.ft_audio.model.Track;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class AudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener
        , MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioFocusManager.AudioFocusListener {

    private static final String TAG = AudioPlayer.class.getSimpleName();
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INTERVAL = 100;
    //因为失去音频焦点而暂停
    private boolean isPauseByFocusLoss;
    private AudioFocusManager audioFocusManager;
    private CustomMediaPlayer mediaPlayer;
    //利用wifiLock增强后台保活能力
    private WifiManager.WifiLock wifiLock;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    if (getStatus() == CustomMediaPlayer.Status.STARTED
                            || getStatus() == CustomMediaPlayer.Status.PAUSED) {
                        EventBus.getDefault().post(new AudioProgressEvent(getStatus(), getCurPosition(), getDuration()));
                        sendEmptyMessageDelayed(TIME_MSG, TIME_INTERVAL);
                    }
                    break;
            }
        }
    };

    public AudioPlayer() {
        //初始化MediaPlayer
        mediaPlayer = new CustomMediaPlayer();
        mediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnErrorListener(this);
        //初始化WifiLock
        wifiLock = ((WifiManager) AudioHelper.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        audioFocusManager = new AudioFocusManager(AudioHelper.getContext(), this);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //缓存进度回调
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完毕回调
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //返回true表示自行处理异常，不再回调onCompletion
        EventBus.getDefault().post(new AudioErrorEvent());
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //准备完毕
        start();
    }

    @Override
    public void audioFocusGrant() {
        //再次获得音频焦点
        setVolume(1.0f, 1.0f);
        if (isPauseByFocusLoss) {
            resume();
        }
        isPauseByFocusLoss = false;
    }

    @Override
    public void audioFocusLoss() {
        //永久失去焦点
        pause();
    }

    @Override
    public void audioFocusLossTransient() {
        //短暂性失去焦点
        pause();
        isPauseByFocusLoss = true;
    }

    @Override
    public void audioFocusLossDuck() {
        //瞬间失去焦点
        setVolume(0.5f, 0.5f);
    }

    /**
     * 暂停
     */
    public void pause() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED) {
            mediaPlayer.pause();
            //释放WifiLock
            if (wifiLock.isHeld()) {
                wifiLock.release();
            }
            //释放音频焦点
            if (audioFocusManager != null) {
                audioFocusManager.abandonAudioFocus();
            }
            //发送暂停事件
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }

    public void release() {
        if (mediaPlayer == null) {
            return;
        }
        handler.removeCallbacksAndMessages(null);
        mediaPlayer.release();
        mediaPlayer = null;
        if (audioFocusManager != null) {
            audioFocusManager.abandonAudioFocus();
        }
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
        wifiLock = null;
        audioFocusManager = null;
        //发送release事件
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    /**
     * 音频加载方法
     *
     * @param track
     */
    public void load(Track track) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(track.mUrl);
            mediaPlayer.prepareAsync();//异步加载，成功后会回调onPrepared(MediaPlayer mp)
            //对外发送load事件
            EventBus.getDefault().post(new AudioLoadEvent(track));
        } catch (IOException e) {
            e.printStackTrace();
            //对外发送error事件
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }

    /**
     * 继续播放
     */
    public void resume() {
        if (getStatus() == CustomMediaPlayer.Status.PAUSED) {
            start();
        }
    }

    public CustomMediaPlayer.Status getStatus() {
        if (mediaPlayer != null) {
            return mediaPlayer.getStatus();
        }
        return CustomMediaPlayer.Status.STOPPED;
    }

    public int getDuration() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED
                || getStatus() == CustomMediaPlayer.Status.PAUSED) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurPosition() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED
                || getStatus() == CustomMediaPlayer.Status.PAUSED) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 播放
     */
    private void start() {
        if (!audioFocusManager.requestAudioFocus()) {
            Log.d(TAG, "获取音频焦点失败");
            return;
        }

        mediaPlayer.start();
        wifiLock.acquire();
        //更新进度
        handler.sendEmptyMessage(TIME_MSG);
        //对外发送start事件
        EventBus.getDefault().post(new AudioStartEvent());
    }

    private void setVolume(float leftVol, float rightVol) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(leftVol, rightVol);
        }
    }
}
