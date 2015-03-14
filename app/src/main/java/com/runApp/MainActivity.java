package com.runApp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.runApp.database.GymDatabaseHelper;
import com.runApp.fragments.CardioFragment;
import com.runApp.fragments.HistoryFragment;
import com.runApp.fragments.NavigationDrawerFragment;
import com.runApp.fragments.PathGoogleMapFragment;
import com.runApp.models.ComplexLocation;
import com.runApp.utils.Constants;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.DumbData;
import com.runApp.utils.GPSTracker;
import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String TAG = MainActivity.class.getSimpleName();

    //    @InjectView(R.id.main_content_frame)
//    View contentFrame;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    TextView toolbarTitle;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private Fragment currentFragment = null;
    private GPSTracker tracker;
    private PathGoogleMapFragment mPathGoogleMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbarTitle = ((TextView) toolbar.findViewById(R.id.toolbar_title));
        toolbarTitle.setText(R.string.cardio_selection);

        mPathGoogleMapFragment = new PathGoogleMapFragment();

        //TODO remove user's age from here
        UserUtils.setUserAge(23);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon_cardio);


//        restoreActionBar();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //insert dumb data
//        inserLocations();
    }

    public GPSTracker getTracker() {
        return tracker;
    }

    public void startTracker() {
        tracker = new GPSTracker(this);
    }

    public void stopTracker() {
        tracker.stopUsingGPS();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserUtils.checkDate();
        //TODO remove the tracker from here
//        startTracker();
    }

    private void inserLocations() {
        ComplexLocation complexLocation = new ComplexLocation(DumbData.POSITION_1.latitude, DumbData.POSITION_1.longitude, 0.0f, 23);
        GymDatabaseHelper.getInst().insertLocation(complexLocation);
        complexLocation = new ComplexLocation(DumbData.POSITION_2.latitude, DumbData.POSITION_2.longitude, 0.0f, 23);
        GymDatabaseHelper.getInst().insertLocation(complexLocation);
        complexLocation = new ComplexLocation(DumbData.POSITION_3.latitude, DumbData.POSITION_3.longitude, 0.0f, 23);
        GymDatabaseHelper.getInst().insertLocation(complexLocation);
        complexLocation = new ComplexLocation(DumbData.POSITION_4.latitude, DumbData.POSITION_4.longitude, 0.0f, 23);
        GymDatabaseHelper.getInst().insertLocation(complexLocation);
        complexLocation = new ComplexLocation(DumbData.POSITION_5.latitude, DumbData.POSITION_5.longitude, 0.0f, 23);
        GymDatabaseHelper.getInst().insertLocation(complexLocation);
        complexLocation = new ComplexLocation(DumbData.POSITION_6.latitude, DumbData.POSITION_6.longitude, 0.0f, 23);
        GymDatabaseHelper.getInst().insertLocation(complexLocation);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
//
//        if (UserUtils.isTracking()) {
//            DialogHandler.showSimpleDialog(this, R.string.dialog_tracking_will_stop_title, R.string.dialog_tracking_will_stop_text,
//                    R.string.dialog_ok,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //nothing happens
//                        }
//                    });
//        } else {
        // update the main content by replacing fragments
        Fragment fragment = null;

        Log.e("navigation called", "pos " + position);

        getSupportFragmentManager().popBackStackImmediate();

        switch (position) {
//            case 1:
//                fragment = new RoutinesFragment();
//                mTitle = getString(R.string.routines_selection);
//                getSupportActionBar().setTitle(mTitle);
//                break;
            case 0:
                fragment = new CardioFragment();
                mTitle = getString(R.string.cardio_selection);
                break;
            case 1:
                fragment = new HistoryFragment();
                mTitle = getString(R.string.history_selection);
                break;
//            case 4:
//                fragment = new HistoryFragment();
//                mTitle = "Logs History";
//                getSupportActionBar().setTitle(mTitle);
//                break;
//            case 5:
//                fragment = new ConfiguratorFragment();
//                mTitle = "Your Best Trainer";
//                getSupportActionBar().setTitle(mTitle);
//                break;
            default:
                fragment = new CardioFragment();
                mTitle = getString(R.string.cardio_selection);
        }

        if (currentFragment != null && currentFragment.equals(fragment.getClass().getSimpleName())) {
            return;
        }

        currentFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        setToolbarTitle(mTitle.toString());
//        }
    }

    @Override
    public void onBackPressed() {
        LogUtils.LOGE(TAG, "size:" + currentFragment.getFragmentManager().getBackStackEntryCount());
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }
        if (currentFragment instanceof CardioFragment && currentFragment.getFragmentManager().getBackStackEntryCount() == 0) {
            LogUtils.LOGE(TAG, "in the main screen");
            if (UserUtils.isTracking()) {
                DialogHandler.showConfirmDialog(this,
                        R.string.dialog_discard_workout_title,
                        R.string.dialog_discard_workout_text,
                        R.string.dialog_discard_workout_button,
                        R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    ((CardioFragment) currentFragment).closeConnection(false);
                                    finish();
                                }
                            }
                        });
            } else {
                super.onBackPressed();
            }
        } else if (currentFragment instanceof CardioFragment) {
            LogUtils.LOGE(TAG, "not in the main screen");
            super.onBackPressed();
        } else {
            LogUtils.LOGE(TAG, "other");
            onNavigationDrawerItemSelected(Constants.HOME_FRAGMENT);
        }
    }

    public void setToolbarTitle(String title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setLogo(R.drawable.icon_cardio);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
//            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_cardio, container, false);
//            return rootView;
//        }
//
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((MainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
//    }

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNavigationDrawer();
        ButterKnife.inject(this);

        int actionBar = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTextView = (TextView) getWindow().findViewById(actionBar);

        contentFrame.setVisibility(View.VISIBLE);
    }

    private void initNavigationDrawer() {
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
//        Set up the drawer.
        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        onNavigationDrawerItemSelected(RoutinesFragment.class);
    }

    @Override
    public void onNavigationDrawerItemSelected(final Class<?> fragment) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Update the main content by replacing fragments
                if (fragment != null) {
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.container, Fragment.instantiate(getApplicationContext(), fragment.getName()));
                    transaction.commit();
                }
            }
        });
    }

    public void closeKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View v = getCurrentFocus();
        if (v == null)
            return;
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }*/

}
