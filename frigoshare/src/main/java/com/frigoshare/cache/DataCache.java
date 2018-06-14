package com.frigoshare.cache;

import android.os.AsyncTask;
import android.util.Log;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.proxy.LeftoverProxy;
import com.frigoshare.user.UserTools;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class DataCache {

    public DataCache() {

    }

    private Collection<Leftover> unclaimedLeftovers = new HashSet<Leftover>();

    // To sync on
    protected final Object u = new Object();
    protected final Object l = new Object();

    // Getters

    public Collection<Leftover> getUnclaimedLeftOvers() {
        synchronized (this.u) {
            return Collections.unmodifiableCollection(this.unclaimedLeftovers);
        }
    }

    // Cleaning

    public void clean() {
        cleanUnclaimedLeftovers();
    }

    public void cleanUnclaimedLeftovers() {
        setUnclaimedLeftovers(new HashSet<Leftover>());
    }

    // Fetching

    public void fetch() {
        fetchUnclaimedLeftovers();
    }

    public void fetchUnclaimedLeftovers() {
        createUnclaimedLeftoversTask().execute();
    }

    // Tasks

    private UnclaimedLeftoversTask createUnclaimedLeftoversTask() {
        return new UnclaimedLeftoversTask();
    }

    private class UnclaimedLeftoversTask extends AsyncTask<Void, Void, Collection<Leftover>> {

        @Override
        protected Collection<Leftover> doInBackground(Void... params) {
            try {
                return LeftoverProxy.getAllUnclaimedLeftovers(UserTools.getCurrentUser());
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Collection<Leftover> result) {
            setUnclaimedLeftovers(result);
            Log.d("DATA_Fetch", "Unclaimed leftovers fetched: " + getUnclaimedLeftOvers().size());
        }
    }

    // Setters

    private void setUnclaimedLeftovers(Collection<Leftover> unclaimedLeftovers) {
        boolean success = (unclaimedLeftovers != null);
        if (success) {
            synchronized (this.u) {
                this.unclaimedLeftovers = unclaimedLeftovers;
            }
            ProfilePictureCache.getCache().refresh();
        }

        synchronized (this.l) {
            if (isListening()) {
                for (DataCacheListener listener : this.listeners) {
                    listener.onUnclaimedLeftoversFetched(success);
                }
            }
        }
    }

    // Listeners service

    private List<DataCacheListener> listeners = new LinkedList<DataCacheListener>();

    public void addListener(DataCacheListener listener) {
        synchronized (this.l) {
            this.listeners.add(listener);
        }
    }

    public void removeListener(DataCacheListener listener) {
        synchronized (this.l) {
            this.listeners.remove(listener);
        }
    }

    private boolean isListening = true;

    public boolean isListening() {
        return this.isListening;
    }

    public void startListeningService() {
        synchronized (this.l) {
            this.isListening = true;
        }
    }

    public void stopListeningService() {
        synchronized (this.l) {
            this.isListening = false;
        }
    }

    public void cleanListeners() {
        synchronized (this.l) {
            this.listeners = new LinkedList<DataCacheListener>();
        }
    }
}
