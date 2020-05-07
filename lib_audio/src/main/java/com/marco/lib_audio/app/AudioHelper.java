package com.marco.lib_audio.app;

import android.content.Context;

import com.marco.lib_audio.db.GreenDaoHelper;

public class AudioHelper {
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
        GreenDaoHelper.initDatabase();
    }

    public static Context getContext() {
        return sContext;
    }
}
