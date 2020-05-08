package com.marco.lib_audio.app;

import android.content.Context;

import com.marco.lib_audio.db.GreenDaoHelper;
import com.marco.lib_audio.mediaplayer.core.MusicService;
import com.marco.lib_audio.model.Track;

import java.util.ArrayList;
import java.util.List;

public class AudioHelper {
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
        GreenDaoHelper.initDatabase();
    }

    public static Context getContext() {
        return sContext;
    }

    //外部启动MusicService方法
    public static void startMusicService(ArrayList<Track> audios) {
        MusicService.startMusicService(audios);
    }
}
