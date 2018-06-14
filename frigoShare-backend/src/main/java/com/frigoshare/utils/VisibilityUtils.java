package com.frigoshare.utils;

import com.frigoshare.data.User;

import java.util.List;

public final class VisibilityUtils {

    public static boolean isVisible(User requester, User offerer) {
        switch (offerer.getVisibility()) {
            case ALL:
                return true;
            case FRIENDS:
                return isFriendOf(requester, offerer);
            case FRIENDS_OF_FRIENDS:
                return isFriendOfFriend(requester, offerer);
            default:
                return false;
        }
    }

    public static boolean isFriendOf(User requester, User offerer) {
        return requester.getInfo().getId().equals(offerer.getInfo().getId()) || offerer.getInfo().getContacts().contains(requester.getInfo().getId());
    }

    public static boolean isFriendOfFriend(User requester, User offerer) {
        String regId = requester.getInfo().getId();
        String offId = offerer.getInfo().getId();
        if (regId.equals(offId)) {                  //check offerer vs requester
            return true;
        }
        List<String> oids = offerer.getInfo().getContacts();
        List<String> rids = requester.getInfo().getContacts();

        // optimization possible if
        // oids.size() >> rids.size()
        // oids.size() << rids.size()

        int k=0;
        for (int i=0; i<oids.size(); i++) {         //sorted (ascend order)
            String o = oids.get(i);
            for (int j=k; j<rids.size(); j++) {     //sorted (ascend order)
                String r = rids.get(i);
                if (o.equals(regId)) {              //check offerer friend vs requester
                    return true;
                } else if (offId.equals(r)) {       //check offerer vs requester friend
                    return true;
                } else if (o.equals(r)) {           //check offerer friend vs requester friend
                    return true;
                } else if (o.compareTo(r) > 0) {
                    k=j+1;
                }
            }
        }
        return false;
    }
}
