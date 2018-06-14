package com.frigoshare.endpoints;

import com.frigoshare.data.Address;
import com.frigoshare.data.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import static com.frigoshare.OfyService.ofy;

@Api(name = "endpoint", version = "v1", namespace = @ApiNamespace(ownerDomain = "frigoshare.com", ownerName = "frigoshare.com", packagePath=""))
public final class UserEndpoint {

    public UserEndpoint() {
    }

    @ApiMethod(name = "Users.insertUser")
    public void iUser(User user) {
        user.setId(user.getInfo().getId());
        ofy().save().entity(user).now();
    }

    @ApiMethod(name = "Users.updateUser")
    public void uUserPreferOldest(User user) {
        iUser(updatePreferences(user, gUser(user.getInfo().getId())));
    }

    private static User updatePreferences(User newUser, User oldUser) {
        if (oldUser != null) {
            //Merge address
            newUser.setAddress(oldUser.getAddress());
            //Merge visibility
            newUser.setVisibility(oldUser.getVisibility());
            //Merge reg ids
            List<String> nrs = newUser.getRegIds();
            if (nrs == null) { nrs = new ArrayList<String>(); }
            for (String r : nrs) { oldUser.addRegId(r); }
            newUser.setRegIds(oldUser.getRegIds());
        }
        return newUser;
    }

    @ApiMethod(httpMethod = "GET", name = "Users.getUser")
    public User gUser(@Named("id") String id) {
        return ofy().load().type(User.class).id(id).now();
    }

    @ApiMethod(name = "Users.deleteUser")
    public void dUser(@Named("id") String id) {
        ofy().delete().type(User.class).id(id);
    }
}
