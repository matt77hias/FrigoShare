package com.frigoshare.utils;

import com.frigoshare.data.Leftover;
import com.frigoshare.data.User;

public class VisibilityFilter implements Filter<Leftover> {

    // This visibility filter checks if the requester is allowed
    // to see the offerers offers.

    private final User requester;

    public User getRequester() {
        return requester;
    }

    public VisibilityFilter(User requester) {
        this.requester = requester;
    }

    @Override
    public boolean accept(Leftover leftover) {
        if (getRequester() == null) {
            return false;
        }
        return VisibilityUtils.isVisible(getRequester(), leftover.getOfferer());
    }
}
