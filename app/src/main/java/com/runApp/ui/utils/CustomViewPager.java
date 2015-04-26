package com.runApp.ui.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Rares on 25/04/15.
 * Custom component for view pager
 */
public class CustomViewPager extends NoCrashViewPager {
    /**
     * Instantiates a new Custom view pager.
     *
     * @param context the context
     */
    public CustomViewPager(Context context) {
        super(context);
    }


    /**
     * Instantiates a new Custom view pager.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        try{
            if(v != this  && v instanceof ViewPager)
            {
                ViewPager viewPager = (ViewPager) v;
                int currentPage =viewPager.getCurrentItem();
                int size = viewPager.getAdapter().getCount();

                return !(currentPage == (size - 1) && dx < 0);
            }
            return super.canScroll(v, checkV, dx, x, y);
        }catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }
}
