package com.frigoshare;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.frigoshare.cache.DataCache;
import com.frigoshare.cache.DataCacheListener;
import com.frigoshare.leftover.Category;
import com.frigoshare.marketdata.CategoryGridAdaptor;
import com.frigoshare.cache.DataTools;
import com.frigoshare.tracking.Action;
import com.frigoshare.tracking.GaTracker;
import com.frigoshare.user.UserTools;
import com.google.android.gms.analytics.GoogleAnalytics;

public class MarketSearchCategoryActivity extends ActionBarActivity {

    ProgressDialog dialog;

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
        setContentView(R.layout.activity_market_search_category);

        // Dynamically create grid.
        GridView gl = (GridView) findViewById(R.id.category_gridview);
        gl.setAdapter(new CategoryGridAdaptor(this));

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null) {
                GaTracker.track(com.frigoshare.tracking.Category.CLICK, Action.SEARCH, "Search: " + query);
                gotoMarketSearch(query);
            }
        } else {
            // Refresh data.
            final DataCache dc = DataTools.getCurrentDataCache();
            DataCacheListener dcl = new DataCacheListener() {
                @Override
                public void onUnclaimedLeftoversFetched(boolean b) {
                    dc.removeListener(this);
                    dialog.dismiss();
                }
            };

            dc.addListener(dcl);
            // Show waiting dialog
            dialog = ProgressDialog.show(this, "Loading", "Refreshing data, please wait...", true);
            dc.fetch();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.market_category_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                onSearchRequested();
                return true;
            case R.id.action_refresh:
                DataTools.getCurrentDataCache().fetch();
                UserTools.getCurrentUser().refresh();
                return true;
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoMarketSearch(String query) {
        MarketSearchActivity.setCategory(Category.ALL);
        MarketSearchActivity.setInitialSearchString(query);
        GaTracker.track(com.frigoshare.tracking.Category.CLICK, Action.VIEW_SWITCH, "Search: " + query + " -> MarketSearch");
        Intent intent = new Intent(this, MarketSearchActivity.class);
        startActivity(intent);
    }
}
