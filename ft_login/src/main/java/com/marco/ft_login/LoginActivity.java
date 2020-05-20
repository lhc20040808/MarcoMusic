package com.marco.ft_login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.marco.ft_login.api.MockData;
import com.marco.ft_login.api.RequestCenter;
import com.marco.ft_login.manager.UserManager;
import com.marco.lib_base.login.model.user.User;
import com.marco.lib_network.response.listener.DisposeDataListener;
import com.marco.lib_network.utils.ResponseEntityToModule;
import com.qihoo360.replugin.loader.a.PluginFragmentActivity;

/**
 * 登录页面
 */
public class LoginActivity extends PluginFragmentActivity implements DisposeDataListener {

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
        //TODO 插件化不能通过eventBus通信
//        EventBus.getDefault().post(new LoginEvent());
        finish();
    }

    @Override
    public void onFailure(Object reasonObj) {
        //登录失败逻辑
        onSuccess(ResponseEntityToModule.parseJsonToModule(
                MockData.LOGIN_DATA, User.class));
    }
}
