package com.frigoshare.utils;

import com.frigoshare.data.Leftover;

public class ValidFilter implements Filter<Leftover> {

    public ValidFilter() {

    }

    @Override
    public boolean accept(Leftover leftover) {
        if (leftover == null) {
            return false;
        }
        return leftover.isValid();
    }
}
