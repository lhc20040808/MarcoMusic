package com.marco.lib_audio.mediaplayer.event;

import com.marco.lib_audio.mediaplayer.core.CustomMediaPlayer;

public class AudioProgressEvent {

    public CustomMediaPlayer.Status status;
    public int duration;
    public int progress;

    public AudioProgressEvent(CustomMediaPlayer.Status status, int progress, int duration) {
        this.status = status;
        this.progress = progress;
        this.duration = duration;
    }
}
