package com.frigoshare.proxy;

import android.os.AsyncTask;
import android.util.Log;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.endpoint.model.TimeSlot;
import com.frigoshare.endpoint.model.User;
import com.frigoshare.tracking.GaTracker;
import com.frigoshare.user.UserTools;

import java.io.IOException;
import java.util.Collection;

public final class LeftoverProxy {

    public static void insert(Leftover leftover) throws IOException  {
        try {
            Proxy.getLeftoverEndpoint().insertLeftover(UserTools.getCurrentUser().getId(), leftover).execute();
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                Log.d("DataStore_Insert_Error", message);
            }
            GaTracker.track(e);
            throw e;
        }
    }

    public static void delete(Long id) throws IOException {
        try {
            Proxy.getLeftoverEndpoint().deleteLeftover(id).execute();
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                Log.d("DataStore_Delete_Error", message);
            }
            GaTracker.track(e);
            throw e;
        }
    }

    public static void delete(Leftover leftover) throws IOException  {
        delete(leftover.getId());
    }

    public static Leftover get(Long id) throws IOException {
        try {
            return Proxy.getLeftoverEndpoint().getLeftover(id).execute();
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                Log.d("DataStore_Get_Error", message);
            }
            GaTracker.track(e);
            throw e;
        }
    }

    public static void claim(String userId, Long offerId, TimeSlot content)
            throws IOException {
        try {
            Proxy.getLeftoverEndpoint().claimLeftover(userId, offerId, content).execute();
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                Log.d("DataStore_Error", message);
            }
            GaTracker.track(e);
            throw e;
        }
    }

    public static void claim(User user, Leftover leftover, TimeSlot content)
            throws IOException {
        claim(user.getInfo().getId(), leftover.getId(), content);
    }

    public static void claim(com.frigoshare.user.User user, Leftover leftover, TimeSlot content)
            throws IOException {
        claim(user.getId(), leftover.getId(), content);
    }

    public static Collection<Leftover> getAllUnclaimedLeftovers(String userId) throws IOException {
        try {
            return Proxy.getLeftoverEndpoint().getAllUnclaimedLeftovers(userId).execute().getItems();
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                Log.d("DataStore_UnclaimedLeftovers_Error", message);
            }
            GaTracker.track(e);
            throw e;
        }
    }

    public static Collection<Leftover> getAllUnclaimedLeftovers(User user) throws IOException {
        return getAllUnclaimedLeftovers(user.getInfo().getId());
    }

    public static Collection<Leftover> getAllUnclaimedLeftovers(com.frigoshare.user.User user) throws IOException {
        return getAllUnclaimedLeftovers(user.getId());
    }

    public static InsertTask createInsertTask() {
        return new InsertTask();
    }

    public static class InsertTask extends AsyncTask<Leftover, Void, Void> {

        @Override
        protected Void doInBackground(Leftover... params) {
            for (Leftover leftover : params) {
                try {
                    insert(leftover);
                } catch (IOException e) {}
            }
            return null;
        }
    }

    public static DeleteTask createDeleteTask() {
        return new DeleteTask();
    }

    public static class DeleteTask extends AsyncTask<Leftover, Void, Void> {

        @Override
        protected Void doInBackground(Leftover... params) {
            for (Leftover leftover : params) {
                try {
                    delete(leftover);
                } catch (IOException e) {}
            }
            return null;
        }
    }
}
