package com.marco.voice.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.marco.lib_common_ui.base.BaseActivity;
import com.marco.lib_network.response.listener.DisposeDataListener;
import com.marco.lib_network.utils.ResponseEntityToModule;
import com.marco.voice.R;
import com.marco.voice.view.api.MockData;
import com.marco.voice.view.api.RequestCenter;
import com.marco.voice.view.login.event.LoginEvent;
import com.marco.voice.view.login.manager.UserManager;
import com.marco.voice.view.login.user.User;

import org.greenrobot.eventbus.EventBus;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements DisposeDataListener {

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
        EventBus.getDefault().post(new LoginEvent());
        finish();
    }

    @Override
    public void onFailure(Object reasonObj) {
        //登录失败逻辑
        onSuccess(ResponseEntityToModule.parseJsonToModule(
                MockData.LOGIN_DATA, User.class));
    }
}
