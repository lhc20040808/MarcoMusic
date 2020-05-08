package com.marco.lib_audio.mediaplayer.event;

import com.marco.lib_audio.mediaplayer.core.CustomMediaPlayer;

public class AudioProgressEvent {

    public CustomMediaPlayer.Status status;
    public int duration;
    public int progress;

    public AudioProgressEvent(CustomMediaPlayer.Status status, int duration, int progress) {
        this.status = status;
        this.duration = duration;
        this.progress = progress;
    }
}
