package com.frigoshare.marketdata;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.frigoshare.R;
import com.frigoshare.user.Visibility;

public class VisibilitySpinnerAdapter implements SpinnerAdapter {

    private final Context mContext;
    private final Visibility[] visibilities;

    public VisibilitySpinnerAdapter(Context mContext) {
        this.mContext = mContext;
        this.visibilities = Visibility.values();
    }

    protected Context getContext() {
        return mContext;
    }

    protected Visibility[] getVisibilities() {
        return visibilities;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(getContext(), R.layout.dropdown_entry, null);
        ((TextView) convertView).setText(getVisibilities()[position].toPrettyString());
        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        //Not needed, data is static.
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        //Not needed, data is static
    }

    @Override
    public int getCount() {
        return getVisibilities().length;
    }

    @Override
    public Object getItem(int position) {
        return getVisibilities()[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getVisibilities().length<1;
    }

    public int getPositionOfVisibility(Visibility v) {
       int i = 0;
       for (Visibility vis : getVisibilities()) {
           if(vis.equals(v)) {
               return i;
           }
           i++;
       }
       return 0;
    }
}
