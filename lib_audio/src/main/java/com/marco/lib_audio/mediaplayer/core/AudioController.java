package com.marco.lib_audio.mediaplayer.core;

import com.marco.lib_audio.exception.AudioQueueEmptyException;
import com.marco.lib_audio.model.Track;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AudioController {
    /**
     * 播放方式
     */
    private enum PlayMode {
        //列表循环
        LOOP,
        //随机
        RANDOM,
        //单曲循环
        REPEAT
    }

    private AudioPlayer mAudioPlayer;
    private List<Track> mQueue;
    private int mQueueIndex;
    private PlayMode mPlayMode;

    public AudioController() {
        this.mQueueIndex = 0;
        this.mQueue = new ArrayList<>();
        this.mAudioPlayer = new AudioPlayer();
        this.mPlayMode = PlayMode.LOOP;
    }

    private static class Holder {
        private static final AudioController INSTANCE = new AudioController();
    }

    public static AudioController getInstance() {
        return Holder.INSTANCE;
    }

    public List<Track> getQueue() {
        return mQueue == null ? new ArrayList<Track>() : mQueue;
    }

    /**
     * 设置播放队列
     *
     * @param queue
     */
    public void setQueue(List<Track> queue) {
        setQueue(queue, 0);
    }

    public void setQueue(List<Track> queue, int queueIndex) {
        this.mQueue = queue;
        this.mQueueIndex = queueIndex;
    }

    public void addAudio(Track track) {
        addAudio(0, track);
    }

    /**
     * 添加单一声音
     *
     * @param index
     * @param track
     */
    public void addAudio(int index, Track track) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空");
        }
        int query = queryAudio(track);
        if (query == -1) {
            //没有添加过该声音
            addCustomAudio(index, track);
            setPlayIndex(index);
        } else {
            Track curTrack = getNowPlaying();
            if (!curTrack.id.equals(track.id)) {
                //已经添加过且不是当前播放的声音，则播放该声音
                setPlayIndex(query);
            }
        }
    }

    private void addCustomAudio(int index, Track track) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前队列为空");
        }
        mQueue.add(index, track);
    }

    private int queryAudio(Track track) {
        return 0;
    }

    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.mPlayMode = playMode;
    }

    public int getPlayIndex() {
        return mQueueIndex;
    }

    public void setPlayIndex(int queueIndex) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("audio queue is null");
        }
        this.mQueueIndex = queueIndex;
        play();
    }

    public void pause() {
        mAudioPlayer.pause();
    }

    public void resume() {
        mAudioPlayer.resume();
    }

    public void release() {
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    public void playOrPause() {
        if (isStartState()) {
            pause();
        } else if (isPauseState()) {
            resume();
        }
    }

    public void play() {
        Track track = getNowPlaying();
        mAudioPlayer.load(track);
    }

    /**
     * 播放下一首
     */
    public void next() {
        Track track = getNextPlaying();
        mAudioPlayer.load(track);
    }

    /**
     * 播放上一首
     */
    public void prev() {
        Track track = getPrevPlaying();
        mAudioPlayer.load(track);
    }

    public boolean isStartState() {
        return getStatus() == CustomMediaPlayer.Status.STARTED;
    }

    public boolean isPauseState() {
        return getStatus() == CustomMediaPlayer.Status.PAUSED;
    }

    private Track getNowPlaying() {
        return getPlaying();
    }

    private Track getPlaying() {
        if (mQueue != null && !mQueue.isEmpty() && mQueueIndex >= 0 && mQueueIndex < mQueue.size()) {
            return mQueue.get(mQueueIndex);
        } else {
            throw new AudioQueueEmptyException("当前播放队列为空");
        }
    }

    private Track getNextPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getPlaying();
    }

    private Track getPrevPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex - 1 + mQueue.size()) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getPlaying();
    }

    private CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }
}
