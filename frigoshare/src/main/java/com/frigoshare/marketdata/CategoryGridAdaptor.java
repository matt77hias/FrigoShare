package com.frigoshare.marketdata;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.frigoshare.R;
import com.frigoshare.leftover.Category;

public class CategoryGridAdaptor extends BaseAdapter {
    private final Context mContext;
    private final Category[] categories;

    protected Context getContext() {
        return mContext;
    }

    protected Category[] getCategories() {
        return categories;
    }

    public CategoryGridAdaptor(Context mCcontext) {
        this.mContext = mCcontext;
        this.categories = Category.values();
    }

    @Override
    public int getCount() {
        return getCategories().length;
    }

    @Override
    public Object getItem(int position) {
        return getCategories()[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv  = (TextView) View.inflate(getContext(), R.layout.category_icon, null);
        Category c = getCategories()[position];
        tv.setText(c.toString());
        tv.setCompoundDrawablesWithIntrinsicBounds(null, c.getDrawable(getContext()), null, null);
        tv.setOnClickListener(new CategoryClickListener(c));
        return tv;
    }

}
