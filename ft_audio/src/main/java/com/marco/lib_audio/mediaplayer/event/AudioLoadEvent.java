package com.marco.lib_audio.mediaplayer.event;

import com.marco.lib_model.ft_audio.model.Track;

public class AudioLoadEvent {
    public Track track;

    public AudioLoadEvent(Track track) {
        this.track = track;
    }
}
