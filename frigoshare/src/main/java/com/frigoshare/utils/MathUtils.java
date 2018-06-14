package com.frigoshare.utils;

public final class MathUtils {

    public static double distance(double lo1, double la1, double lo2, double la2) {
        Double latDistance = toRad(la1-la2);
        Double lonDistance = toRad(lo1-lo2);
        Double a =  Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(toRad(la2)) * Math.cos(toRad(la1)) *
                    Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private static final int R = 6378; // in km

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }
}


