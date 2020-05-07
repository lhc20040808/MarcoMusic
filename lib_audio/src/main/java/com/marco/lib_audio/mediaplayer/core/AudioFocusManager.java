package com.marco.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;

public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {

    private AudioManager audioManager;
    private AudioFocusListener audioFocusListener;

    public AudioFocusManager(Context context, AudioFocusListener audioFocusListener) {
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.audioFocusListener = audioFocusListener;
    }

    public boolean requestAudioFocus() {
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            // 重新获得焦点
            case AudioManager.AUDIOFOCUS_GAIN:
                if (audioFocusListener != null) audioFocusListener.audioFocusGrant();
                break;
            // 永久丢失焦点，如被其他播放器抢占
            case AudioManager.AUDIOFOCUS_LOSS:
                if (audioFocusListener != null) audioFocusListener.audioFocusLoss();
                break;
            // 短暂丢失焦点，如来电
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (audioFocusListener != null) audioFocusListener.audioFocusLossTransient();
                break;
            // 瞬间丢失焦点，如通知
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (audioFocusListener != null) audioFocusListener.audioFocusLossDuck();
                break;
        }
    }

    /**
     * 音频焦点改变,接口回调，
     */
    public interface AudioFocusListener {
        //获得焦点回调处理
        void audioFocusGrant();

        //永久失去焦点回调处理
        void audioFocusLoss();

        //短暂失去焦点回调处理
        void audioFocusLossTransient();

        //瞬间失去焦点回调
        void audioFocusLossDuck();
    }
}
