package com.frigoshare;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.frigoshare.application.Application;
import com.frigoshare.cache.DataTools;
import com.frigoshare.tracking.Action;
import com.frigoshare.tracking.Category;
import com.frigoshare.tracking.GaTracker;
import com.frigoshare.user.GcmUpdater;
import com.frigoshare.auth.Keys;
import com.frigoshare.user.UserTools;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import java.util.List;

public class LoginActivity extends ActionBarActivity {

    private SocialAuthAdapter adapter;

    private SocialAuthAdapter getAdapter() {
        return this.adapter;
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
        setContentView(R.layout.activity_login);
        setup();
    }

    private void setup() {
        this.adapter = new SocialAuthAdapter(new ResponseListener());

        try {
            getAdapter().addConfig(SocialAuthAdapter.Provider.FACEBOOK, Keys.FACEBOOK_KEY, Keys.FACEBOOK_SECRET, null);
        } catch (Exception e) {
            Log.d("SocialAuth_ConfigError", e.getMessage());
            GaTracker.track(e);
        } try {
            getAdapter().addConfig(SocialAuthAdapter.Provider.GOOGLEPLUS, Keys.GOOGLEPLUS_KEY, Keys.GOOGLEPLUS_SECRET, null);
        } catch (Exception e) {
            Log.d("SocialAuth_ConfigError", e.getMessage());
            GaTracker.track(e);
        } try {
            getAdapter().addConfig(SocialAuthAdapter.Provider.TWITTER, Keys.TWITTER_KEY, Keys.TWITTER_SECRET, "read");
        } catch (Exception e) {
            Log.d("SocialAuth_ConfigError", e.getMessage());
            GaTracker.track(e);
        }

        getAdapter().addProvider(SocialAuthAdapter.Provider.FACEBOOK, R.drawable.facebook);
        getAdapter().addProvider(SocialAuthAdapter.Provider.GOOGLEPLUS, R.drawable.googleplus);
        getAdapter().addProvider(SocialAuthAdapter.Provider.TWITTER, R.drawable.twitter);

        getAdapter().addCallBack(SocialAuthAdapter.Provider.GOOGLEPLUS, "http://localhost");
        getAdapter().addCallBack(SocialAuthAdapter.Provider.TWITTER, "http://anarchikul.wordpress.com/");

        Button facebook_button = (Button)findViewById(R.id.fb_btn);
        facebook_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getAdapter().authorize(LoginActivity.this, SocialAuthAdapter.Provider.FACEBOOK);
            }
        });
        Button googleplus_button = (Button)findViewById(R.id.gp_btn);
        googleplus_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getAdapter().authorize(LoginActivity.this, SocialAuthAdapter.Provider.GOOGLEPLUS);
            }
        });
        Button twitter_button = (Button)findViewById(R.id.tw_btn);
        twitter_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getAdapter().authorize(LoginActivity.this, SocialAuthAdapter.Provider.TWITTER);
            }
        });
    }

    private final class ResponseListener implements DialogListener {

        @Override
        public void onComplete(Bundle values) {
            clean();
            getAdapter().getUserProfileAsync(new ProfileDataListener());
            getAdapter().getContactListAsync(new ContactDataListener());
            GcmUpdater.createGCMTask().execute(Application.get());
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            Log.d("SocialAuth_ResponseError", socialAuthError.getMessage());
            GaTracker.track(socialAuthError);
        }

        @Override
        public void onCancel() {}

        @Override
        public void onBack() {}
    }

    // To receive the profile response after authentication
    private final class ProfileDataListener implements SocialAuthListener<Profile> {

        @Override
        public void onExecute(String provider, Profile t) {
            UserTools.update(provider, t);
            Log.d("SocialAuth_Fetch", "Profile retrieved");
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            Log.d("SocialAuth_FetchError", socialAuthError.getMessage());
            GaTracker.track(socialAuthError);
        }
    }

    // To receive the contacts response after authentication
    private final class ContactDataListener implements SocialAuthListener<List<Contact>> {

        @Override
        public void onExecute(String provider, List<Contact> t) {
            UserTools.update(provider, t);
            Log.d("SocialAuth_Fetch", "Contacts retrieved");
            gotoMarket();
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            Log.d("SocialAuth_FetchError", socialAuthError.getMessage());
            GaTracker.track(socialAuthError);
        }
    }

    private void onLogin() {
        UserTools.getCurrentUser().refresh();
        DataTools.fetchData();
    }

    private void clean() {
        UserTools.cleanCurrentUser();
        DataTools.cleanData();
    }

    private void gotoMarket() {
        if (UserTools.getCurrentUser().getId() != null) {
            onLogin();

            Intent intent = new Intent(this, MarketActivity.class);
            startActivity(intent);
            GaTracker.track(Category.CLICK, Action.VIEW_SWITCH, "Login -> Market");
        } else {
            makeText("Login failed");
            GaTracker.track(Category.CLICK, Action.VIEW_SWITCH, "Login -> Login");
            clean();
        }
    }

    public void makeText(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.about:
                showAbout();
                return true;
            case R.id.contact:
                com.frigoshare.application.Contact.gotoMail(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        AlertDialog alert = new AlertDialog.Builder(this)
                            .setTitle(R.string.app_name)
                            .setMessage(Html.fromHtml(getString(R.string.about_txt)))
                            .setCancelable(false)
                            .setPositiveButton("OK", null)
                            .create();
        alert.show();

        ((TextView) alert
                .findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }
}
