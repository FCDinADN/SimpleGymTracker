package com.runApp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.fragments.CardioFragment;
import com.runApp.fragments.PathGoogleMapFragment;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.GPSTracker;
import com.runApp.utils.UserUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 20/03/15.
 */
public class CardioActivity extends ActionBarActivity {

    @InjectView(R.id.cardio_timer)
    TextView timer;

    public static final String USE_HXMSENSOR = "use_hxm_sensor";

    private boolean useHxM;
    private PathGoogleMapFragment mPathGoogleMapFragment;
    private GPSTracker tracker;

    CardioFragment mCardioFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);
        ButterKnife.inject(this);

        mPathGoogleMapFragment = new PathGoogleMapFragment();

        useHxM = getIntent().getExtras().getBoolean(USE_HXMSENSOR);

        mCardioFragment = new CardioFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(USE_HXMSENSOR, useHxM);
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
                                    mCardioFragment.closeConnection(false);
                                    finish();
                                }
                            }
                        });
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

}
