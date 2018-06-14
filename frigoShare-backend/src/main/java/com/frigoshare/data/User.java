package com.frigoshare.data;

import com.frigoshare.utils.FilterUtils;
import com.frigoshare.utils.ValidFilter;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.LinkedList;
import java.util.List;

import static com.frigoshare.OfyService.ofy;

@Entity
public class User {

    // User profile info
    @Id
    private String id;
    private UserInfo info;

    // Options
    private Address address;
    private Visibility visibility;

    // Google Cloud Messaging
    private List<String> regIds;

    public User() {

    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setId(String id) {
        this.id = id;
    }

    public UserInfo getInfo() {
        return this.info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }

    //Leftovers

    public List<Leftover> getPendingClaims() {
        return FilterUtils.filter(ofy().load().type(Leftover.class)
                .filter("claimer", Ref.create(this))
                .list()
                , new ValidFilter());
    }

    public List<Leftover> getPendingClaimedOffers() {
        return FilterUtils.filter(ofy().load().type(Leftover.class)
                .filter("offerer", Ref.create(this))
                .filter("claimer !=", null)
                .list()
                , new ValidFilter());
    }

    public List<Leftover> getPendingNonClaimedOffers() {
        return FilterUtils.filter(ofy().load().type(Leftover.class)
                .filter("offerer", Ref.create(this))
                .filter("claimer", null)
                .list()
                , new ValidFilter());
    }

    // Options

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Visibility getVisibility() {
        if (this.visibility == null) {
            this.visibility = Visibility.ALL;
        }
        return this.visibility;
    }

    public void setVisibility(Visibility visibility) {
        if (visibility != null) {
            this.visibility = visibility;
        }
    }

    // Google Cloud Messaging

    public List<String> getRegIds() {
        if (this.regIds == null) {
            this.regIds = new LinkedList<String>();
        }
        return this.regIds;
    }

    public void setRegIds(List<String> regIds) {
        if (regIds != null) {
            this.regIds = regIds;
        }
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void addRegId(String regId) {
        if (regId != null) {
            getRegIds().remove(regId);
            getRegIds().add(regId);
        }
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void removeRegId(String regId) {
        getRegIds().remove(regId);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public String getLastRegId() {
        return getRegIds().get(getRegIds().size()-1);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public boolean isReachableByGCM() {
        return (!getRegIds().isEmpty());
    }

    @Override
    public boolean equals(Object object){
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        User other = (User) object;
        return getInfo().getId().equals(other.getInfo().getId());
    }
}
