package com.marco.lib_audio.mediaplayer.event;

import com.marco.lib_audio.mediaplayer.core.AudioController;

public class AudioPlayModeEvent {
    public AudioController.PlayMode playMode;

    public AudioPlayModeEvent(AudioController.PlayMode playMode) {
        this.playMode = playMode;
    }
}
