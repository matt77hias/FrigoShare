package com.frigoshare.marketdata;

import android.app.ActionBar;
import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frigoshare.R;
import com.frigoshare.cache.ProfilePictureCache;
import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.endpoint.model.TimeSlot;
import com.frigoshare.leftover.Category;
import com.frigoshare.user.Location;
import com.frigoshare.utils.MathUtils;
import com.frigoshare.utils.TimeTools;

import java.text.DecimalFormat;
import java.util.List;

public class LeftoverExpandableAdapter extends BaseExpandableListAdapter {

    private final List<Leftover> searchResults;
    private final LayoutInflater inflater;
    private final Activity activity;

    public LeftoverExpandableAdapter(Activity act, List<Leftover> searchResults) {
        this.activity = act;
        this.inflater = act.getLayoutInflater();
        this.searchResults = searchResults;
    }

    public void removeLeftover(Leftover left){
        searchResults.remove(left);
    }

    protected List<Leftover> getSearchResults() {
        return this.searchResults;
    }

    protected LayoutInflater getInflater() {
        return this.inflater;
    }

    protected Activity getActivity() {
        return this.activity;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getSearchResults().get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Get Leftover to display
        final Leftover item = (Leftover) getChild(groupPosition, childPosition);
        // Create new view, do not reuse old one since it contains dynamic data.
        convertView = getInflater().inflate(R.layout.offer_detail, null);

        // Set text lables
        TextView text = (TextView) convertView.findViewById(R.id.offer_detail_discription);
        text.setText(item.getDescription().getMainDescription());
        text = (TextView) convertView.findViewById(R.id.offer_detail_owner);
        text.setText(item.getOffererInfo().getFullName());
        text.setCompoundDrawablesWithIntrinsicBounds(null, null, ProfilePictureCache.getCache().getDrawable(getActivity(), item.getOffererInfo().getProfileImageURL()), null);
        text = (TextView) convertView.findViewById(R.id.offer_detail_address);
        text.setText(item.getAddress().getAddress());

        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.vertical_list_offer_detail);
        for (TimeSlot t : item.getTimeslots()) {
            Button b = (Button) View.inflate(getActivity().getBaseContext(), R.layout.timeslot_button, null);
            b.setLayoutParams(getLayoutParams());
            b.setPadding(20, 20, 20, 20);
            b.setText(getTimeSlotString(t));
            b.setOnClickListener(new TimeslotClickListener(item, t));
            ll.addView(b);
        }
        return convertView;
    }

    private static LinearLayout.LayoutParams getLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10,10,10,10);
        return params;
    }

    private String getTimeSlotString(TimeSlot t) {
        String temp = TimeTools.getDateStringFromDate(t.getStart());
        temp = temp + "   " + TimeTools.getTimeStringFromDate(t.getStart()) + " - " + TimeTools.getTimeStringFromDate(t.getEnd());
        return temp;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(getSearchResults().isEmpty()) {
            return 0;
        }
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getSearchResults().get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        if(getSearchResults().isEmpty()) {
            return 1;
        }
        return getSearchResults().size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (getSearchResults().isEmpty()) {
            convertView = getInflater().inflate(R.layout.title_text, null);
            return convertView;
        }

        convertView = getInflater().inflate(R.layout.offer_title_distance, null);
        Leftover item = (Leftover) getGroup(groupPosition);

        View subView = convertView.findViewById(R.id.offer_title);
        ((TextView) subView).setText(item.getDescription().getName());
        ((TextView) subView).setCompoundDrawablesWithIntrinsicBounds(Category.convert(item.getCategory()).getDrawable(getActivity()), null, null, null);

        Location currentLocation = Location.getCurrentLocation();
        double distance = MathUtils.distance(currentLocation.getLongitude(), currentLocation.getLatitude(), item.getAddress().getLongitude(), item.getAddress().getLatitude());
        DecimalFormat df = new DecimalFormat("#.00");
        subView = convertView.findViewById(R.id.offer_distance);
        ((TextView) subView).setText(df.format(distance) + "km");
       return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}