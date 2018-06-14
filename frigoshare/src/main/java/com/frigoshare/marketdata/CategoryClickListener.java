package com.frigoshare.marketdata;

import android.content.Intent;
import android.view.View;

import com.frigoshare.MarketSearchActivity;
import com.frigoshare.leftover.Category;
import com.frigoshare.tracking.Action;
import com.frigoshare.tracking.GaTracker;

public class CategoryClickListener implements View.OnClickListener {

    private final Category category;

    public Category getCategory() {
        return this.category;
    }

    public CategoryClickListener(Category category) {
        super();
        this.category = category;
    }

    @Override
    public void onClick(final View v) {
        MarketSearchActivity.setCategory(getCategory());
        GaTracker.track(com.frigoshare.tracking.Category.CLICK, Action.VIEW_SWITCH, "Category: " + category.toString() + " -> MarketSearch");
        Intent intent = new Intent(v.getContext(), MarketSearchActivity.class);
        v.getContext().startActivity(intent);
    }
}