package com.frigoshare.tracking;

import android.content.Context;

import com.frigoshare.R;
import com.frigoshare.application.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

public class GaTracker {

    private static GaTracker gaTracker;

    protected static GaTracker get() {
        return gaTracker;
    }

    public static void initAnalytics(Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        analytics.setDryRun(Application.get().isDebug());
        gaTracker = new GaTracker(analytics.newTracker(R.xml.tracker));
    }

    private GaTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    // Google Analytics tracker

    private final Tracker tracker;

    protected Tracker getTracker() {
        return tracker;
    }

    protected void trackException(Throwable e) {
        getTracker().send(new HitBuilders.ExceptionBuilder()
                        .setDescription(new StandardExceptionParser(Application.get(), null)
                                .getDescription(Thread.currentThread().getName(), e))
                        .build()
        );
    }

    protected void trackEvent(String category, String action) {
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    protected void trackEvent(String category, String action, String label) {
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    protected void trackEvent(String category, String action, long value) {
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setValue(value)
                .build());
    }

    protected void trackEvent(String category, String action, String label, long value) {
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());
    }

    public static void track(Throwable e) {
        get().trackException(e);
    }

    public static void track(Category category, Action action) {
        get().trackEvent(category.toString(), action.toString());
    }

    public static void track(Category category, Action action, String label) {
        get().trackEvent(category.toString(), action.toString(), label);
    }

    public static void track(Category category, Action action, long value) {
        get().trackEvent(category.toString(), action.toString(), value);
    }

    public static void track(Category category, Action action, String label, long value) {
        get().trackEvent(category.toString(), action.toString(), label, value);
    }
}
