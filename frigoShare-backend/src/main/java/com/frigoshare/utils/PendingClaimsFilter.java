package com.frigoshare.utils;

import com.frigoshare.data.Leftover;
import com.frigoshare.data.User;

public class PendingClaimsFilter implements Filter<Leftover> {

    private final User reference;

    public User getReference() {
        return reference;
    }

    public String getReferenceId() {
        return getReference().getInfo().getId();
    }

    public PendingClaimsFilter(User reference) {
        this.reference = reference;
    }

    @Override
    public boolean accept(Leftover leftover) {
        if (leftover == null) {
            return false;
        }
        return leftover.isValid() && getReferenceId().equals(leftover.getClaimerInfo().getId());
    }
}
