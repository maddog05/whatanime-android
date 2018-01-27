package com.maddog05.whatanime.core.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.maddog05.maddogutilities.callback.Callback;
import com.maddog05.maddogutilities.image.ImageLoader;

/**
 * Created by andree on 25/09/2017.
 */

public class GlideLoader implements ImageLoader {
    private Context context;
    private Callback<Boolean> callback;
    private int resDrawable;
    private String path;
    private ImageView imageView;

    public static GlideLoader create() {
        return new GlideLoader();
    }

    private GlideLoader() {
        resDrawable = -1;
    }

    @Override
    public ImageLoader with(Context context) {
        this.context = context;
        return this;
    }

    @Override
    public ImageLoader placeholder(@DrawableRes int resDrawable) {
        this.resDrawable = resDrawable;
        return this;
    }

    @Override
    public ImageLoader callback(Callback<Boolean> callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public ImageLoader load(String path) {
        this.path = path;
        return this;
    }

    @Override
    public ImageLoader target(ImageView imageView) {
        this.imageView = imageView;
        return this;
    }

    @Override
    public void start() {
        RequestOptions requestOptions = new RequestOptions();
        if (resDrawable != -1) {
            requestOptions.placeholder(resDrawable);
            requestOptions.error(resDrawable);
        }

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(path)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        if (callback != null)
                            callback.done(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        if (callback != null)
                            callback.done(true);
                        return false;
                    }
                })
                .into(imageView);
    }

    public void loadAsBitmap(final Callback<Bitmap> callback) {
        RequestOptions requestOptions = new RequestOptions();
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .asBitmap()
                .load(path)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        callback.done(resource);
                    }
                });
    }
}
