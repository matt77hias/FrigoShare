package com.frigoshare.leftover.filtering;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.user.UserTools;
import com.frigoshare.user.Visibility;
import com.frigoshare.utils.Filter;

public class VisibilityFilter implements Filter<Leftover> {

    // This visibility filter checks NOT if the requester is allowed
    // to see the offerers offers.
    // This visibility filter is used by the requester to filter
    // his fetched offers (which he is allowed to see).

    public Visibility getVisibility() {
        return visibility;
    }

    private final Visibility visibility;

    public VisibilityFilter(Visibility visibility) {
        if (visibility == null) {
            visibility = Visibility.ALL;
        }
        this.visibility = visibility;
    }

    @Override
    public boolean accept(Leftover leftover) {
        if (UserTools.getCurrentUser() == null) {
            return false;
        }
        return VisibilityUtils.isVisible(getVisibility(), leftover.getOffererInfo());
    }
}
