package com.frigoshare;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.frigoshare.marketdata.VisibilitySpinnerAdapter;
import com.frigoshare.tracking.Action;
import com.frigoshare.tracking.GaTracker;
import com.frigoshare.user.Location;
import com.frigoshare.user.User;
import com.frigoshare.user.UserTools;
import com.frigoshare.user.Visibility;
import com.google.android.gms.analytics.GoogleAnalytics;
public class Preferences extends ActionBarActivity {

    private Visibility visPref;
    private String addressPref;

    public Preferences() {

    }

    protected Visibility getVisPref() {
        return this.visPref;
    }

    protected String getAddressPref() {
        return this.addressPref;
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
        setContentView(R.layout.activity_preferences);

        // Fetch default data
        setDefaultValues();

        // Set address
        EditText text = (EditText) this.findViewById(R.id.preferences_address);
        text.setText(getAddressPref());

        // Set dropdown menu
        Spinner spinner = (Spinner) this.findViewById(R.id.preferences_spinner_visibility);
        final VisibilitySpinnerAdapter adapter = new VisibilitySpinnerAdapter(this);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                visPref = (Visibility) adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing
            }
        });
        spinner.setSelection(adapter.getPositionOfVisibility(getVisPref()));

    }

    private void setDefaultValues() {
        this.visPref = UserTools.getCurrentUser().getVisibility();
        this.addressPref = (UserTools.getCurrentUser().getAddress() != null) ? UserTools.getCurrentUser().getAddress().getAddress() : "";
    }

    public void saveSettings(View view) {
        EditText text = (EditText) this.findViewById(R.id.preferences_address);
        Editable address = text.getText();

        if (address.length() < 5) {
            showInvalidDataToast();
            return;
        }

        User current = UserTools.getCurrentUser();
        current.setVisibility(getVisPref());
        current.setAddress(Location.getCurrentAddress().setAddress(address.toString()));
        current.push();

        GaTracker.track(com.frigoshare.tracking.Category.OFFER, Action.CANCELLATION);
        gotoMarket();
    }

    public void cancelSettings(View view) {
        GaTracker.track(com.frigoshare.tracking.Category.OFFER, Action.CANCELLATION);
        gotoMarket();
    }

    private void gotoMarket() {
        Intent intent = new Intent(this, MarketActivity.class);
        GaTracker.track(com.frigoshare.tracking.Category.CLICK, Action.VIEW_SWITCH, "Preferences -> Market");
        startActivity(intent);
    }

    private void showInvalidDataToast() {
        makeText("Add a valid address, at least 5 characters");
        GaTracker.track(com.frigoshare.tracking.Category.OFFER, Action.CONFIRMATION, "Invalid address");
    }

    public void makeText(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
