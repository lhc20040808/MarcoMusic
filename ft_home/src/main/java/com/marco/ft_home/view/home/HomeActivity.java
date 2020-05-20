package com.marco.ft_home.view.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.marco.ft_home.R;
import com.marco.ft_home.constant.Constant;
import com.marco.ft_home.view.home.adpater.HomePagerAdapter;
import com.marco.ft_home.view.home.model.CHANNEL;
import com.marco.lib_base.ILoginService;
import com.marco.lib_base.audio.AudioServiceWrapper;
import com.marco.lib_base.ft_login.LoginPluginConfig;
import com.marco.lib_base.ft_login.model.user.User;
import com.marco.lib_common_ui.base.BaseActivity;
import com.marco.lib_common_ui.pager_indicator.ScaleTransitionPagerTitleView;
import com.marco.lib_image_loder.ImageLoaderManager;
import com.marco.lib_model.ft_audio.model.Track;
import com.qihoo360.replugin.RePlugin;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    //指定首页要出现的卡片
    private static final CHANNEL[] CHANNELS =
            new CHANNEL[]{CHANNEL.MY, CHANNEL.DISCORY, CHANNEL.FRIEND};

    /*
     * View
     */
    private DrawerLayout mDrawerLayout;
    private View mToggleView;
    private View mSearchView;
    private ViewPager mViewPager;
    private HomePagerAdapter mAdapter;

    private View unLogginLayout;
    private ImageView mPhotoView;

    /*
     * data
     */
    private ArrayList<Track> mLists = new ArrayList<>();

    private UserBroadcastReceiver userBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerUserBroadcastReceiver();
        setContentView(R.layout.activity_home);
        initView();
        initData();
    }

    private void initData() {
        mLists.add(new Track("100001", "http://sp-sycdn.kuwo.cn/resource/n2/85/58/433900159.mp3",
                "以你的名字喊我", "周杰伦", "七里香", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698076304&di=e6e99aa943b72ef57b97f0be3e0d2446&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201401%2F04%2F20140104170315_XdG38.jpeg",
                "4:30"));
        mLists.add(
                new Track("100002", "http://sq-sycdn.kuwo.cn/resource/n1/98/51/3777061809.mp3", "勇气",
                        "梁静茹", "勇气", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698193627&di=711751f16fefddbf4cbf71da7d8e6d66&imgtype=jpg&src=http%3A%2F%2Fimg0.imgtn.bdimg.com%2Fit%2Fu%3D213168965%2C1040740194%26fm%3D214%26gp%3D0.jpg",
                        "4:40"));
        mLists.add(
                new Track("100003", "http://sp-sycdn.kuwo.cn/resource/n2/52/80/2933081485.mp3", "灿烂如你",
                        "汪峰", "春天里", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698239736&di=3433a1d95c589e31a36dd7b4c176d13a&imgtype=0&src=http%3A%2F%2Fpic.zdface.com%2Fupload%2F201051814737725.jpg",
                        "3:20"));
        mLists.add(
                new Track("100004", "http://sr-sycdn.kuwo.cn/resource/n2/33/25/2629654819.mp3", "小情歌",
                        "五月天", "小幸运", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698289780&di=5146d48002250bf38acfb4c9b4bb6e4e&imgtype=0&src=http%3A%2F%2Fpic.baike.soso.com%2Fp%2F20131220%2Fbki-20131220170401-1254350944.jpg",
                        "2:45"));
        AudioServiceWrapper.getInstance().startMusicService(mLists);
    }

    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggleView = findViewById(R.id.toggle_view);
        mToggleView.setOnClickListener(this);
        mSearchView = findViewById(R.id.search_view);

        mViewPager = findViewById(R.id.view_pager);
        mAdapter = new HomePagerAdapter(getSupportFragmentManager(), CHANNELS);
        mViewPager.setAdapter(mAdapter);
        initMagicIndicator();
        //登录相关UI
        unLogginLayout = findViewById(R.id.unloggin_layout);
        unLogginLayout.setOnClickListener(this);
        mPhotoView = findViewById(R.id.avatr_view);

        findViewById(R.id.online_music_view).setOnClickListener(this);
    }

    //初始化指示器
    private void initMagicIndicator() {
        MagicIndicator magicIndicator = findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return CHANNELS == null ? 0 : CHANNELS.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(CHANNELS[index].getKey());
                simplePagerTitleView.setTextSize(19);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return 1.0f;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterUserBroadcastReceiver();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.unloggin_layout) {
            IBinder iBinder = RePlugin.fetchBinder(LoginPluginConfig.PLUGIN_NAME, LoginPluginConfig.KEY_INTERFACE);
            if (iBinder == null) {
                Log.e("HomeActivity","no LoginServiceImpl found");
                return;
            }
            ILoginService iLoginService = ILoginService.Stub.asInterface(iBinder);
            try {
                if (!iLoginService.hasLogin()) {
                    Intent intent = RePlugin.createIntent(LoginPluginConfig.PLUGIN_NAME, "com.marco.ft_login.LoginActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    RePlugin.startActivity(this, intent);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.online_music_view) {//跳到指定webactivity
            gotoWebView("https://www.imooc.com");
        }
    }

    private void updateLoginUI(User user) {
        unLogginLayout.setVisibility(View.GONE);
        mPhotoView.setVisibility(View.VISIBLE);
        ImageLoaderManager.getInstance()
                .displayImageForCircle(mPhotoView, user.data.photoUrl);
    }

    private void gotoWebView(String url) {
        ARouter.getInstance()
                .build(Constant.Router.ROUTER_WEB_ACTIVIYT)
                .withString("url", url)
                .navigation();
    }

    private void registerUserBroadcastReceiver() {
        if (userBroadcastReceiver == null) {
            userBroadcastReceiver = new UserBroadcastReceiver();
        }

        registerReceiver(userBroadcastReceiver
                , new IntentFilter(LoginPluginConfig.ACTION.LOGIN_SUCCESS_ACTION));
    }

    private void unregisterUserBroadcastReceiver() {
        if (userBroadcastReceiver != null) {
            unregisterReceiver(userBroadcastReceiver);
        }
    }

    private class UserBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(LoginPluginConfig.ACTION.LOGIN_SUCCESS_ACTION)) {
                User user = new Gson().fromJson(intent.getStringExtra(LoginPluginConfig.KEY.USER_DATA), User.class);
                updateLoginUI(user);
            }
        }
    }
}
