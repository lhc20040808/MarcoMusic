package com.marco.lib_audio.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.marco.lib_audio.R;
import com.marco.lib_audio.app.AudioHelper;
import com.marco.lib_audio.db.GreenDaoHelper;
import com.marco.lib_audio.mediaplayer.core.AudioController;
import com.marco.lib_audio.mediaplayer.core.MusicService;
import com.marco.lib_audio.model.Track;
import com.marco.lib_image_loder.ImageLoaderManager;

/**
 * 音乐Notification帮助类
 * 1.完成notification的创建和初始化
 * 2.对外提供表更新notification的方法
 */
public class NotificationHelper {

    public static final String CHANNEL_ID = "channel_id_audio";
    public static final String CHANNEL_NAME = "channel_name_audio";
    public static final int NOTIFICATION_ID = 0x111;

    /**
     * UI相关
     */
    //最终的Notification显示类
    private Notification mNotification;
    private RemoteViews mRemoteViews; // 大布局
    private RemoteViews mSmallRemoteViews; //小布局
    private NotificationManager mNotificationManager;

    /**
     * data
     */
    private NotificationHelperListener mListener;
    private String packageName;
    //当前要播的歌曲Bean
    private Track track;

    public static NotificationHelper getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static NotificationHelper INSTANCE = new NotificationHelper();
    }

    public void init(NotificationHelperListener listener) {
        mNotificationManager = (NotificationManager) AudioHelper.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        packageName = AudioHelper.getContext().getPackageName();
        track = AudioController.getInstance().getNowPlaying();
        initNotification();
        mListener = listener;
        if (mListener != null) mListener.onNotificationInit();
    }

    /*
     * 创建Notification,
     */
    private void initNotification() {
        if (mNotification == null) {
            //首先创建布局
            initRemoteViews();
            //再构建Notification
            //适配安卓8.0的消息渠道
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //NotificationManager.IMPORTANCE_MIN: 静默
                //NotificationManager.IMPORTANCE_HIGH:随系统使用声音或振动
                NotificationChannel channel =
                        new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
                channel.enableLights(false);
                channel.enableVibration(false);
                channel.setVibrationPattern(new long[]{0});
                channel.setSound(null, null);
                mNotificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(AudioHelper.getContext(), MusicPlayerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(AudioHelper.getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(AudioHelper.getContext(), CHANNEL_ID)
                            .setContentIntent(pendingIntent)
                            .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setCustomBigContentView(mRemoteViews) //大布局
                            .setVibrate(new long[]{0})
                            .setContent(mSmallRemoteViews); //正常布局，两个布局可以切换
            mNotification = builder.build();

            showLoadStatus(track);
        }
    }

    /*
     * 创建Notification的布局,默认布局为Loading状态
     */
    private void initRemoteViews() {
        int layoutId = R.layout.notification_big_layout;
        mRemoteViews = new RemoteViews(packageName, layoutId);
        mRemoteViews.setTextViewText(R.id.title_view, track.name);
        mRemoteViews.setTextViewText(R.id.tip_view, track.album);


        int smalllayoutId = R.layout.notification_small_layout;
        mSmallRemoteViews = new RemoteViews(packageName, smalllayoutId);
        mSmallRemoteViews.setTextViewText(R.id.title_view, track.name);
        mSmallRemoteViews.setTextViewText(R.id.tip_view, track.album);

        //点击播放按钮要发关的广播
        Intent playIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PLAY);
        PendingIntent playPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(),
                        1, playIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);

        //点上一首按钮要发送的广播
        Intent previousIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PRE);
        PendingIntent previousPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(),
                        2, previousIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.previous_view, previousPendingIntent);

        //点下一首按钮要发送的广播
        Intent nextIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(),
                        3, nextIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);

        //点收藏要发送的广播
        Intent favouriteIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_FAV);
        PendingIntent favouritePendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(),
                        4, favouriteIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.favourite_view, favouritePendingIntent);
    }

    /**
     * 更新为加载状态
     */
    public void showLoadStatus(Track bean) {
        track = bean;
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mRemoteViews.setTextViewText(R.id.title_view, track.name);
            mRemoteViews.setTextViewText(R.id.tip_view, track.album);
            //为notification中的imageview加载图片
            ImageLoaderManager.getInstance()
                    .displayImageForNotification(
                            AudioHelper.getContext(),
                            R.id.image_view,
                            mRemoteViews,
                            mNotification,
                            NOTIFICATION_ID,
                            track.albumPic
                    );
            //更新收藏状态
            if (GreenDaoHelper.selectFavorite(bean) != null) {
                //被收藏过
                mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_loved);
            } else {
                mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_love_white);
            }

            //更新小布局
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mRemoteViews.setTextViewText(R.id.title_view, track.name);
            mRemoteViews.setTextViewText(R.id.tip_view, track.album);
            ImageLoaderManager.getInstance()
                    .displayImageForNotification(
                            AudioHelper.getContext(),
                            R.id.image_view,
                            mSmallRemoteViews,
                            mNotification,
                            NOTIFICATION_ID,
                            track.albumPic
                    );
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    /**
     * 更新为播放状态
     */
    public void showPlayStatus() {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    /**
     * 更新为暂停状态
     */
    public void showPauseStatus() {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    public void changeFavouriteStatus(boolean isFavourite) {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.favourite_view,
                    isFavourite ? R.mipmap.note_btn_loved : R.mipmap.note_btn_love_white);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    public Notification getNotification() {
        return mNotification;
    }

    /**
     * 与音乐service的回调通信
     */
    public interface NotificationHelperListener {
        void onNotificationInit();
    }
}
