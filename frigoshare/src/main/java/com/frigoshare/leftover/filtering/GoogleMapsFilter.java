package com.frigoshare.leftover.filtering;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.utils.Filter;

public class GoogleMapsFilter implements Filter<Leftover> {

    public GoogleMapsFilter() {
    }

    @Override
    public boolean accept(Leftover leftover) {
        return !(leftover.getAddress().getLongitude() == null || leftover.getAddress().getLatitude() == null);
    }
}
