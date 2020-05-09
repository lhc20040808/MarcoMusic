package com.marco.lib_video.player;

import android.view.ViewGroup;

public class VideoContext implements VideoSlot.VideoSlotListener {

    private ViewGroup viewGroup;
    private VideoSlot videoSlot;
    private String url;

    public VideoContext(ViewGroup viewGroup, String url) {
        this.viewGroup = viewGroup;
        this.url = url;
        this.videoSlot = new VideoSlot(url, this);
    }

    @Override
    public void onVideoLoadFailed() {

    }

    @Override
    public void onVideoLoadComplete() {

    }

    @Override
    public void onVideoLoadSuccess() {

    }

    @Override
    public ViewGroup getParent() {
        return viewGroup;
    }
}
