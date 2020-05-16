package com.marco.lib_audio.mediaplayer.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.marco.lib_audio.app.AudioHelper;
import com.marco.lib_audio.mediaplayer.event.AudioFavoriteEvent;
import com.marco.lib_audio.mediaplayer.event.AudioLoadEvent;
import com.marco.lib_audio.mediaplayer.event.AudioPauseEvent;
import com.marco.lib_audio.mediaplayer.event.AudioStartEvent;
import com.marco.lib_audio.view.NotificationHelper;
import com.marco.lib_model.ft_audio.model.Track;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 音乐后台服务,并更新notification状态
 */
public class MusicService extends Service implements NotificationHelper.NotificationHelperListener {

    private static String DATA_AUDIOS = "AUDIOS";
    //actions
    private static String ACTION_START = "ACTION_START";

    private ArrayList<Track> mTracks;

    private NotificationReceiver mReceiver;

    /**
     * 外部直接service方法
     */
    public static void startMusicService(ArrayList<Track> audioBeans) {
        Intent intent = new Intent(AudioHelper.getContext(), MusicService.class);
        intent.setAction(ACTION_START);
        //还需要传list数据进来
        intent.putExtra(DATA_AUDIOS, audioBeans);
        AudioHelper.getContext().startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        registerBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTracks = (ArrayList<Track>) intent.getSerializableExtra(DATA_AUDIOS);
        if (intent.getAction().equals(ACTION_START)) {
            //播放音乐
            playMusic();
            //初始化Notification
            NotificationHelper.getInstance().init(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unRegisterBroadcastReceiver();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新notification状态为加载态
        NotificationHelper.getInstance().showLoadStatus(event.track);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新notification状态为play态
        NotificationHelper.getInstance().showPlayStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新notification状态为pause态
        NotificationHelper.getInstance().showPauseStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavoriteEvent(AudioFavoriteEvent event) {
        NotificationHelper.getInstance().changeFavouriteStatus(event.isFavorite);
    }

    private void playMusic() {
        AudioController.getInstance().setQueue(mTracks);
        AudioController.getInstance().play();
    }

    private void registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(NotificationReceiver.ACTION_STATUS_BAR);
            registerReceiver(mReceiver, filter);
        }
    }

    private void unRegisterBroadcastReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onNotificationInit() {
        //把Notification和Service绑定，让Service成为前台服务
        startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.getInstance().getNotification());
    }

    /**
     * 接收Notification发送的广播
     */
    public static class NotificationReceiver extends BroadcastReceiver {
        public static final String ACTION_STATUS_BAR =
                AudioHelper.getContext().getPackageName() + ".NOTIFICATION_ACTIONS";
        public static final String EXTRA = "extra";
        public static final String EXTRA_PLAY = "play_pause";
        public static final String EXTRA_NEXT = "play_next";
        public static final String EXTRA_PRE = "play_previous";
        public static final String EXTRA_FAV = "play_favourite";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            String action = intent.getStringExtra(EXTRA);
            switch (action) {
                case EXTRA_PLAY:
                    AudioController.getInstance().playOrPause();
                    break;
                case EXTRA_PRE:
                    AudioController.getInstance().prev();
                    break;
                case EXTRA_NEXT:
                    AudioController.getInstance().next();
                    break;
                case EXTRA_FAV:
                    AudioController.getInstance().changeFavorite();
                    break;
            }

        }
    }
}
