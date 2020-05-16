package com.marco.lib_base.ft_home;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface HomeService extends IProvider {

    String PATH = "/home/home_service";

    void startHomeActivity(Context context);
}
