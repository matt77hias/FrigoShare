package com.frigoshare.proxy;

import android.os.AsyncTask;
import android.util.Log;

import com.frigoshare.endpoint.model.User;
import com.frigoshare.tracking.GaTracker;

import java.io.IOException;

public final class UserProxy {


    public static void insert(User user) throws IOException {
        try {
            Proxy.getUserEndpoint().insertUser(user).execute();
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                Log.d("DataStore_Insert_Error", message);
            }
            GaTracker.track(e);
            throw e;
        }
    }

    public static void update(User user) throws IOException {
        try {
            Proxy.getUserEndpoint().updateUser(user).execute();
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                Log.d("DataStore_Update_Error", message);
            }
            GaTracker.track(e);
            throw e;
        }
    }

    public static void delete(String id) throws IOException {
        try {
            Proxy.getUserEndpoint().deleteUser(id).execute();
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                Log.d("DataStore_Delete_Error", message);
            }
            GaTracker.track(e);
            throw e;
        }
    }

    public static void delete(User user) throws IOException {
        delete(user.getInfo().getId());
    }

    public static void delete(com.frigoshare.user.User user) throws IOException {
        delete(user.getId());
    }

    public static User get(String id) throws IOException {
        try {
            return Proxy.getUserEndpoint().getUser(id).execute();
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                Log.d("DataStore_Get_Error", message);
            }
            GaTracker.track(e);
            throw e;
        }
    }

    public static com.frigoshare.user.User getUser(String id) throws IOException {
        return new com.frigoshare.user.User(get(id));
    }

    public static InsertTask createInsertTask() {
        return new InsertTask();
    }

    public static class InsertTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... params) {
            for (User user : params) {
                try {
                    insert(user);
                } catch (IOException e) {}
            }
            return null;
        }
    }

    public static UpdateTask createUpdateTask() {
        return new UpdateTask();
    }

    public static class UpdateTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... params) {
            for (User user : params) {
                try {
                    update(user);
                } catch (IOException e) {}
            }
            return null;
        }
    }

    public static DeleteTask createDeleteTask() {
        return new DeleteTask();
    }

    public static class DeleteTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... params) {
            for (User user : params) {
                try {
                    delete(user);
                } catch (IOException e) {}
            }
            return null;
        }
    }
}
