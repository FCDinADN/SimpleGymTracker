package com.runApp.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.runApp.R;
import com.runApp.ui.utils.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rares on 25/04/15.
 * Container fragment that contains tabs, maybe will be used in different places.
 */
public class BaseTabContainerFragment extends Fragment {

    private String[] titles;

    /**
     * The View pager.
     */
    @InjectView(R.id.pager)
    ViewPager viewPager;

    private MyPagerAdapter tabsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabbed_custom, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        titles = getResources().getStringArray(R.array.history_tabs);
        tabsAdapter = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(tabsAdapter);
        PagerSlidingTabStrip pageSlidingTab = (PagerSlidingTabStrip) view.findViewById(R.id.pager_tabstrip);
        pageSlidingTab.setIndicatorColor(getResources().getColor(R.color.actionbar_background));
        pageSlidingTab.setBackgroundColor(getResources().getColor(R.color.white));
        pageSlidingTab.setTextSize((int) getResources().getDimension(R.dimen.tab_top_text_size));
        pageSlidingTab.setShouldExpand(true);
        pageSlidingTab.setTabPaddingLeftRight(getResources().getDimensionPixelSize(R.dimen.tab_paddig)); // 6 dp
        pageSlidingTab.setDividerColor(getResources().getColor(R.color.border_color));
        pageSlidingTab.setIndicatorHeight(7); // convert to dp
        pageSlidingTab.setAllCaps(false);
        pageSlidingTab.setViewPager(viewPager);
        pageSlidingTab.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i2) {
                    }

                    @Override
                    public void onPageSelected(int i) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
    }

    /**
     * The type My pager adapter.
     */
    public class MyPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        /**
         * Instantiates a new My pager adapter.
         *
         * @param fm the fm
         */
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];

        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new EverydayHistoryFragment();
                case 1:
                    return new HistoryFragment();
                default:
                    return new EverydayHistoryFragment();
            }
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }

        @Override
        public void onPageSelected(int i) {
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
}
