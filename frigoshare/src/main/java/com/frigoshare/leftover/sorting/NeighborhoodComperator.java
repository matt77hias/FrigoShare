package com.frigoshare.leftover.sorting;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.utils.MathUtils;

import java.util.Comparator;

public class NeighborhoodComperator implements Comparator<Leftover> {

    private final double longitude;
    private final double latitude;

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public NeighborhoodComperator(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public int compare(Leftover lhs, Leftover rhs) {
        double dl = MathUtils.distance(lhs.getAddress().getLongitude(), lhs.getAddress().getLatitude(), getLongitude(), getLatitude());
        double dr = MathUtils.distance(rhs.getAddress().getLongitude(), rhs.getAddress().getLatitude(), getLongitude(), getLatitude());
        return Double.compare(dl, dr);
    }
}
