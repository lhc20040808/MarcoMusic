package com.marco.lib_audio.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marco.lib_audio.R;
import com.marco.lib_audio.mediaplayer.core.AudioController;
import com.marco.lib_audio.mediaplayer.event.AudioLoadEvent;
import com.marco.lib_audio.mediaplayer.event.AudioPauseEvent;
import com.marco.lib_audio.mediaplayer.event.AudioStartEvent;
import com.marco.lib_image_loder.ImageLoaderManager;
import com.marco.lib_model.ft_audio.model.Track;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 播放器底部View
 */
public class BottomMusicView extends RelativeLayout {

    private Context mContext;

    /*
     * View
     */
    private ImageView mLeftView;
    private TextView mTitleView;
    private TextView mAlbumView;
    private ImageView mPlayView;
    private ImageView mRightView;
    /*
     * data
     */
    private Track track;
    private ObjectAnimator animator;

    public BottomMusicView(Context context) {
        this(context, null);
    }

    public BottomMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.bottom_view, this);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerActivity.start((Activity) mContext);
            }
        });
        mLeftView = rootView.findViewById(R.id.album_view);
        //让左侧mLeftView不停旋转
        animator = ObjectAnimator
                .ofFloat(mLeftView, View.ROTATION.getName(), 0f, 360);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        animator.start();

        mTitleView = rootView.findViewById(R.id.audio_name_view);
        mAlbumView = rootView.findViewById(R.id.audio_album_view);
        mPlayView = rootView.findViewById(R.id.play_view);
        mPlayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理播放暂停事件
                AudioController.getInstance().playOrPause();
            }
        });
        mRightView = rootView.findViewById(R.id.show_list_view);
        mRightView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 显示音乐列表对话框
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //监听加载事件
        track = event.track;
        showLoadingView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        showPlayView();
        if (animator.isPaused()) {
            animator.resume();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        showPauseView();
        if (animator.isRunning()) {
            animator.pause();
        }
    }

    private void showLoadingView() {
        if (track != null) {
            ImageLoaderManager.getInstance().
                    displayImageForCircle(mLeftView, track.albumPic);
            mTitleView.setText(track.name);
            mAlbumView.setText(track.album);
            mPlayView.setImageResource(R.mipmap.note_btn_pause_white);
        }
    }

    private void showPauseView() {
        if (track != null) {
            mPlayView.setImageResource(R.mipmap.note_btn_play_white);
        }
    }

    private void showPlayView() {
        if (track != null) {
            mPlayView.setImageResource(R.mipmap.note_btn_pause_white);
        }
    }
}
