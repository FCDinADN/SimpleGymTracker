package com.runApp.ui.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Rares on 25/04/15.
 * Custom component for no crash view pager
 */
public class NoCrashViewPager extends ViewPager {


    /**
     * Instantiates a new No crash view pager.
     *
     * @param context the context
     */
    public NoCrashViewPager(Context context) {
        super(context);
    }

    /**
     * Instantiates a new No crash view pager.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public NoCrashViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
