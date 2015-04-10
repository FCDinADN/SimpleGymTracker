package com.runApp.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.database.GymDatabaseHelper;
import com.runApp.ui.fragments.CardioFragment;
import com.runApp.ui.fragments.HistoryFragment;
import com.runApp.ui.fragments.NavigationDrawerFragment;
import com.runApp.ui.fragments.PathGoogleMapFragment;
import com.runApp.ui.fragments.SettingsFragment;
import com.runApp.ui.fragments.StartActivityFragment;
import com.runApp.models.ComplexLocation;
import com.runApp.pedometer.Utils;
import com.runApp.services.CaloriesService;
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
     * True, when service is running.
     */
    private boolean mIsRunning;

    private Utils mUtils;

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

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.actionbar_background_dark));

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
        getSupportActionBar().setLogo(R.drawable.ic_running);


//        restoreActionBar();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //insert dumb data
//        inserLocations();

//        mUtils = Utils.getInstance();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "[ACTIVITY] onStart");
        super.onStart();
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

        // Read from preferences if the service was running on the last onPause
        mIsRunning = UserUtils.isServiceRunning();
        LogUtils.LOGE(TAG, "[ACTIVITY] running? " + mIsRunning);

        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning) {// && mPedometerSettings.isNewStart()) {
            startStepService();
            bindStepService();
        } else if (mIsRunning) {
            bindStepService();
        }

        UserUtils.setIsServiceRunning(false);

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

        if (currentFragment instanceof CardioFragment && UserUtils.isTracking()) {
            DialogHandler.showSimpleDialog(this, R.string.dialog_tracking_will_stop_title, R.string.dialog_tracking_will_stop_text,
                    R.string.dialog_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing happens
                        }
                    });
        } else {

//        update the main content by replacing fragments
            Fragment fragment = null;

            getSupportFragmentManager().popBackStackImmediate();

            switch (position) {
//            case 1:
//                fragment = new RoutinesFragment();
//                mTitle = getString(R.string.routines_selection);
//                getSupportActionBar().setTitle(mTitle);
//                break;
                case 0:
                    fragment = new StartActivityFragment();
                    mTitle = getString(R.string.cardio_selection);
                    break;
                case 1:
                    fragment = new HistoryFragment();
                    mTitle = getString(R.string.history_selection);
                    break;
                case 2:
                    fragment = new SettingsFragment();
                    mTitle = getString(R.string.settings_selection);
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
                    fragment = new StartActivityFragment();
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
    }

    @Override
    public void onBackPressed() {
        LogUtils.LOGE(TAG, "size:" + currentFragment.getFragmentManager().getBackStackEntryCount());
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }
//        if (currentFragment instanceof CardioFragment && currentFragment.getFragmentManager().getBackStackEntryCount() == 0) {
//            LogUtils.LOGE(TAG, "in the main screen");
//            if (UserUtils.isTracking()) {
//                DialogHandler.showConfirmDialog(this,
//                        R.string.dialog_discard_workout_title,
//                        R.string.dialog_discard_workout_text,
//                        R.string.dialog_discard_workout_button,
//                        R.string.dialog_cancel,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (which == DialogInterface.BUTTON_POSITIVE) {
//                                    ((CardioFragment) currentFragment).closeConnection(false);
//                                    finish();
//                                }
//                            }
//                        });
//            } else {
//                super.onBackPressed();
//            }
//        } else
        if (currentFragment instanceof StartActivityFragment) {
            super.onBackPressed();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            onNavigationDrawerItemSelected(Constants.HOME_FRAGMENT);
        } else {
            super.onBackPressed();
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

    private CaloriesService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((CaloriesService.CaloriesBinder) service).getService();

            mService.registerCallback(mCallback);
//            mService.reloadSettings();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    private CaloriesService.ICallback mCallback = new CaloriesService.ICallback() {
        @Override
        public void stepsChanged(int value) {
            LogUtils.LOGE(TAG, "stepsChanged " + value);
        }

        @Override
        public void caloriesChanged(float value) {

        }
    };

    @Override
    protected void onPause() {
        if (mIsRunning) {
            unbindStepService();
        }
        LogUtils.LOGE(TAG, "[ACTIVITY] onPause service running? " + mIsRunning);
        UserUtils.setIsServiceRunning(mIsRunning);
        super.onPause();
    }

    private void startStepService() {
        Log.i(TAG, "[ACTIVITY] startStepService");
        if (!mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(MainActivity.this,
                    CaloriesService.class));
        }
    }

    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(MainActivity.this,
                CaloriesService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[ACTIVITY] Unbind");
        unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "[ACTIVITY] onStop");
        super.onStop();
    }

    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(MainActivity.this,
                    CaloriesService.class));
        }
        mIsRunning = false;
    }

//    private static final int STEPS_MSG = 1;
//    private static final int CALORIES_MSG = 2;
//
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message message) {
//            switch (message.what) {
//                case STEPS_MSG:
//                    LogUtils.LOGE(TAG, "value " + (int) message.arg1);
//                    break;
//            }
//        }
//    };

}
