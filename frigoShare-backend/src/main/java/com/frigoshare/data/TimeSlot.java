package com.frigoshare.data;

import com.googlecode.objectify.annotation.Embed;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

@Embed
public class TimeSlot implements Comparable<TimeSlot>, Serializable {

    private static final long serialVersionUID = 1L;

    private Date start;
    private Date end;

    public TimeSlot() {

    }

    public TimeSlot(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public int compareTo(TimeSlot o) {
        return this.start.compareTo(o.getStart());
    }

    public Date getStart() {
        return this.start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return this.end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        return dateFormat.format(getStart()) + " "
                + timeFormat.format(getStart()) + " until "
                + timeFormat.format(getEnd());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEnd() == null) ? 0 : getEnd().hashCode());
        result = prime * result + ((getStart() == null) ? 0 : getStart().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TimeSlot other = (TimeSlot) obj;
        if (getEnd() == null) {
            if (other.getEnd() != null)
                return false;
        } else if (!getEnd().equals(other.getEnd()))
            return false;
        if (getStart() == null) {
            if (other.getStart() != null)
                return false;
        } else if (!getStart().equals(other.getStart()))
            return false;
        return true;
    }
}
