package com.frigoshare.user;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.utils.Converter;
import com.frigoshare.utils.IdUtils;

import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Profile;

import java.util.ArrayList;
import java.util.List;

public final class UserTools {

    // Sync
    private static final Object p = new Object();
    private static boolean profileFetched = false;
    private static boolean contactsFetched = false;
    private static boolean regIdFetched = false;

    private static boolean readyToPush() {
        return isProfileFetched() && isContactsFetched() && isRegIdFetched();
    }

    private static boolean isProfileFetched() {
        return profileFetched;
    }

    private static void setProfileFetched(boolean profileFetched) {
        UserTools.profileFetched = profileFetched;
    }

    private static boolean isContactsFetched() {
        return contactsFetched;
    }

    private static void setContactsFetched(boolean contactsFetched) {
        UserTools.contactsFetched = contactsFetched;
    }

    private static boolean isRegIdFetched() {
        return regIdFetched;
    }

    private static void setRegIdFetched(boolean regIdFetched) {
        UserTools.regIdFetched = regIdFetched;
    }

    // User

    private static final Object u = new Object();

    private static User currentUser = new User();

    public static User getCurrentUser() {
        return currentUser;
    }

    private static void setCurrentUser(User user) {
        if (user != null) {
            currentUser = user;
        }
    }

    //Use this when you're sure no sync problems could happen
    public static void cleanCurrentUser() {
       setCurrentUser(new User());
       setProfileFetched(false);
       setContactsFetched(false);
       setRegIdFetched(false);
    }

    // Update

    public static void update(String provider, Profile profile) {
        if (profile != null) {
            synchronized (u) {
                getCurrentUser()
                        .setProvider(provider)
                        .setId(IdUtils.getIdPrefix(provider).concat(profile.getValidatedId()))
                        .setValidatedId(profile.getValidatedId())
                        .setProvider(provider)
                        .setFirstName(profile.getFirstName())
                        .setLastName(profile.getLastName())
                        .setEmail(profile.getEmail())
                        .setDisplayName(profile.getDisplayName())
                        .setCountry(profile.getCountry())
                        .setLanguage(profile.getLanguage())
                        .setFullName(profile.getFullName())
                        .setGender(profile.getGender())
                        .setLocation(profile.getLocation())
                        .setProfileImageURL(httpToHttps(provider, profile.getProfileImageURL()))
                        .setDob(Converter.convert(profile.getDob()))
                        .setContactInfo(Converter.convert(profile.getContactInfo()))
                        .setVisibility(Visibility.ALL);
            }

            synchronized (p) {
                setProfileFetched(true);
                update();
            }
        }
    }

    protected static String httpToHttps(String provider, String url) {
        if ("facebook".equals(provider) && url != null) {
            return url.replaceFirst("^http://", "https://");
        } else {
            return url;
        }
    }

    public static void update(String provider, List<Contact> cs) {
        List<String> contacts = new ArrayList<String>();
        if (cs != null) {
            for (Contact contact : cs) {
                if (contact != null) {
                    contacts.add(IdUtils.getIdPrefix(provider).concat(contact.getId()));
                }
            }
            synchronized (u) {
                getCurrentUser().setContacts(contacts);
            }
        }

        synchronized (p) {
            setContactsFetched(true);
            update();
        }
    }

    protected static void update(String regId) {
        getCurrentUser().setRegIds(new ArrayList<String>());

        if (regId != null) {
            List<String> regIds = new ArrayList<String>();
            regIds.add(regId);
            synchronized (u) {
                getCurrentUser().setRegIds(regIds);
            }
        }

        synchronized (p) {
            setRegIdFetched(true);
            update();
        }
    }

    private static void update() {
        synchronized (p) {
            if (readyToPush()) {
                getCurrentUser().update();
            }
        }
    }

    // Leftover

    public static Leftover createLeftover() {
        return new Leftover();
    }
}
