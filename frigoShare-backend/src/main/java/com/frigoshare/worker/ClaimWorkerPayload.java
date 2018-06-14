package com.frigoshare.worker;

import com.frigoshare.data.TimeSlot;

import java.io.Serializable;

public class ClaimWorkerPayload implements Serializable {

    private static final long serialVersionUID = 5L;

    private String userId;
    private Long offerId;
    private TimeSlot timeSlot;

    public ClaimWorkerPayload() {
    }

    public ClaimWorkerPayload(String userId, Long offerId, TimeSlot timeSlot) {
        setUserId(userId);
        setOfferId(offerId);
        setTimeSlot(timeSlot);
    }

    public String getUserId() {
        return userId;
    }

    public ClaimWorkerPayload setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Long getOfferId() {
        return offerId;
    }

    public ClaimWorkerPayload setOfferId(Long offerId) {
        this.offerId = offerId;
        return this;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public ClaimWorkerPayload setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
        return this;
    }
}
