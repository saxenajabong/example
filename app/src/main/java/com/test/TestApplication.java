package com.test;

import android.app.Application;

import com.test.volley.VolleyTest;

/**
 * Runs at the start of the application and is responsible for initializing
 * the queues, ImageLoader, and image cache.
 */
public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyTest.getInstance(this);
    }
}
