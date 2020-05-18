package com.marco.ft_market.view.splash;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.marco.ft_market.R;
import com.marco.lib_base.ft_home.HomeServiceWrapper;
import com.marco.lib_common_ui.base.BaseActivity;
import com.marco.lib_common_ui.base.constant.Constant;
import com.marco.lib_pullalive.AliveJobService;

public class LoadingActivity extends BaseActivity {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            HomeServiceWrapper.getInstance().startHomeActivity(getBaseContext());
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AliveJobService.start(this);
        }
        if (hasPermission(Constant.WRITE_READ_EXTERNAL_PERMISSION)) {
            doSDCardPermission();
        } else {
            requestPermission(Constant.WRITE_READ_EXTERNAL_CODE, Constant.WRITE_READ_EXTERNAL_PERMISSION);
        }
    }


    @Override
    public void doSDCardPermission() {
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
