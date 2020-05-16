package com.marco.ft_home.service;

import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.marco.ft_home.view.home.HomeActivity;
import com.marco.lib_base.ft_home.HomeService;

@Route(path = HomeService.PATH)
public class HomeServiceImpl implements HomeService {
    @Override
    public void startHomeActivity(Context context) {
        context.startActivity(new Intent(context, HomeActivity.class));
    }

    @Override
    public void init(Context context) {

    }
}
