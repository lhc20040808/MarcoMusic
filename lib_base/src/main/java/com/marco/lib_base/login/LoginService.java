package com.marco.lib_base.login;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.marco.lib_base.login.model.user.User;

public interface LoginService extends IProvider {

    String PATH = "/login/login_service";

    User getUserInfo();

    void removeUser();

    boolean hasLogin();

    void login(Context context);
}
