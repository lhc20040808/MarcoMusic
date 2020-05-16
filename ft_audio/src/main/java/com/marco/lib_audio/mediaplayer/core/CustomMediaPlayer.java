package com.marco.lib_audio.mediaplayer.core;

import android.media.MediaPlayer;

import java.io.IOException;

public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {

    public enum Status {
        IDLE, INITIALIZED, STARTED, PAUSED, STOPPED, COMPLETED
    }

    private Status status;
    private OnCompletionListener onCompletionListener;

    public CustomMediaPlayer() {
        super();
        status = Status.IDLE;
        super.setOnCompletionListener(this);
    }

    @Override
    public void reset() {
        super.reset();
        status = Status.IDLE;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        status = Status.PAUSED;
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        status = Status.INITIALIZED;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        status = Status.STARTED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        status = Status.STOPPED;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        status = Status.COMPLETED;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isComplete() {
        return status == Status.COMPLETED;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }
}
