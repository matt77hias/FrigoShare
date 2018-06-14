package com.frigoshare.data;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.channel.ChannelService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
public class Leftover implements Comparable<Leftover> {

    @Id
    private Long id;
    @Index @Load
    private Ref<User> offerer;
    private Category category;
    private Description description;
    private Address address;
    private List<TimeSlot> timeslots;
    @Index @Load
    private Ref<User> claimer;
    private TimeSlot claimedSlot;

    private Date offerTimestamp;
    private Date claimTimestamp;

    public Leftover() {

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public User getOfferer() {
        return this.offerer.get();
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setOfferer(User offerer) {
        this.offerer = Ref.create(offerer);
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Description getDescription() {
        return this.description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<TimeSlot> getTimeslots() {
        return this.timeslots;
    }

    public void setTimeslots(List<TimeSlot> timeslots) {
        this.timeslots = timeslots;
        Collections.sort(getTimeslots());
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public User getClaimer() {
        return this.claimer == null ? null : this.claimer.get();
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setClaimer(User claimer) {
        this.claimer = Ref.create(claimer);
    }

    public TimeSlot getClaimedSlot() {
        return this.claimedSlot;
    }

    public void setClaimedSlot(TimeSlot claimedSlot) {
        this.claimedSlot = claimedSlot;
    }

    public Date getOfferTimestamp() {
        return this.offerTimestamp;
    }

    public void setOfferTimestamp(Date offerTimestamp) {
        this.offerTimestamp = offerTimestamp;
    }

    public Date getClaimTimestamp() {
        return this.claimTimestamp;
    }

    public void setClaimTimestamp(Date claimTimestamp) {
        this.claimTimestamp = claimTimestamp;
    }

    public UserInfo getOffererInfo() {
        return getOfferer().getInfo();
    }

    public UserInfo getClaimerInfo() {
        if (isClaimed()) {
            return getClaimer().getInfo();
        } else {
            return null;
        }
    }

    // LOGIC

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void claimOffer(User claimer, TimeSlot timeSlot)
            throws IllegalArgumentException, IllegalStateException {
        if (!isAvailableTimeSlot(timeSlot)) {
            throw new IllegalArgumentException("TimeSlot " + timeSlot + " is not an available time slot of " + getDescription().getName() + '.');
        } else if (isClaimed()) {
            throw new IllegalStateException(getDescription().getName() + " is already claimed.");
        } else if (!isValid()) {
            throw new IllegalStateException(getDescription().getName() + " is already closed.");
        }

        setClaimer(claimer);
        setClaimedSlot(timeSlot);
        setClaimTimestamp(new Date());
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public boolean isAvailableTimeSlot(TimeSlot timeSlot) {
        return getTimeslots().contains(timeSlot);
    }

    public boolean isClaimed() {
        return getClaimer() != null;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public boolean isValid() {
        return getTimeslots().get(getTimeslots().size() - 1).getEnd().after(new Date());
    }

    @Override
    public int compareTo(Leftover o) {
        return getOfferTimestamp().compareTo(o.getOfferTimestamp());
    }
}
