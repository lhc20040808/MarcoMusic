package com.marco.ft_login.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.marco.ft_login.LoginActivity;
import com.marco.ft_login.manager.UserManager;
import com.marco.lib_base.ft_login.LoginService;
import com.marco.lib_base.ft_login.model.user.User;

@Route(path = LoginService.PATH)
public class LoginServiceImpl implements LoginService {
    @Override
    public User getUserInfo() {
        return UserManager.getInstance().getUser();
    }

    @Override
    public void removeUser() {
        UserManager.getInstance().removeUser();
    }

    @Override
    public boolean hasLogin() {
        return UserManager.getInstance().hasLogin();
    }

    @Override
    public void login(Context context) {
        LoginActivity.start(context);
    }

    @Override
    public void init(Context context) {
        Log.d(LoginServiceImpl.class.getSimpleName(), "init()");
    }
}
