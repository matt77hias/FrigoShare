package com.frigoshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.frigoshare.endpoint.model.Address;
import com.frigoshare.endpoint.model.Description;
import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.endpoint.model.TimeSlot;

import com.frigoshare.leftover.Category;
import com.frigoshare.marketdata.CategorySpinnerAdaptor;
import com.frigoshare.tracking.Action;
import com.frigoshare.tracking.GaTracker;
import com.frigoshare.utils.Converter;
import com.frigoshare.utils.TimeTools;
import com.frigoshare.proxy.LeftoverProxy;
import com.frigoshare.user.UserTools;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MarketOfferActivity extends ActionBarActivity {

    /*
    * Needs to be static, allows it to be saved over different screen calls.
    * Start values, also define these in resetData.
    */
    // Category.
    private static Category category = Category.values()[1];
    private static int spinner = 0;
    // Timeslots.
    private static List<TimeSlot> timeslots = new ArrayList<TimeSlot>();
    // Text fields.
    private static String title = "";
    private static String description = "";
    private static String address = "";
    // Input checks.
    private static boolean hasValidTitle = false;
    private static boolean hasValidDescription = false;
    private static boolean hasValidAddress = false;
    private static boolean hasValidTimeslot = false;

    /**
     * Resets all the in putted data to its initial state. They should both be defined in the parameters used in the class and in this method.
     */
    private void resetData() {
        timeslots = new ArrayList<TimeSlot>();
        category = Category.values()[1];
        spinner = 0;
        title = "";
        description = "";
        fetchAddress();
        hasValidTitle = false;
        hasValidDescription = false;
        hasValidTimeslot = false;
    }

    private void fetchAddress() {
        if (UserTools.getCurrentUser().getAddress() != null) {
            address = UserTools.getCurrentUser().getAddress().getAddress();
            hasValidAddress = true;
        } else {
            address = "";
            hasValidAddress = false;
        }
    }


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
        setContentView(R.layout.activity_market_offer);

        //Needed since there isn't a constructor to set this the first time.
        if (this.address.isEmpty()) {
            fetchAddress();
        }

        // Fill drop down menu
        Spinner catSpinner = (Spinner) this.findViewById(R.id.offer_spinner_category);
        catSpinner.setAdapter(new CategorySpinnerAdaptor(this));
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (Category) view.getTag();
                spinner = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing
            }
        });

        // Restore previous
        catSpinner.setSelection(spinner);
        // Set text slots
        setTitleText();
        setDescriptionText();
        setAddressText();
        // Fill timeslots
        setCreatedTimeSlots();

        //Hide keyboardcode
        setupUI(this.findViewById(R.id.scrollView));
    }

    private void setTitleText() {
        final EditText text = (EditText) this.findViewById(R.id.offer_detail_title);
        text.setText(title);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (text.getText().length() > 4) {
                    hasValidTitle = true;
                }
                title = s.toString();
            }
        });
    }

    private void setDescriptionText() {
        final EditText text = (EditText) this.findViewById(R.id.offer_detail_description);
        text.setText(description);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (text.getText().length() > 4) {
                    hasValidDescription = true;
                }
                description = s.toString();
            }
        });
    }

    private void setAddressText() {
        final EditText text = (EditText) this.findViewById(R.id.offer_detail_address);
        text.setText(address);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (text.getText().length() > 4) {
                    hasValidAddress = true;
                } else {
                    hasValidAddress = false;
                }
                address = s.toString();
            }
        });
    }


    private void setCreatedTimeSlots() {
        //Fetch layout and clear.
        final LinearLayout ll = (LinearLayout) this.findViewById(R.id.offer_detail_timeslotList);
        ll.removeAllViews();

        //Add all timeslots
        for (TimeSlot ts : timeslots) {
            String temp = TimeTools.getDateStringFromDate(ts.getStart());
            temp = temp + "  " + TimeTools.getTimeStringFromDate(ts.getStart());
            temp = temp + " - " + TimeTools.getTimeStringFromDate(ts.getEnd());

            TextView tv = (TextView) this.getLayoutInflater().inflate(R.layout.timeslot_text,null);
            tv.setText(temp);
            tv.setTag(ts);
            tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete timeslot")
                            .setMessage("Do you want to delete timeslot " + "\n" + ((TextView) v).getText())
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    timeslots.remove(v.getTag());
                                    ll.removeView(v);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();
                    return true;
                }
            });
            ll.addView(tv);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.market_offer, menu);
        return true;
    }

    /// BUTTON METHODS ///

    /// ADD TIME SLOT ///
    public void addTimeSlot(View view) {
        Intent intent = new Intent(this, OfferTimeSelect.class);
        startActivity(intent);
    }

    public static void addTimeSlot(Calendar start, Calendar end) {
        TimeSlot ts = new TimeSlot();
        ts.setStart(new DateTime(start.getTime()));
        ts.setEnd(new DateTime(end.getTime()));
        timeslots.add(ts);
        hasValidTimeslot = true;
    }

    /// CONFIRM OFFER ///
    public void confirmOffer(View view) {
        if (!hasValidTitle) {
            showInvalidDataToast("Please add a title. (4 chars minimum)");
            return;
        }
        if (!hasValidDescription) {
            showInvalidDataToast("Please add a description. (4 chars minimun)");
            return;
        }
        if (!hasValidAddress) {
            showInvalidDataToast("Please add a address");
            return;
        }
        if (!hasValidTimeslot) {
            showInvalidTimeSlotToast();
            return;
        }

        Leftover left = createLeftover();
        try {
            insertLeftover(left);
        } catch (IOException e) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_title_leftover_db_error))
                    .setMessage(getString(R.string.dialog_leftover_db_error))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    })
                    .show(); //TODO; check behavior
        }
        this.resetData();
        GaTracker.track(com.frigoshare.tracking.Category.OFFER, Action.CONFIRMATION);
        GaTracker.track(com.frigoshare.tracking.Category.CLICK, Action.VIEW_SWITCH, "Market Offer -> Market");
        Intent intent = new Intent(this, MarketActivity.class);
        startActivity(intent);
    }

    private void showInvalidTimeSlotToast() {
        makeText("Add a valid time slot");
        GaTracker.track(com.frigoshare.tracking.Category.OFFER, Action.INPUT_REJECTED, "Invalid time slot");
    }

    private void showInvalidDataToast(String message) {
        makeText(message);
        GaTracker.track(com.frigoshare.tracking.Category.OFFER, Action.CONFIRMATION, message);
    }

    private Leftover createLeftover() {
        return UserTools.createLeftover()
                .setCategory(category.toString())
                .setDescription(createDescription())
                .setAddress(createAddress())
                .setTimeslots(timeslots)
                .setOfferTimestamp(Converter.convert(new Date()));
    }

    private Address createAddress() {
        if (UserTools.getCurrentUser().getAddress() != null && UserTools.getCurrentUser().getAddress().getAddress().equals(this.address)) {
            return UserTools.getCurrentUser().getAddress();
        }
        Address a = new Address();
        a.setAddress(this.address);
        return a;
    }

    private Description createDescription() {
        Description des = new Description();
        des.setName(title);
        des.setMainDescription(description);
        return des;
    }

    private void insertLeftover(Leftover l) throws IOException {
        LeftoverProxy.InsertTask it = LeftoverProxy.createInsertTask();
        it.execute(l);
        try {
            it.get();
        } catch (InterruptedException e) {
            throw new IOException();
        } catch (ExecutionException e) {
            throw new IOException();
        }
    }

    /// CANCEL OFFER ///
    /*
     * Cancels the offer and clears all data fields, returns to main screen.
     */
    public void cancelOffer(View view) {
        // Reset data
        this.resetData();
        Intent intent = new Intent(this, MarketActivity.class);
        GaTracker.track(com.frigoshare.tracking.Category.OFFER, Action.CANCELLATION);
        GaTracker.track(com.frigoshare.tracking.Category.CLICK, Action.VIEW_SWITCH, "Market Offer -> Market");
        startActivity(intent);
    }

    public void makeText(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    ///// Hide Keyboard /////
    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public void hideSoftKeyboard() {
        hideSoftKeyboard(this);
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
