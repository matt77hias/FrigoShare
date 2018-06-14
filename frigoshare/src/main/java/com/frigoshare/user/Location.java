package com.frigoshare.user;

import com.frigoshare.endpoint.model.Address;

public class Location {

    private static final Location currentLocation = (new Location())
                                                        .setLongitude(0d)
                                                        .setLatitude(0d);

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    public static Address getCurrentAddress() {
        return new Address()
                .setLongitude(getCurrentLocation().getLongitude())
                .setLatitude(getCurrentLocation().getLatitude())
                .setPostalCode(getCurrentLocation().getPostalCode())
                .setCountryName(getCurrentLocation().getCountryName());
    }

    private Object l = new Object();
    private String countryName;
    private String postalCode;
    private double longitude;
    private double latitude;

    protected Location() {

    }

    public String getCountryName() {
        synchronized(this.l) {
            return this.countryName;
        }
    }

    public Location setCountryName(String countryName) {
        synchronized(this.l) {
            this.countryName = countryName;
        }
        return this;
    }

    public String getPostalCode() {
        synchronized(this.l) {
            return this.postalCode;
        }
    }

    public Location setPostalCode(String postalCode) {
        synchronized(this.l) {
            this.postalCode = postalCode;
        }
        return this;
    }

    public double getLongitude() {
        synchronized(this.l) {
            return this.longitude;
        }
    }

    public Location setLongitude(double longitude) {
        synchronized(this.l) {
            this.longitude = longitude;
        }
        return this;
    }

    public double getLatitude() {
        synchronized(this.l) {
            return latitude;
        }
    }

    public Location setLatitude(double latitude) {
        synchronized(this.l) {
            this.latitude = latitude;
        }
        return this;
    }
}
