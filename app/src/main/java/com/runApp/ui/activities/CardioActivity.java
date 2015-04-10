package com.runApp.ui.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.ui.fragments.CardioFragment;
import com.runApp.ui.fragments.PathGoogleMapFragment;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.GPSTracker;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rares on 20/03/15.
 */
public class CardioActivity extends ActionBarActivity {

    @InjectView(R.id.cardio_timer)
    TextView timer;
    @InjectView(R.id.cardio_show_map)
    ImageView showMap;

    public static final String SHORT_ACTIVITY = "short_activity";

    private PathGoogleMapFragment mPathGoogleMapFragment;
    private GPSTracker tracker;

    private CardioFragment mCardioFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);
        ButterKnife.inject(this);

        mPathGoogleMapFragment = new PathGoogleMapFragment();

        mCardioFragment = new CardioFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SHORT_ACTIVITY, getIntent().getBooleanExtra(SHORT_ACTIVITY, false));
        mCardioFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cardio_container, mCardioFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTracker();
    }

    public void setToolbarTitle(String title) {
        if (timer != null) {
            timer.setText(title);
        }
    }

    public void startTracker() {
        tracker = new GPSTracker(this);
    }

    public void stopTracker() {
        tracker.stopUsingGPS();
    }

    public GPSTracker getTracker() {
        return tracker;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//            if (UserUtils.isTracking()) {
            DialogHandler.showConfirmDialog(this,
                    R.string.dialog_discard_workout_title,
                    R.string.dialog_discard_workout_text,
                    R.string.dialog_discard_workout_button,
                    R.string.dialog_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                mCardioFragment.closeConnection(false);
                                finish();
                            }
                        }
                    });
//            } else {
//                super.onBackPressed();
//            }
        } else {
            super.onBackPressed();
        }
    }

    public void showShowMap() {
        showMap.setVisibility(View.VISIBLE);
    }

    public void hideShowMap() {
        showMap.setVisibility(View.GONE);
    }

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;


    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimer() {
        setToolbarTitle("FINISHED");
        customHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int hours = mins / 60;
            setToolbarTitle(String.format("%02d", hours) + ":"
                    + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };


}
