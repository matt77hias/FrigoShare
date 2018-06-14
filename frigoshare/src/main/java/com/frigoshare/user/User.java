package com.frigoshare.user;

import android.os.AsyncTask;
import android.util.Log;

import com.frigoshare.cache.ProfilePictureCache;
import com.frigoshare.endpoint.model.Address;
import com.frigoshare.endpoint.model.BirthDate;
import com.frigoshare.endpoint.model.JsonMap;
import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.endpoint.model.UserInfo;
import com.frigoshare.proxy.UserProxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class User {

    protected final Object u = new Object();

    // Cache

    protected com.frigoshare.endpoint.model.User user;

    protected com.frigoshare.endpoint.model.User getUser() {
        synchronized (this.u) {
            return this.user;
        }
    }

    protected void setUser(com.frigoshare.endpoint.model.User user) {
        boolean success = (user != null);
        if (success) {
            synchronized (this.u) {
                this.user = user;
            }
            ProfilePictureCache.getCache().refresh();
        }
        notifyListeners(success);
    }

    protected void notifyListeners(boolean success) {
        synchronized (this.l) {
            if (isListening()) {
                for (UserCacheListener listener : this.listeners) {
                    listener.onUserFetched(success);
                }
            }
        }
    }

    // Network

    public void push() {
        synchronized (this.u) {
            // This is to avoid a strange bug
            // we need to use the wrapped user due to the problems with empty lists.
            List<Leftover> pcs = getUser().getPendingClaims();
            List<Leftover> pcos = getUser().getPendingClaimedOffers();
            List<Leftover> pncos = getUser().getPendingNonClaimedOffers();

            getUser().setPendingClaims(null);
            getUser().setPendingClaimedOffers(null);
            getUser().setPendingNonClaimedOffers(null);

            UserProxy.createInsertTask().execute(getUser());

            getUser().setPendingClaims(pcs);
            getUser().setPendingClaimedOffers(pcos);
            getUser().setPendingNonClaimedOffers(pncos);
        }
    }

    protected void update() {
        UserProxy.createUpdateTask().execute(getUser());
    }

    protected void delete()  {
        UserProxy.createDeleteTask().execute(getUser());
    }

    public void refresh() {
        createRefreshTask().execute();
    }

    protected RefreshTask createRefreshTask() {
        return new RefreshTask();
    }

    protected class RefreshTask extends AsyncTask<Void, Void, com.frigoshare.endpoint.model.User> {

        @Override
        protected com.frigoshare.endpoint.model.User doInBackground(Void... params) {
            try {
                return UserProxy.get(getId());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(com.frigoshare.endpoint.model.User result) {
            setUser(result);
            Log.d("DATA_Fetch", "Pending claims fetched: " + getPendingClaims().size());
            Log.d("DATA_Fetch", "Pending claimed offers fetched: " + getPendingClaimedOffers().size());
            Log.d("DATA_fetch", "Pending non-claimed offers fetched: " + getPendingNonClaimedOffers().size());
        }
    }

    // Constructor

    public User() {
        this((new com.frigoshare.endpoint.model.User()).setInfo(new UserInfo()));
    }

    public User(com.frigoshare.endpoint.model.User user) {
        this.user = user;
    }

    // Data

    public String getId() {
        synchronized (this.u) {
            return getUser().getInfo().getId();
        }
    }

    protected User setId(String id) {
        synchronized (this.u) {
            getUser().getInfo().setId(id);
        }
        return this;
    }

    public String getValidatedId() {
        synchronized (this.u) {
            return getUser().getInfo().getValidatedId();
        }
    }

    protected User setValidatedId(String validatedId) {
        synchronized (this.u) {
            getUser().getInfo().setValidatedId(validatedId);
        }
        return this;
    }

    public String getProvider() {
        synchronized (this.u) {
            return getUser().getInfo().getProvider();
        }
    }

    protected User setProvider(String provider) {
        synchronized (this.u) {
            getUser().getInfo().setProvider(provider);
        }
        return this;
    }

    public String getFirstName() {
        synchronized (this.u) {
            return getUser().getInfo().getFirstName();
        }
    }

    protected User setFirstName(String firstName) {
        synchronized (this.u) {
            getUser().getInfo().getFirstName();
        }
        return this;
    }

    public String getLastName() {
        synchronized (this.u) {
            return getUser().getInfo().getLastName();
        }
    }

    protected User setLastName(String lastName) {
        synchronized (this.u) {
            getUser().getInfo().setLastName(lastName);
        }
        return this;
    }

    public String getEmail() {
        synchronized (this.u) {
            return getUser().getInfo().getEmail();
        }
    }

    protected User setEmail(String email) {
        synchronized (this.u) {
            getUser().getInfo().setEmail(email);
        }
        return this;
    }

    public String getDisplayName() {
        synchronized (this.u) {
            return getUser().getInfo().getDisplayName();
        }
    }

    protected User setDisplayName(String displayName) {
        synchronized (this.u) {
            getUser().getInfo().setDisplayName(displayName);
        }
        return this;
    }

    public String getCountry() {
        synchronized (this.u) {
            return getUser().getInfo().getCountry();
        }
    }

    protected User setCountry(String country) {
        synchronized (this.u) {
            getUser().getInfo().setCountry(country);
        }
        return this;
    }

    public String getLanguage() {
        synchronized (this.u) {
            return getUser().getInfo().getLanguage();
        }
    }

    protected User setLanguage(String language) {
        synchronized (this.u) {
            getUser().getInfo().setLanguage(language);
        }
        return this;
    }

    public String getFullName() {
        synchronized (this.u) {
            return getUser().getInfo().getFullName();
        }
    }

    protected User setFullName(String fullName) {
        synchronized (this.u) {
            getUser().getInfo().setFullName(fullName);
        }
        return this;
    }

    public BirthDate getDob() {
        synchronized (this.u) {
            return getUser().getInfo().getDob();
        }
    }

    protected User setDob(BirthDate dob) {
        synchronized (this.u) {
            getUser().getInfo().setDob(dob);
        }
        return this;
    }

    public String getGender() {
        synchronized (this.u) {
            return getGender();
        }
    }

    protected User setGender(String gender) {
        synchronized (this.u) {
            getUser().getInfo().setGender(gender);
        }
        return this;
    }

    public String getLocation() {
        synchronized (this.u) {
            return getUser().getInfo().getLocation();
        }
    }

    protected User setLocation(String location) {
        synchronized (this.u) {
            getUser().getInfo().setLocation(location);
        }
        return this;
    }

    public String getProfileImageURL() {
        synchronized (this.u) {
            return getUser().getInfo().getProfileImageURL();
        }
    }

    protected User setProfileImageURL(String profileImageURL) {
        synchronized (this.u) {
            getUser().getInfo().setProfileImageURL(profileImageURL);
        }
        return this;
    }

    public JsonMap getContactInfo() {
        synchronized (this.u) {
            return getUser().getInfo().getContactInfo();
        }
    }

    protected User setContactInfo(JsonMap contactInfo) {
        synchronized (this.u) {
            getUser().getInfo().setContactInfo(contactInfo);
        }
        return this;
    }

    public List<String> getContacts() {
        synchronized (this.u) {
            if (getUser().getInfo().getContacts() == null) {
                return new ArrayList<String>();
            }
            return Collections.unmodifiableList(getUser().getInfo().getContacts());
        }
    }

    protected User setContacts(List<String> contacts) {
        if (contacts != null) {
            Collections.sort(contacts);
            synchronized (this.u) {
                getUser().getInfo().setContacts(contacts);
            }
        }
        return this;
    }

    // Leftovers

    public List<Leftover> getPendingClaims() {
        synchronized (this.u) {
            if (getUser().getPendingClaims() == null) {
                return new ArrayList<Leftover>();
            }
            return Collections.unmodifiableList(getUser().getPendingClaims());
        }
    }

    public List<Leftover> getPendingClaimedOffers() {
        synchronized (this.u) {
            if (getUser().getPendingClaimedOffers() == null) {
                return new ArrayList<Leftover>();
            }
            return Collections.unmodifiableList(getUser().getPendingClaimedOffers());
        }
    }

    public List<Leftover> getPendingNonClaimedOffers() {
        synchronized (this.u) {
            if (getUser().getPendingNonClaimedOffers() == null) {
                return new ArrayList<Leftover>();
            }
            return Collections.unmodifiableList(getUser().getPendingNonClaimedOffers());
        }
    }

    // Options

    public Address getAddress() {
        synchronized (this.u) {
            return getUser().getAddress();
        }
    }

    public User setAddress(Address address) {
        synchronized (this.u) {
            getUser().setAddress(address);
        }
        return this;
    }

    public String getVisibilityString() {
        synchronized (this.u) {
            return getUser().getVisibility();
        }
    }

    public Visibility getVisibility() {
        synchronized (this.u) {
            return Visibility.convert(getUser().getVisibility());
        }
    }

    public User setVisibility(String visibility) {
        synchronized (this.u) {
            getUser().setVisibility(visibility);
        }
        return this;
    }

    public User setVisibility(Visibility visibility) {
        synchronized (this.u) {
            getUser().setVisibility(visibility.toString());
        }
        return this;
    }

    // Google Cloud Messaging

    public List<String> getRegIds() {
        synchronized (this.u) {
            if (getUser().getRegIds() == null) {
                return new ArrayList<String>();
            }
            return Collections.unmodifiableList(getUser().getRegIds());
        }
    }

    protected User setRegIds(List<String> regIds) {
        if (regIds != null) {
            synchronized (this.u) {
                getUser().setRegIds(regIds);
            }
        }
        return this;
    }

    // Listeners service

    private List<UserCacheListener> listeners = new LinkedList<UserCacheListener>();

    protected final Object l = new Object();

    public void addListener(UserCacheListener listener) {
        synchronized (this.l) {
            this.listeners.add(listener);
        }
    }

    public void removeListener(UserCacheListener listener) {
        synchronized (this.l) {
            this.listeners.remove(listener);
        }
    }

    private boolean isListening = true;

    public boolean isListening() {
        return this.isListening;
    }

    public void startListening() {
        synchronized (this.l) {
            this.isListening = true;
        }
    }

    public void stopListeningService() {
        synchronized (this.l) {
            this.isListening = false;
        }
    }

    public void cleanListenersService() {
        synchronized (this.l) {
            this.listeners = new LinkedList<UserCacheListener>();
        }
    }

    @Override
    public boolean equals(Object object){
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        User other = (User) object;
        return getId().equals(other.getId());
    }
}

