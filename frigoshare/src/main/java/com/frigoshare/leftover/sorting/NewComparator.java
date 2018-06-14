package com.frigoshare.leftover.sorting;

import com.frigoshare.endpoint.model.Leftover;

import java.util.Comparator;

public class NewComparator implements Comparator<Leftover> {

    public NewComparator() {
    }

    @Override
    public int compare(Leftover lhs, Leftover rhs) {
        long l =  lhs.getOfferTimestamp().getValue();
        long r =  rhs.getOfferTimestamp().getValue();
        return Double.compare(r, l);
    }
}
