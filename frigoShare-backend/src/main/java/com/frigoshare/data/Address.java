package com.frigoshare.data;

import com.googlecode.objectify.annotation.Embed;


@Embed
public class Address implements Comparable<Address> {

    private double latitude;
    private double longitude;
    private String address;
    private String postalCode;
    private String countryName;

    public Address() {

    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public int compareTo(Address o) {
        return getAddress().compareTo(o.getAddress());
    }
}
