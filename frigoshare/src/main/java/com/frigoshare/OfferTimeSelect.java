package com.frigoshare;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.frigoshare.tracking.Action;
import com.frigoshare.tracking.Category;
import com.frigoshare.tracking.GaTracker;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.Calendar;

public class OfferTimeSelect extends ActionBarActivity {

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_time_select);
        createStartData();
        updateDates();
    }

    private Calendar startDate;
    private Calendar endDate;

    /*
    Sets the initial visible dates.
     */
    private void createStartData() {
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        endDate.add(Calendar.MINUTE,30);
    }

    /*
    Updates the GUI to display the selected dates.
     */
    private void updateDates(){
        TextView text = (TextView) this.findViewById(R.id.dateselect);
        text.setText(startDate.get(Calendar.DATE)+"-"+(startDate.get(Calendar.MONTH)+1)+"-"+startDate.get(Calendar.YEAR));

        text = (TextView) this.findViewById(R.id.startTime);
        if(startDate.get(Calendar.MINUTE) < 10) {
            text.setText(startDate.get(Calendar.HOUR_OF_DAY)+":"+0+startDate.get(Calendar.MINUTE));
        } else {
            text.setText(startDate.get(Calendar.HOUR_OF_DAY)+":"+startDate.get(Calendar.MINUTE));
        }

        text = (TextView) this.findViewById(R.id.endTime);
        if(endDate.get(Calendar.MINUTE) < 10) {
            text.setText(endDate.get(Calendar.HOUR_OF_DAY) + ":" + 0 + endDate.get(Calendar.MINUTE));
        } else {
            text.setText(endDate.get(Calendar.HOUR_OF_DAY)+":"+endDate.get(Calendar.MINUTE));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.offer_time_select, menu);
        return true;
    }

    /// BUTTONS ///
    public void selectDate(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void selectStartTime(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setUpdateDate(startDate);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void selectEndTime(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setUpdateDate(endDate);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void createTimeslot(View view) {
        if (startDate.after(endDate)){
            String text = "R.string.dialog_wrong_time";
            makeText(text);
            GaTracker.track(Category.OFFER, Action.INPUT_REJECTED, "Time slot creation: end < start");
            return;
        }
        if (endDate.before(Calendar.getInstance())) {
            String text = "End time should be after the current time.";
            makeText(text);
            GaTracker.track(Category.OFFER, Action.INPUT_REJECTED, "Time slot creation: end < current");
            return;
        }
        MarketOfferActivity.addTimeSlot(startDate, endDate);
        GaTracker.track(Category.OFFER, Action.INPUT_ACCEPTED, "Time slot");
        GaTracker.track(Category.CLICK, Action.VIEW_SWITCH, "Time slot -> Market Offer");
        Intent intent = new Intent(this, MarketOfferActivity.class);
        startActivity(intent);
    }

    public void cancelTimeslot(View view) {
        Intent intent = new Intent(this, MarketOfferActivity.class);
        startActivity(intent);
    }

    // DATE AND TIME PICKER
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public DatePickerFragment() {

        }

        private OfferTimeSelect getOfferTimeSelect() {
            return (OfferTimeSelect) super.getActivity();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            getOfferTimeSelect().startDate.set(Calendar.YEAR, year);
            getOfferTimeSelect().startDate.set(Calendar.MONTH, month);
            getOfferTimeSelect().startDate.set(Calendar.DATE, day);
            getOfferTimeSelect().endDate.set(Calendar.YEAR, year);
            getOfferTimeSelect().endDate.set(Calendar.MONTH, month);
            getOfferTimeSelect().endDate.set(Calendar.DATE, day);
            getOfferTimeSelect().updateDates();
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        private Calendar updateDate;

        public TimePickerFragment(){
        }

        private void setUpdateDate(Calendar updateDate) {
            this.updateDate = updateDate;
        }

        private OfferTimeSelect getOfferTimeSelect() {
            return (OfferTimeSelect) super.getActivity();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = updateDate.get(Calendar.HOUR_OF_DAY);
            int minute = updateDate.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            updateDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
            updateDate.set(Calendar.MINUTE,minute);
            getOfferTimeSelect().updateDates();
        }
    }

    public void makeText(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
