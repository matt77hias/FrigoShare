package com.frigoshare.marketdata;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.frigoshare.R;
import com.frigoshare.leftover.Category;

import java.util.Arrays;
import java.util.List;

public class CategorySpinnerAdaptor implements SpinnerAdapter {
    private final Context mContext;
    private final List<Category> categories;

    public CategorySpinnerAdaptor(Context mContext) {
        this.mContext = mContext;
        this.categories = Category.getAllCategoriesExceptAll();
    }

    protected Context getmContext() {
        return this.mContext;
    }

    protected List<Category> getCategories() {
        return this.categories;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.dropdown_entry, null);
        }
        Category c = getCategories().get(position);
        ((TextView) convertView).setText(c.toString());
        convertView.setTag(c);
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
        return getCategories().size();
    }

    @Override
    public Object getItem(int position) {
        return getCategories().get(position);
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
        return getCategories().size() < 1;
    }
}
