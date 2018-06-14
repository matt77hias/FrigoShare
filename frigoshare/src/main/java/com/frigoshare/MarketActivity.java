package com.frigoshare;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.leftover.filtering.GoogleMapsFilter;
import com.frigoshare.marketdata.ClaimExpandableAdapter;
import com.frigoshare.cache.DataTools;
import com.frigoshare.tracking.Action;
import com.frigoshare.tracking.Category;
import com.frigoshare.tracking.GaTracker;
import com.frigoshare.user.UserCacheListener;
import com.frigoshare.user.UserTools;
import com.frigoshare.utils.CustomViewPager;

import com.frigoshare.utils.FilterUtils;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarketActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private SectionsPagerAdapter getSectionsPagerAdapter() {
        return this.mSectionsPagerAdapter;
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private CustomViewPager mViewPager;

    private CustomViewPager getViewPager() {
        return this.mViewPager;
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
        setContentView(R.layout.activity_market);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.pager);
        getViewPager().setAdapter(getSectionsPagerAdapter());

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        getViewPager().setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < getSectionsPagerAdapter().getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(getSectionsPagerAdapter().getPageTitle(i))
                    .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        getViewPager().setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Fragment market = new MarketFragment();
        private Fragment neighbourhood = new NeighbourhoodFragment();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return this.market;
                case 1:
                    return this.neighbourhood;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.tab1).toUpperCase(l);
                case 1:
                    return getString(R.string.tab2).toUpperCase(l);
            }
            return null;
        }
    }

    public static class MarketFragment extends Fragment {

        public MarketFragment() {
        }

        private MarketActivity getMarketActivity() {
            return (MarketActivity) super.getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_market, container, false);
            Button search_button = (Button) rootView.findViewById(R.id.search_btn);
            search_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getMarketActivity().gotoSearch();
                }
            });

            Button offer_button = (Button) rootView.findViewById(R.id.offer_btn);
            offer_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getMarketActivity().gotoOffer();
                }
            });

            ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.expandleClaimView);
            final ClaimExpandableAdapter adapter = new ClaimExpandableAdapter(getMarketActivity());
            UserTools.getCurrentUser().addListener(new UserCacheListener() {
                @Override
                public void onUserFetched(boolean success){
                   adapter.fetchData();
                   adapter.notifyDataSetChanged();
                   Log.d("DATA_Fetch", "Refreshed pending leftovers");
                }
            });
            listView.setAdapter(adapter);

            //Fetch data.
            DataTools.getCurrentDataCache().fetch();
            UserTools.getCurrentUser().refresh();
            return rootView;
        }

        @Override
        public void onStart() {
            getMarketActivity().getViewPager().setSwipeable(true);
            super.onStart();
        }
    }

    public static class NeighbourhoodFragment extends Fragment {

        private GoogleMap googleMap;

        public NeighbourhoodFragment() {
        }

        private MarketActivity getMarketActivity() {
            return (MarketActivity) super.getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_neighbourhood, container, false);
            try {
                initializeMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rootView;
        }

        private void initializeMap() {
            if (this.googleMap == null) {
                this.googleMap = ((SupportMapFragment) getMarketActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
                if (this.googleMap == null) {
                    getMarketActivity().showTextMessage("Sorry! unable to create maps");
                } else {
                    this.googleMap.setMyLocationEnabled(true);
                    this.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location arg0) {
                            centerMapOnMyLocation(arg0.getLatitude(), arg0.getLongitude());
                            updateLocation(arg0.getLatitude(), arg0.getLongitude());
                        }
                    });
                }
            }
        }

        private void centerMapOnMyLocation(double latitude, double longitude) {
            LatLng myLocation = new LatLng(latitude, longitude);
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.5f), 4000, null);
        }

        private MarkerOptions marker;

        private void updateLocation(double latitude, double longitude) {
            Geocoder geoCoder = new Geocoder(getMarketActivity().getBaseContext(), Locale.getDefault());
            try {
                this.googleMap.clear();

                final List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    com.frigoshare.user.Location.getCurrentLocation()
                            .setCountryName(addresses.get(0).getCountryName())
                            .setPostalCode(addresses.get(0).getPostalCode());
                }
                com.frigoshare.user.Location.getCurrentLocation()
                        .setLongitude(longitude)
                        .setLatitude(latitude);
            } catch (IOException e) {
                Log.d("GMaps_Error", e.getMessage());
                GaTracker.track(e);
            }
        }

        private List<Leftover> getLeftovers() {
            return FilterUtils.filter(DataTools.getCurrentDataCache().getUnclaimedLeftOvers(), new GoogleMapsFilter());
        }

        private void addMarkers() {
            for (Leftover l : getLeftovers()) {
                MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(l.getAddress().getLatitude(), l.getAddress().getLongitude()))
                    .title(l.getDescription().getName())
                    .snippet(l.getDescription().getMainDescription())
                    .icon(BitmapDescriptorFactory.fromResource(com.frigoshare.leftover.Category.convert(l.getCategory()).getDrawableId(getMarketActivity())));
                this.googleMap.addMarker(marker);
            }
        }

        @Override
        public void onStart() {
            getMarketActivity().getViewPager().setSwipeable(false);
            super.onStart();
        }
    }

    private void gotoSearch() {
        GaTracker.track(Category.CLICK, Action.VIEW_SWITCH, "Market -> Market Search");
        Intent intent = new Intent(this, MarketSearchCategoryActivity.class);
        startActivity(intent);
    }

    private void gotoOffer() {
        GaTracker.track(Category.CLICK, Action.VIEW_SWITCH, "Market -> Market Offer");
        Intent intent = new Intent(this, MarketOfferActivity.class);
        startActivity(intent);
    }

    private void gotoPreferences() {
        GaTracker.track(Category.CLICK, Action.VIEW_SWITCH, "Market -> Preferences");
        Intent intent = new Intent(this, Preferences.class);
        startActivity(intent);
    }

    private void showTextMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.market, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.preferences:
                gotoPreferences();
                return true;
            case R.id.action_refresh:
                DataTools.getCurrentDataCache().fetch();
                UserTools.getCurrentUser().refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
