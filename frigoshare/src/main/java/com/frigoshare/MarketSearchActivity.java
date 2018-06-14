package com.frigoshare;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.frigoshare.cache.DataTools;
import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.leftover.Category;
import com.frigoshare.leftover.filtering.CategoryFilter;
import com.frigoshare.leftover.filtering.TextualFilter;
import com.frigoshare.leftover.filtering.VisibilityFilter;
import com.frigoshare.leftover.sorting.LastMinuteComparator;
import com.frigoshare.leftover.sorting.NewComparator;
import com.frigoshare.leftover.sorting.NeighborhoodComperator;
import com.frigoshare.marketdata.LeftoverExpandableAdapter;
import com.frigoshare.tracking.Action;
import com.frigoshare.tracking.GaTracker;
import com.frigoshare.user.Location;
import com.frigoshare.user.UserTools;
import com.frigoshare.user.Visibility;
import com.frigoshare.utils.Filter;
import com.frigoshare.utils.FilterUtils;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MarketSearchActivity extends ActionBarActivity implements ActionBar.TabListener {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private SectionsPagerAdapter getSectionsPagerAdapter() {
        return this.mSectionsPagerAdapter;
    }

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private ViewPager getViewPager() {
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
        setContentView(R.layout.activity_market_search);
        // Set up the action bar.

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
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
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(getSectionsPagerAdapter().getPageTitle(i))
                            .setTabListener(this)
            );
        }

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null) {
                GaTracker.track(com.frigoshare.tracking.Category.CLICK, Action.SEARCH, "Search: " + query);
                setCurrentFilter(new TextualFilter(query));
            }
        }

        if (getInitialSearchString() != null) {
            setCurrentFilter(new TextualFilter(getInitialSearchString()));
            setInitialSearchString(null); //TODO really ugly at the moment
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
        private Fragment newOffers = new NewFragment();
        private Fragment lastMinuteOffers = new LastMinuteFragment();
        private Fragment neighbourhoodOffers = new NeighborhoodFragment();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return this.newOffers;
                case 1:
                    return this.lastMinuteOffers;
                case 2:
                    return this.neighbourhoodOffers;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.s_tab1).toUpperCase(l);
                case 1:
                    return getString(R.string.s_tab2).toUpperCase(l);
                case 2:
                    return getString(R.string.s_tab3).toUpperCase(l);
            }
            return null;
        }
    }

    public static class NewFragment extends Fragment {

        private static LeftoverExpandableAdapter adapter;

        public NewFragment() {
        }

        private MarketSearchActivity getMarketSearchActivity() {
            return (MarketSearchActivity) super.getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_market_search, container, false);
            ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.expandleResultView);
            adapter = new LeftoverExpandableAdapter(getMarketSearchActivity(), getMarketSearchActivity().getCachedNew());
            listView.setAdapter(adapter);
            return rootView;
        }

        public static void removeLeftover(Leftover left) {
            if (adapter != null) {
                adapter.removeLeftover(left);
            }
        }

        public static void refreshAdaptor() {
            if (adapter != null) {

                adapter.notifyDataSetChanged();
            }
        }
    }

    public static class LastMinuteFragment extends Fragment {

        private static LeftoverExpandableAdapter adapter;

        public LastMinuteFragment() {
        }

        private MarketSearchActivity getMarketSearchActivity() {
            return (MarketSearchActivity) super.getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_market_search, container, false);
            ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.expandleResultView);
            adapter = new LeftoverExpandableAdapter(getMarketSearchActivity(), getMarketSearchActivity().getCachedLastMinute());
            listView.setAdapter(adapter);
            return rootView;
        }

        public static void removeLeftover(Leftover left) {
            if (adapter != null) {
                adapter.removeLeftover(left);
            }
        }

        public static void refreshAdaptor() {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public static class NeighborhoodFragment extends Fragment {

        private static LeftoverExpandableAdapter adapter;

        public NeighborhoodFragment() {
        }

        private MarketSearchActivity getMarketSearchActivity() {
            return (MarketSearchActivity) super.getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_market_search, container, false);
            ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.expandleResultView);
            adapter = new LeftoverExpandableAdapter(getMarketSearchActivity(), getMarketSearchActivity().getCachedNeighborhood());
            listView.setAdapter(adapter);
            return rootView;
        }

        public static void removeLeftover(Leftover left) {
            if (adapter != null) {
                adapter.removeLeftover(left);
            }
        }

        public static void refreshAdaptor() {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public static void removeLeftover(Leftover left) {
        NewFragment.removeLeftover(left);
        LastMinuteFragment.removeLeftover(left);
        NeighborhoodFragment.removeLeftover(left);
        NewFragment.refreshAdaptor();
        LastMinuteFragment.refreshAdaptor();
        NeighborhoodFragment.refreshAdaptor();
    }

    private Filter<Leftover> filter = new VisibilityFilter(Visibility.ALL);

    protected Filter<Leftover> getCurrentFilter() {
        synchronized (this.c) {
            return this.filter;
        }
    }

    protected void setCurrentFilter(Filter<Leftover> filter) {
        synchronized (this.c) {
            this.filter = filter;
            refresh();
        }
    }

    private static String initialSearchString;

    public static String getInitialSearchString() {
        return initialSearchString;
    }

    public static void setInitialSearchString(String initialSearchString) {
        MarketSearchActivity.initialSearchString = initialSearchString;
    }

    private static Category category;

    public static Category getCategory() {
        return category;
    }

    public static void setCategory(Category category) {
        MarketSearchActivity.category = category;
    }

    protected static CategoryFilter getCategoryFilter() {
        return new CategoryFilter(getCategory());
    }

    protected static List<Leftover> getUnclaimedLeftovers() {
        return FilterUtils.filter(DataTools.getCurrentDataCache().getUnclaimedLeftOvers(), getCategoryFilter());
    }

    protected static List<Leftover> getNew(Filter<Leftover> filter) {
        List<Leftover> temp = getUnclaimedLeftovers();
        Collections.sort(temp, new NewComparator());
        return FilterUtils.filter(temp, filter);
    }

    protected static List<Leftover> getLastMinute(Filter<Leftover> filter) {
        List<Leftover> temp = getUnclaimedLeftovers();
        Collections.sort(temp, new LastMinuteComparator());
        return FilterUtils.filter(temp, filter);
    }

    protected static List<Leftover> getNeighborhood(Filter<Leftover> filter) {
        List<Leftover> temp = getUnclaimedLeftovers();
        Collections.sort(temp, new NeighborhoodComperator(
                Location.getCurrentLocation().getLongitude(),
                Location.getCurrentLocation().getLatitude()));
        return FilterUtils.filter(temp, filter);
    }

    private Object c = new Object();
    private List<Leftover> cachedNew = getNew(getCurrentFilter());
    private List<Leftover> cachedLastMinute = getLastMinute(getCurrentFilter());
    private List<Leftover> cachedNeighborhood = getNeighborhood(getCurrentFilter());

    public List<Leftover> getCachedNeighborhood() {
        synchronized (this.c) {
            return this.cachedNeighborhood;
        }
    }

    public void setCachedNeighborhood(List<Leftover> cachedNeighborhood) {
        synchronized (this.c) {
            this.cachedNeighborhood = cachedNeighborhood;
        }
    }

    public List<Leftover> getCachedLastMinute() {
        synchronized (this.c) {
            return this.cachedLastMinute;
        }
    }

    public void setCachedLastMinute(List<Leftover> cachedLastMinute) {
        synchronized (this.c) {
            this.cachedLastMinute = cachedLastMinute;
        }
    }

    public List<Leftover> getCachedNew() {
        synchronized (this.c) {
            return this.cachedNew;
        }
    }

    public void setCachedNew(List<Leftover> cachedNew) {
        synchronized (this.c) {
            this.cachedNew = cachedNew;
        }
    }

    public void refresh() {
        setCachedNew(getNew(getCurrentFilter()));
        setCachedLastMinute(getLastMinute(getCurrentFilter()));
        setCachedNeighborhood(getNeighborhood(getCurrentFilter()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.market_search, menu);
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
            case R.id.filter_all:
                setCurrentFilter(new VisibilityFilter(Visibility.ALL));
                return true;
            case R.id.filter_f:
                setCurrentFilter(new VisibilityFilter(Visibility.FRIENDS));
                return true;
            case R.id.filter_fof:
                setCurrentFilter(new VisibilityFilter(Visibility.FRIENDS_OF_FRIENDS));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}