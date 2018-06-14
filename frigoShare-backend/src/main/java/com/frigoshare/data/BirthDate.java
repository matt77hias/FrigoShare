package com.frigoshare.data;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class BirthDate {

    private int day;
    private int month;
    private int year;

    public BirthDate() {

    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)                              { return true;  }
        if (o == null || getClass() != o.getClass()){ return false; }
        BirthDate birthdate = (BirthDate) o;
        if (getDay()    != birthdate.getDay())      { return false; }
        if (getMonth()  != birthdate.getMonth())    { return false; }
        if (getYear()   != birthdate.getYear())     { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int result = getDay();
        result = 31 * result + getMonth();
        result = 31 * result + getYear();
        return result;
    }
}
