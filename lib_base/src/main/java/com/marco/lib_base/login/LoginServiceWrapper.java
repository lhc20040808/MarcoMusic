package com.marco.lib_base.login;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.marco.lib_base.login.model.user.User;

public class LoginServiceWrapper {

    @Autowired(name = LoginService.PATH)
    protected LoginService loginService;

    private LoginServiceWrapper() {
        ARouter.getInstance().inject(this);
    }

    private volatile static LoginServiceWrapper sInstance = null;

    public static LoginServiceWrapper getInstance() {
        if (sInstance == null) {
            synchronized (LoginServiceWrapper.class) {
                if (sInstance == null) {
                    sInstance = new LoginServiceWrapper();
                }
            }
        }
        return sInstance;
    }

    public void login(Context context) {
        loginService.login(context);
    }

    public User getUserInfo() {
        return loginService.getUserInfo();
    }

    public void removeUser() {
        loginService.removeUser();
    }

    public boolean hasLogin() {
        return loginService.hasLogin();
    }


}
