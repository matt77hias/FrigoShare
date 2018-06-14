package com.frigoshare.data;

import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.EmbedMap;
import com.googlecode.objectify.annotation.Ignore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Embed
public class UserInfo {

    private String id;
    private String validatedId;
    private String provider;
    private String firstName;
    private String lastName;
    private String email;
    private String displayName;
    private String country;
    private String language;
    private String fullName;
    private BirthDate dob;
    private String gender;
    private String location;
    private String profileImageURL;

    @EmbedMap
    private Map<String, String> contactInfo;

    // User friends info
    private List<String> contacts; // just ids


    public UserInfo() {

    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValidatedId() {
        return this.validatedId;
    }

    public void setValidatedId(String validatedId) {
        this.validatedId = validatedId;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public BirthDate getDob() {
        return this.dob;
    }

    public void setDob(BirthDate dob) {
        this.dob = dob;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfileImageURL() {
        return this.profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public Map<String, String> getContactInfo() {
        return this.contactInfo;
    }

    public void setContactInfo(Map<String, String> contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<String> getContacts() {
        if (this.contacts == null) {
            this.contacts = new ArrayList<String>();
        }
        return this.contacts;
    }

    public void setContacts(List<String> contacts) {
        if (contacts != null) {
            Collections.sort(contacts);
            this.contacts = contacts;
        }
    }
}
