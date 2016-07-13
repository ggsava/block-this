package com.savageorgiev.blockthis;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
            // todo Timber.plant(new Timber.DebugTree());
        }
    }
}
