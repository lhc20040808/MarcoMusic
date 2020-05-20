package com.marco.voice.application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.marco.lib_audio.app.AudioHelper;
import com.qihoo360.replugin.RePluginApplication;

public class VoiceApplication extends RePluginApplication {

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
