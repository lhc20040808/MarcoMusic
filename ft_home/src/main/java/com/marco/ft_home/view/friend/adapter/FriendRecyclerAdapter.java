package com.marco.ft_home.view.friend.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.marco.ft_home.R;
import com.marco.ft_home.view.friend.model.FriendBodyValue;
import com.marco.lib_audio.app.AudioHelper;
import com.marco.lib_base.login.LoginServiceWrapper;
import com.marco.lib_common_ui.recyclerview.MultiItemTypeAdapter;
import com.marco.lib_common_ui.recyclerview.base.ItemViewDelegate;
import com.marco.lib_common_ui.recyclerview.base.ViewHolder;
import com.marco.lib_common_ui.view.MultiImageViewLayout;
import com.marco.lib_image_loder.ImageLoaderManager;
import com.marco.lib_video.player.VideoContext;

import java.util.List;

public class FriendRecyclerAdapter extends MultiItemTypeAdapter {

    public static final int MUSIC_TYPE = 0x01; //音乐类型
    public static final int VIDEO_TYPE = 0x02; //视频类型

    private Context mContext;

    public FriendRecyclerAdapter(Context context, List<FriendBodyValue> datas) {
        super(context, datas);
        mContext = context;
        addItemViewDelegate(MUSIC_TYPE, new MusicItemDelegate());
        addItemViewDelegate(VIDEO_TYPE, new VideoItemDelegate());
    }

    private class MusicItemDelegate implements ItemViewDelegate<FriendBodyValue> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_friend_list_picture_layout;
        }

        @Override
        public boolean isForViewType(FriendBodyValue item, int position) {
            return item.type == FriendRecyclerAdapter.MUSIC_TYPE;
        }

        @Override
        public void convert(ViewHolder holder, final FriendBodyValue recommandBodyValue, int position) {
            //为viewholder绑定数据
            holder.setText(R.id.name_view, recommandBodyValue.name + " 分享单曲:");
            holder.setText(R.id.fansi_view, recommandBodyValue.fans + "粉丝");
            holder.setText(R.id.text_view, recommandBodyValue.text);
            holder.setText(R.id.zan_view, recommandBodyValue.zan);
            holder.setText(R.id.message_view, recommandBodyValue.msg);
            holder.setText(R.id.audio_name_view, recommandBodyValue.audioBean.name);
            holder.setText(R.id.audio_author_view, recommandBodyValue.audioBean.album);
            holder.setOnClickListener(R.id.album_layout, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //调用音频播放器方法
                    AudioHelper.addAudio((Activity) mContext, recommandBodyValue.audioBean);
                }
            });
            holder.setOnClickListener(R.id.guanzhu_view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!LoginServiceWrapper.getInstance().hasLogin()) {
                        //goto login
                        LoginServiceWrapper.getInstance().login(mContext);
                    }
                }
            });
            ImageView avatar = holder.getView(R.id.photo_view);
            ImageLoaderManager.getInstance().displayImageForCircle(avatar, recommandBodyValue.avatr);
            ImageView albumPicView = holder.getView(R.id.album_view);
            ImageLoaderManager.getInstance()
                    .displayImageForView(albumPicView, recommandBodyValue.audioBean.albumPic);
            //多图自动布局
            MultiImageViewLayout imageViewLayout = holder.getView(R.id.image_layout);
            imageViewLayout.setList(recommandBodyValue.pics);

        }
    }

    /**
     * 视频类型item
     */
    private class VideoItemDelegate implements ItemViewDelegate<FriendBodyValue> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_friend_list_video_layout;
        }

        @Override
        public boolean isForViewType(FriendBodyValue item, int position) {
            return item.type == FriendRecyclerAdapter.VIDEO_TYPE;
        }

        @Override
        public void convert(ViewHolder holder, FriendBodyValue recommandBodyValue, int position) {
            RelativeLayout videoGroup = holder.getView(R.id.video_layout);
            VideoContext videoContext = new VideoContext(videoGroup, recommandBodyValue.videoUrl);
            holder.setText(R.id.fansi_view, recommandBodyValue.fans + "粉丝");
            holder.setText(R.id.name_view, recommandBodyValue.name + " 分享视频");
            holder.setText(R.id.text_view, recommandBodyValue.text);
            holder.setOnClickListener(R.id.guanzhu_view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!LoginServiceWrapper.getInstance().hasLogin()) {
                        //goto login
                        LoginServiceWrapper.getInstance().login(mContext);
                    }
                }
            });
            ImageView avatar = holder.getView(R.id.photo_view);
            ImageLoaderManager.getInstance().displayImageForCircle(avatar, recommandBodyValue.avatr);
        }
    }

}


