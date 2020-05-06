package com.marco.lib_image_loder;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ImageLoaderManager {


    private ImageLoaderManager() {

    }

    private static class Holder {
        private final static ImageLoaderManager INSTANCE = new ImageLoaderManager();
    }

    public static ImageLoaderManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 为ImageView加载图片
     *
     * @param imageView
     * @param url
     */
    public void displayImageForView(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOptions())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(imageView);
    }

    /**
     * 为ImageView加载圆形图片
     *
     * @param imageView
     * @param url
     */
    public void displayImageForCircle(final ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOptions())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(imageView.getResources(), resource);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }

    /**
     * 为ViewGroup添加背景
     *
     * @param group
     * @param url
     */
    public void displayImageForViewGroup(final ViewGroup group, String url) {
        Glide.with(group.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOptions())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        final Bitmap bitmap = resource;
                        Observable.just(resource)
                                .map(new Function<Bitmap, Drawable>() {
                                    @Override
                                    public Drawable apply(Bitmap bitmap) throws Exception {
                                        Drawable drawable = new BitmapDrawable(Utils.doBlur(bitmap, 100, true));
                                        return drawable;
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Drawable>() {
                                    @Override
                                    public void accept(Drawable drawable) throws Exception {
                                        group.setBackground(drawable);
                                    }
                                });
                    }
                });
    }

    public void displayImageForNotification(Context context, int viewId, RemoteViews rv, Notification notification, int notificationId, String url) {
        NotificationTarget target = initNotificationTarget(context, viewId, rv, notification, notificationId);
        this.displayImageForTarget(context, target, url);
    }

    /**
     * 构造一个NotificationTarget
     *
     * @param context
     * @param viewId
     * @param rv
     * @param notification
     * @param notificationId
     * @return
     */
    private NotificationTarget initNotificationTarget(Context context, int viewId, RemoteViews rv, Notification notification, int notificationId) {
        return new NotificationTarget(context, viewId, rv, notification, notificationId);
    }

    /**
     * 为非View加载图片
     *
     * @param context
     * @param target
     * @param url
     */
    private void displayImageForTarget(Context context, Target target, String url) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOptions())
                .transition(BitmapTransitionOptions.withCrossFade())
                .fitCenter()
//                .listener(customRequestListener)
                .into(target);
    }

    private RequestOptions initCommonRequestOptions() {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.b4y)
                .error(R.mipmap.b4y)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL);
        return options;
    }
}
