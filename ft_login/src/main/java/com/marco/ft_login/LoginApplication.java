package com.marco.ft_login;

import android.app.Application;
import android.util.Log;

import com.marco.ft_login.service.aidl.LoginServiceImpl;
import com.marco.lib_base.ft_login.LoginPluginConfig;
import com.qihoo360.replugin.RePlugin;

public class LoginApplication extends Application {
    private static final String TAG = LoginApplication.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"load LoginServiceImpl");
        RePlugin.registerPluginBinder(LoginPluginConfig.KEY_INTERFACE, new LoginServiceImpl());
    }
}
