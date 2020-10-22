package com.covrsecurity.io.app;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.covrsecurity.io.BuildConfig;

@GlideModule
public class CovrGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(Log.VERBOSE);
        }
    }
}
