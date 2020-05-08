package com.marco.lib_audio.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.marco.lib_audio.R;
import com.marco.lib_audio.db.GreenDaoHelper;
import com.marco.lib_audio.mediaplayer.core.AudioController;
import com.marco.lib_audio.mediaplayer.core.CustomMediaPlayer;
import com.marco.lib_audio.mediaplayer.event.AudioFavoriteEvent;
import com.marco.lib_audio.mediaplayer.event.AudioLoadEvent;
import com.marco.lib_audio.mediaplayer.event.AudioPauseEvent;
import com.marco.lib_audio.mediaplayer.event.AudioProgressEvent;
import com.marco.lib_audio.mediaplayer.event.AudioStartEvent;
import com.marco.lib_audio.model.Track;
import com.marco.lib_audio.utils.Utils;
import com.marco.lib_common_ui.base.BaseActivity;
import com.marco.lib_image_loder.ImageLoaderManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 播放音乐Activity
 */
public class MusicPlayerActivity extends BaseActivity {

    private RelativeLayout mBgView;
    private TextView mInfoView;
    private TextView mAuthorView;

    private ImageView mFavouriteView;

    private SeekBar mProgressView;
    private TextView mStartTimeView;
    private TextView mTotalTimeView;

    private ImageView mPlayModeView;
    private ImageView mPlayView;
    private ImageView mNextView;
    private ImageView mPrevView;

    private Animator animator;
    /**
     * data
     */
    private Track mAudioBean; //当前正在播放歌曲
    private AudioController.PlayMode mPlayMode; //当前播放模式

    public static void start(Activity context) {
        Intent intent = new Intent(context, MusicPlayerActivity.class);
        ActivityCompat.startActivity(context, intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(context).toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(
                    TransitionInflater.from(this)
                            .inflateTransition(
                                    R.transition.transition_bottom2top
                            )
            );
        }
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_music_service_layout);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        mAudioBean = AudioController.getInstance().getNowPlaying();
        mPlayMode = AudioController.getInstance().getPlayMode();
    }

    private void initView() {
        mBgView = findViewById(R.id.root_layout);
        //给背景添加模糊效果
        ImageLoaderManager.getInstance()
                .displayImageForViewGroup(mBgView, mAudioBean.albumPic);
        findViewById(R.id.back_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.title_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.share_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMusic(mAudioBean.mUrl, mAudioBean.name);
            }
        });
        findViewById(R.id.show_list_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出歌单列表dialog
            }
        });
        mInfoView = findViewById(R.id.album_view);
        mInfoView.setText(mAudioBean.albumInfo);
        mInfoView.requestFocus();//跑马灯效果焦点获取
        mAuthorView = findViewById(R.id.author_view);
        mAuthorView.setText(mAudioBean.author);

        mFavouriteView = findViewById(R.id.favourite_view);
        mFavouriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收藏与否
                AudioController.getInstance().changeFavorite();
            }
        });

        changeFavouriteStatus(false);
        mStartTimeView = findViewById(R.id.start_time_view);
        mTotalTimeView = findViewById(R.id.total_time_view);
        mProgressView = findViewById(R.id.progress_view);
        mProgressView.setProgress(0);
        mProgressView.setEnabled(false);

        mPlayModeView = findViewById(R.id.play_mode_view);
        mPlayModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                switch (mPlayMode) {
                    case LOOP:
                        AudioController.getInstance().
                                setPlayMode(AudioController.
                                        PlayMode.RANDOM);
                        break;
                    case RANDOM:
                        AudioController.getInstance().
                                setPlayMode(AudioController.
                                        PlayMode.REPEAT);
                        break;
                    case REPEAT:
                        AudioController.getInstance().
                                setPlayMode(AudioController.
                                        PlayMode.LOOP);
                        break;
                }
            }
        });
        updatePlayModeView();
        mPrevView = findViewById(R.id.previous_view);
        mPrevView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上一首
                AudioController.getInstance().prev();
            }
        });
        mPlayView = findViewById(R.id.play_view);
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放or暂停
                AudioController.getInstance().playOrPause();
            }
        });
        mNextView = findViewById(R.id.next_view);
        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下一首
                AudioController.getInstance().next();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //加载事件处理
        mAudioBean = event.track;
        ImageLoaderManager.getInstance().
                displayImageForViewGroup(mBgView, mAudioBean.albumPic);
        mInfoView.setText(mAudioBean.albumInfo);
        mAuthorView.setText(mAudioBean.author);
        changeFavouriteStatus(false);
        mProgressView.setProgress(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        showPlayView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        showPauseView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioProgressEvent(AudioProgressEvent event) {
        int totalTime = event.duration;
        int currentTime = event.progress;
        //更新时间
        mStartTimeView.setText(Utils.formatTime(currentTime));
        mTotalTimeView.setText(Utils.formatTime(totalTime));
        mProgressView.setProgress(currentTime);
        mProgressView.setMax(totalTime);
        if (event.status == CustomMediaPlayer.Status.PAUSED) {
            showPauseView();
        } else {
            showPlayView();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavouriteEvent(AudioFavoriteEvent event) {
        //更新收藏状态
        changeFavouriteStatus(true);
    }


    private void updatePlayModeView() {
        switch (mPlayMode) {
            case LOOP:
                mPlayModeView.setImageResource(R.mipmap.player_loop);
                break;
            case RANDOM:
                mPlayModeView.setImageResource(R.mipmap.player_random);
                break;
            case REPEAT:
                mPlayModeView.setImageResource(R.mipmap.player_once);
                break;
        }
    }


    private void changeFavouriteStatus(boolean anim) {
        if (GreenDaoHelper.selectFavorite(mAudioBean) != null) {
            mFavouriteView.setImageResource(R.mipmap.audio_aeh);
        } else {
            mFavouriteView.setImageResource(R.mipmap.audio_aef);
        }
        if (anim) {
            //完成收藏效果动画
            if (animator != null) animator.end();
            PropertyValuesHolder animX =
                    PropertyValuesHolder
                            .ofFloat(View.SCALE_X.getName(),
                                    1.0f, 1.2f, 1.0f);
            PropertyValuesHolder animY =
                    PropertyValuesHolder
                            .ofFloat(View.SCALE_Y.getName(),
                                    1.0f, 1.2f, 1.0f);
            animator = ObjectAnimator.ofPropertyValuesHolder(mFavouriteView, animX, animY);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(300);
            animator.start();
        }
    }


    private void showPlayView() {
        mPlayView.setImageResource(R.mipmap.audio_aj6);
    }

    private void showPauseView() {
        mPlayView.setImageResource(R.mipmap.audio_aj7);
    }

    /**
     * 分享音乐给好友
     */
    private void shareMusic(String url, String name) {
    }
}
