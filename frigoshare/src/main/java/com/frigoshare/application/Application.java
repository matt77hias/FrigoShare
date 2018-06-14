package com.frigoshare.application;

import android.content.pm.ApplicationInfo;

import com.frigoshare.tracking.GaTracker;

public final class Application extends android.app.Application {

    private static Application instance;

    public static Application get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        GaTracker.initAnalytics(this);
    }

    // Debug

    public boolean isDebug() {
        return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}