package com.marco.lib_audio.app;

import android.content.Context;

public class AudioHelper {
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }
}
