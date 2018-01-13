package com.example.aser93.organisernewdesign;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by aser93 on 23.6.2017 г..
 */

public class customViewPager extends ViewPager {

    public customViewPager(Context context) {
        super(context);
    }
    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if(v != this && v instanceof ViewPager) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
}
