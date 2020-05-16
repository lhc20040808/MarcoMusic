package com.marco.lib_base.audio;

import android.app.Activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.marco.lib_model.ft_audio.model.Track;

import java.util.ArrayList;

public class AudioServiceWrapper {

    @Autowired(name = AudioService.PATH)
    protected AudioService audioService;

    private AudioServiceWrapper() {
        ARouter.getInstance().inject(this);
    }

    private volatile static AudioServiceWrapper sInstance = null;

    public static AudioServiceWrapper getInstance() {
        if (sInstance == null) {
            synchronized (AudioServiceWrapper.class) {
                if (sInstance == null) {
                    sInstance = new AudioServiceWrapper();
                }
            }
        }
        return sInstance;
    }

    public void pauseAudio() {
        audioService.pauseAudio();
    }

    public void resumeAudio() {
        audioService.resumeAudio();
    }

    public void startMusicService(ArrayList<Track> audios) {
        audioService.startMusicService(audios);
    }

    public void addAudio(Activity activity, Track bean) {
        audioService.addAudio(activity, bean);
    }
}
