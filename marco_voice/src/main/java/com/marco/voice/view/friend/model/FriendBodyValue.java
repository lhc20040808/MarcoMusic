package com.marco.voice.view.friend.model;

import com.marco.lib_audio.model.Track;
import com.marco.voice.model.BaseModel;

import java.util.ArrayList;

/**
 * 朋友数据实体
 */
public class FriendBodyValue extends BaseModel {

    public int type;
    public String avatr;
    public String name;
    public String fans;
    public String text;
    public ArrayList<String> pics;
    public String videoUrl;
    public String zan;
    public String msg;
    public Track audioBean;
}
