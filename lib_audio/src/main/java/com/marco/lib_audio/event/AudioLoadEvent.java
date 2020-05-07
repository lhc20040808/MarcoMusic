package com.marco.lib_audio.event;

import com.marco.lib_audio.model.Track;

public class AudioLoadEvent {
    public Track track;

    public AudioLoadEvent(Track track) {
        this.track = track;
    }
}
