package com.frigoshare.utils;

import com.frigoshare.endpoint.model.TimeSlot;
import com.google.api.client.util.DateTime;

import java.util.Calendar;
import java.util.Date;

public class TimeTools {

    public static String getDateStringFromDate(DateTime date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date.getValue()));
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);

        String temp;
        String connector = "/";

        if(day<10){
            temp = 0+""+day;
        } else {
            temp = ""+day;
        }
        temp = temp + connector;
        if(month < 10) {
            temp = temp + 0 + "" + month;
        } else {
            temp = temp + ""+ month;
        }
        temp = temp + connector + year;

        return temp;
    }

    public static String getTimeStringFromDate(DateTime date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date.getValue()));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        String temp;
        String connector = ":";

        if(hour<10){
            temp = 0+""+hour;
        } else {
            temp = ""+hour;
        }
        temp = temp + connector;
        if(minute < 10) {
            temp = temp + 0 + "" + minute;
        } else {
            temp = temp + ""+ minute;
        }
        return temp;
    }

    public static String getPeriodFromTimeSlot(TimeSlot time){
        String temp = getDateStringFromDate(time.getStart());
        temp = temp + "    ";
        temp = temp + getTimeStringFromDate(time.getStart());
        temp = temp + " - " + getTimeStringFromDate(time.getEnd());
        return temp;
    }
}
