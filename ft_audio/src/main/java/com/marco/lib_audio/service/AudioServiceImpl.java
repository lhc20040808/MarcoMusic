package com.marco.lib_audio.service;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.marco.lib_audio.app.AudioHelper;
import com.marco.lib_audio.mediaplayer.core.AudioController;
import com.marco.lib_base.audio.AudioService;
import com.marco.lib_model.ft_audio.model.Track;

import java.util.ArrayList;

@Route(path = AudioService.PATH)
public class AudioServiceImpl implements AudioService {

    @Override
    public void pauseAudio() {
        AudioController.getInstance().pause();
    }

    @Override
    public void resumeAudio() {
        AudioController.getInstance().resume();
    }

    @Override
    public void startMusicService(ArrayList<Track> audios) {
        AudioHelper.startMusicService(audios);
    }

    @Override
    public void addAudio(Activity activity, Track bean) {
        AudioHelper.addAudio(activity, bean);
    }

    @Override
    public void init(Context context) {

    }
}
