package com.frigoshare.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.frigoshare.R;
import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.user.UserTools;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProfilePictureCache {

    private static ProfilePictureCache cache = new ProfilePictureCache();

    public static ProfilePictureCache getCache() {
        return cache;
    }

    protected ProfilePictureCache() {

    }

    // To sync on
    protected final Object p = new Object();
    private Map<String, Bitmap> icons = new HashMap<String, Bitmap>();

    public Drawable getDrawable(Context context, String url) {
        if (contains(url)) {
            return new BitmapDrawable(context.getResources(), getBitmap(context, url));
        } else {
            refresh();
            return null;
        }
    }

    public Bitmap getBitmap(Context context, String url) {
        synchronized (this.p) {
            if (contains(url)) {
                int dim = context.getResources().getDimensionPixelSize(R.dimen.icon_dim);
                return Bitmap.createScaledBitmap(this.icons.get(url), dim, dim, false);
            } else {
                refresh();
                return null;
            }
        }
    }

    protected void putBitmap(String url, Bitmap bitmap) {
        synchronized (this.p) {
            if (bitmap != null) {
                this.icons.put(url, bitmap);
            }
        }
    }

    protected boolean contains(String url) {
        synchronized (this.p) {
            return this.icons.containsKey(url);
        }
    }

    protected Set<String> gatherURLs() {
        Set<String> urls = new HashSet<String>();
        urls.add(UserTools.getCurrentUser().getProfileImageURL());
        for (Leftover l : UserTools.getCurrentUser().getPendingClaims()) {
            urls.add(l.getOffererInfo().getProfileImageURL());
        }
        for (Leftover l : UserTools.getCurrentUser().getPendingClaimedOffers()) {
            urls.add(l.getClaimerInfo().getProfileImageURL());
        }
        for (Leftover l : DataTools.getCurrentDataCache().getUnclaimedLeftOvers()) {
            urls.add(l.getOffererInfo().getProfileImageURL());
            if (l.getClaimerInfo() != null) {
                urls.add(l.getClaimerInfo().getProfileImageURL());
            }
        }
        return urls;
    }

    public void refresh() {
        try {
            createFetchProfileImages().execute(gatherURLs().toArray(new String[0]));
        } catch (NullPointerException e) {

        }
    }

    private FetchProfileImages createFetchProfileImages() { return new FetchProfileImages(); }

    private class FetchProfileImages extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            for (String url : urls) {
                if (url != null && !contains(url)) {
                    HttpURLConnection httpUrlConnection = null;
                    try {
                        httpUrlConnection = (HttpURLConnection) new URL(url).openConnection();
                        httpUrlConnection.setInstanceFollowRedirects(true);
                        Bitmap icon = BitmapFactory.decodeStream(new SanInputStream(httpUrlConnection.getInputStream()));
                        putBitmap(url, icon);
                    } catch (Exception e) {
                        if (e.getMessage() != null) {
                            Log.e("ICON_Fetch_Error", e.getMessage());
                        }
                        e.printStackTrace();
                    } finally {
                        if (httpUrlConnection != null) {
                            httpUrlConnection.disconnect();
                        }
                    }
                }
            }
            return null;
        }
    }

    // The BitmapFactory.decodeStream() method fails to read a JPEG image (i.e. returns null)
    // if the skip() method of the used FilterInputStream skip less bytes than the required amount.
    protected class SanInputStream extends FilterInputStream {

        public SanInputStream(InputStream in) {
            super(in);
        }

        @Override
        public long skip(long n) throws IOException {
            long m = 0L;
            while (m < n) {
                long _m = in.skip(n-m);
                if (_m == 0L) break;
                m += _m;
            }
            return m;
        }
    }
}
