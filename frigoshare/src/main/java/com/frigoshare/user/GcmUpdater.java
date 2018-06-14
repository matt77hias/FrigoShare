package com.frigoshare.user;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GcmUpdater {

    public static String GCM_SENDER_ID = "395783934311"; //project id

    public static GCMTask createGCMTask() {
        return new GCMTask();
    }

    public static class GCMTask extends AsyncTask<Context, Void, Void> {

        @Override
        protected Void doInBackground(Context... context) {
            if (context.length != 0) {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context[0]);
                try {
                    UserTools.update(gcm.register(GCM_SENDER_ID));
                } catch (IOException e) {
                    e.printStackTrace();
                    UserTools.update(null);
                }
            }
            return null;
        }
    }
}
