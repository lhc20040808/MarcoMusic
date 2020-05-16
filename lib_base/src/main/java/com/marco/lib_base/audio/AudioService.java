package com.marco.lib_base.audio;

import android.app.Activity;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.marco.lib_model.ft_audio.model.Track;

import java.util.ArrayList;

public interface AudioService extends IProvider {

    String PATH = "/audio/audio_service";

    void pauseAudio();

    void resumeAudio();

    void startMusicService(ArrayList<Track> audios);

    void addAudio(Activity activity, Track bean);
}
