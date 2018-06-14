package com.frigoshare.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isSwipeable = true;

    public boolean isSwipeable() {
        return this.isSwipeable;
    }

    public void setSwipeable(boolean isSwipeable) {
        this.isSwipeable = isSwipeable;
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (!isSwipeable()) {
            return true;
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }
}
