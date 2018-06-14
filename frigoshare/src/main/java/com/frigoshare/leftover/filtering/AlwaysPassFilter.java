package com.frigoshare.leftover.filtering;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.utils.Filter;

public class AlwaysPassFilter implements Filter<Leftover> {

    public AlwaysPassFilter() {
    }

    @Override
    public boolean accept(Leftover leftover) {
        return true;
    }
}
