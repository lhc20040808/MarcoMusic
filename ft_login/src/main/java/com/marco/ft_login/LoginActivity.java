package com.marco.ft_login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.Gson;
import com.marco.ft_login.api.MockData;
import com.marco.ft_login.api.RequestCenter;
import com.marco.ft_login.manager.UserManager;
import com.marco.lib_base.ft_login.LoginPluginConfig;
import com.marco.lib_base.ft_login.model.user.User;
import com.marco.lib_common_ui.base.PluginBaseActivity;
import com.marco.lib_network.response.listener.DisposeDataListener;
import com.marco.lib_network.utils.ResponseEntityToModule;

/**
 * 登录页面
 */
public class LoginActivity extends PluginBaseActivity implements DisposeDataListener {

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestCenter.login(LoginActivity.this);
            }
        });
    }

    @Override
    public void onSuccess(Object responseObj) {
        //处理正常逻辑
        User user = (User) responseObj;
        UserManager.getInstance().saveUser(user);
        sendBroadcast(user);
        finish();
    }

    @Override
    public void onFailure(Object reasonObj) {
        //登录失败逻辑
        onSuccess(ResponseEntityToModule.parseJsonToModule(
                MockData.LOGIN_DATA, User.class));
    }

    private void sendBroadcast(User user) {
        Intent intent = new Intent();
        intent.setAction(LoginPluginConfig.ACTION.LOGIN_SUCCESS_ACTION);
        intent.putExtra(LoginPluginConfig.KEY.USER_DATA, new Gson().toJson(user));
        sendBroadcast(intent);
    }
}
