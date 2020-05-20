package com.marco.lib_base.ft_home;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;

public class HomeServiceWrapper {

    @Autowired(name = HomeService.PATH)
    protected HomeService homeService;

    private HomeServiceWrapper() {
        ARouter.getInstance().inject(this);
    }

    private volatile static HomeServiceWrapper sInstance = null;

    public static HomeServiceWrapper getInstance() {
        if (sInstance == null) {
            synchronized (HomeServiceWrapper.class) {
                if (sInstance == null) {
                    sInstance = new HomeServiceWrapper();
                }
            }
        }
        return sInstance;
    }

    public void startHomeActivity(Context context) {
        homeService.startHomeActivity(context);
    }

}
