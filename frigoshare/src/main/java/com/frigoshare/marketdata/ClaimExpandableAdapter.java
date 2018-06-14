package com.frigoshare.marketdata;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.frigoshare.R;
import com.frigoshare.cache.ProfilePictureCache;
import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.endpoint.model.TimeSlot;
import com.frigoshare.user.UserTools;
import com.frigoshare.utils.TimeTools;

import java.util.ArrayList;
import java.util.List;

public class ClaimExpandableAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inflater;
    private final List<Leftover> myPendingClaims = new ArrayList<Leftover>();
    private final List<Leftover> myClaimedOffers = new ArrayList<Leftover>();
    private final List<Leftover> myUnclaimdOffers = new ArrayList<Leftover>();
    private static final int TITLE_COUNT = 3;

    private final Activity activity;

    public ClaimExpandableAdapter(Activity act) {
        this.activity = act;
        this.inflater = act.getLayoutInflater();
        fetchData();
    }

    protected LayoutInflater getInflater() {
        return this.inflater;
    }

    protected Activity getActivity() {
        return this.activity;
    }

    public void fetchData() {
        clearAllLists();
        myPendingClaims.addAll(UserTools.getCurrentUser().getPendingClaims());
        myClaimedOffers.addAll(UserTools.getCurrentUser().getPendingClaimedOffers());
        myUnclaimdOffers.addAll(UserTools.getCurrentUser().getPendingNonClaimedOffers());
    }

    protected void clearAllLists(){
        myPendingClaims.clear();
        myClaimedOffers.clear();
        myUnclaimdOffers.clear();
    }

    protected int getTotalNumberOfLeftovers(){
        return myPendingClaims.size() + myClaimedOffers.size() + myUnclaimdOffers.size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition);
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
        convertView = getInflater().inflate(R.layout.claim_detail, null);

        // Set text lables
        TextView text = (TextView) convertView.findViewById(R.id.claim_detail_discription);
        text.setText(item.getDescription().getMainDescription());

        text = (TextView) convertView.findViewById(R.id.claim_detail_owner);
        text.setText(item.getOffererInfo().getFullName());
        text.setCompoundDrawablesWithIntrinsicBounds(null, null, ProfilePictureCache.getCache().getDrawable(activity, item.getOffererInfo().getProfileImageURL()), null);

        text = (TextView) convertView.findViewById(R.id.claim_detail_address);
        text.setText(item.getAddress().getAddress());

        text = (TextView) convertView.findViewById(R.id.claim_detail_timeSlots);
        if (item.getClaimed()) {
            text.setText(TimeTools.getPeriodFromTimeSlot(item.getClaimedSlot()));
            text = (TextView) convertView.findViewById(R.id.claim_detail_titleTimeSlots);
            text.setText("Claimed Slot");
        } else {
            String temp = "";
            for (TimeSlot sl : item.getTimeslots()) {
                temp = temp + TimeTools.getPeriodFromTimeSlot(sl);
                temp = temp + "\n";
            }
            text.setText(temp);
        }

        if(item.getClaimed()){
            text = (TextView) convertView.findViewById(R.id.claim_detail_claimer);
            text.setText(item.getClaimerInfo().getFullName());
            text.setCompoundDrawablesWithIntrinsicBounds(null, null, ProfilePictureCache.getCache().getDrawable(activity, item.getClaimerInfo().getProfileImageURL()), null);
        }
        
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(isTitle(groupPosition)){
            return 0;
        }
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        int currentPos = 1;
        if(groupPosition < currentPos) {
            return "Pending Claims";
        }

        currentPos = currentPos + myPendingClaims.size();
        if(groupPosition < currentPos){
            return myPendingClaims.get(groupPosition-1);
        }
        currentPos = currentPos + 1;
        if(groupPosition < currentPos){
            return "Claimed Offers";
        }

        currentPos = currentPos + myClaimedOffers.size();
        if(groupPosition < currentPos){
            return myClaimedOffers.get(groupPosition+myClaimedOffers.size()-currentPos);
        }

        currentPos = currentPos + 1;
        if(groupPosition < currentPos){
            return "Unclaimed Offers";
        }

        return myUnclaimdOffers.get(groupPosition-currentPos);
    }

    protected boolean isTitle(int groupPosition){
        int currentPos = 1;
        if(groupPosition < currentPos) {
            return true;
        }

        currentPos = currentPos + myPendingClaims.size();
        if(groupPosition < currentPos){
            return false;
        }
        currentPos = currentPos + 1;
        if(groupPosition < currentPos){
            return true;
        }

        currentPos = currentPos + myClaimedOffers.size();
        if(groupPosition < currentPos){
            return false;
        }

        currentPos = currentPos + 1;
        if(groupPosition < currentPos){
            return true;
        }

        return false;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return TITLE_COUNT + getTotalNumberOfLeftovers();
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
        if(isTitle(groupPosition)){
            String text = (String) getGroup(groupPosition);
            convertView = getInflater().inflate(R.layout.claim_type_title, null);
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
          ((TextView) convertView).setText(content);
            return convertView;
        }
        Leftover item = (Leftover) getGroup(groupPosition);
        convertView = getInflater().inflate(R.layout.claim_title, null);
        ((TextView) convertView).setText(getTitleString(item));
        ((TextView) convertView).setCompoundDrawablesWithIntrinsicBounds(getDrawable(item), null, null, null);
        if(!item.getClaimed()){
            convertView.setPadding(12,12,0,12);
        }
        return convertView;
    }

    protected String getTitleString(Leftover left) {
        String temp = left.getDescription().getName();
        if (left.getClaimed()) {
            temp = temp + "\n" + TimeTools.getPeriodFromTimeSlot(left.getClaimedSlot());
        }
        return temp;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    protected Drawable getDrawable(Leftover leftover) {
        if (leftover.getClaimerInfo() != null && UserTools.getCurrentUser().getId().equals(leftover.getClaimerInfo().getId())) {
            return getDrawableIn();
        } else if (leftover.getClaimerInfo() != null) {
            return getDrawableOut();
        } else {
            return null;
        }
    }

    protected Drawable getDrawableIn() {
        return getActivity().getResources().getDrawable(R.drawable.arrow_left);
    }

    protected Drawable getDrawableOut() {
        return getActivity().getResources().getDrawable(R.drawable.arrow_right);
    }
}