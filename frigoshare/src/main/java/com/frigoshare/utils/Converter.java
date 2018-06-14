package com.frigoshare.utils;

import com.frigoshare.endpoint.model.Address;
import com.frigoshare.endpoint.model.BirthDate;
import com.google.api.client.util.DateTime;
import com.frigoshare.endpoint.model.JsonMap;

import java.util.Map;

public final class Converter {

    public static DateTime convert(java.util.Date d) {
        return new DateTime(d);
    }

    public static BirthDate convert(org.brickred.socialauth.util.BirthDate d) {
        BirthDate dob = new BirthDate();
        if (d != null) {
            dob
                    .setDay(d.getDay())
                    .setMonth(d.getMonth())
                    .setYear(d.getYear());
        }
        return dob;
    }

    public static JsonMap convert(java.util.Map<String, String> m) {
        JsonMap map = new JsonMap();
        if (m != null) {
            for (Map.Entry<String, String> entry : m.entrySet()) {
                map.set(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    public static Address convert(android.location.Address a) {
        Address address = new Address();
        if (a != null) {
            address
                    .setCountryName(a.getCountryName())
                    .setPostalCode(a.getPostalCode())
                    .setLongitude(a.getLongitude())
                    .setLatitude(a.getLatitude());
        }
        return address;
    }
}
