package com.frigoshare.leftover.sorting;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.endpoint.model.TimeSlot;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class LastMinuteComparator implements Comparator<Leftover> {

    private final Date currentDate;

    private Date getCurrentDate() {
        return this.currentDate;
    }

    public LastMinuteComparator() {
        this(new Date());
    }

    public LastMinuteComparator(Date date) {
        this.currentDate = date;
    }

    @Override
    public int compare(Leftover lhs, Leftover rhs) {
        List<TimeSlot> lts = lhs.getTimeslots();
        List<TimeSlot> rts = rhs.getTimeslots();

        long l =  lts.get(lts.size()-1).getEnd().getValue();
        long r = rts.get(rts.size()-1).getEnd().getValue();
        long c = getCurrentDate().getTime();

        long dl = Math.abs(l-c);
        long dr = Math.abs(r-c);

        return Double.compare(dl, dr);
    }
}
