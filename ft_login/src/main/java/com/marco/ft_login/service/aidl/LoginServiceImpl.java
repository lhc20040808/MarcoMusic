package com.marco.ft_login.service.aidl;

import com.marco.ft_login.manager.UserManager;
import com.marco.lib_base.ILoginService;

public class LoginServiceImpl extends ILoginService.Stub {

    @Override
    public boolean hasLogin() {
        return UserManager.getInstance().hasLogin();
    }
}
