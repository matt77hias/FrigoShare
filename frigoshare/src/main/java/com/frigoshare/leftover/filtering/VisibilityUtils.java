package com.frigoshare.leftover.filtering;

import com.frigoshare.endpoint.model.User;
import com.frigoshare.endpoint.model.UserInfo;
import com.frigoshare.user.UserTools;
import com.frigoshare.user.Visibility;

import java.util.List;

public final class VisibilityUtils {

    public static boolean isVisible(Visibility visibility, UserInfo offerer) {
        switch (visibility) {
            case ALL:
                return true;
            case FRIENDS:
                return isFriend(offerer);
            case FRIENDS_OF_FRIENDS:
                return isFriendOfFriend(offerer);
            default:
                return false;
        }
    }

    public static boolean isFriend(UserInfo offerer) {
        return offerer.getContacts().contains(UserTools.getCurrentUser().getId());
    }

    public static boolean isFriendOfFriend(UserInfo offerer) {
        String regId = UserTools.getCurrentUser().getId();
        String offId = offerer.getId();
        if (regId.equals(offId)) {                  //check offerer vs requester
            return true;
        }
        List<String> oids = offerer.getContacts();
        List<String> rids = UserTools.getCurrentUser().getContacts();

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
