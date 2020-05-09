package com.marco.voice.application;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.marco.lib_audio.app.AudioHelper;

public class VoiceApplication extends Application {

    private static VoiceApplication mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        AudioHelper.init(this);
        ARouter.init(this);
    }

    public static VoiceApplication getInstance() {
        return mApplication;
    }
}
