package com.williammora.openfeed;

import android.app.Application;

import com.williammora.openfeed.services.TwitterService;

public class OpenFeed extends Application {

    private static OpenFeed application;

    public OpenFeed() {
        application = this;
    }

    public static OpenFeed getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterService.getInstance().init();
    }
}
