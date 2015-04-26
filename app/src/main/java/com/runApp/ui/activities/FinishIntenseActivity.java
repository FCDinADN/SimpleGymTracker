package com.runApp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.services.CaloriesService;
import com.runApp.ui.fragments.FinishActivityFragment;
import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rares on 22/04/15.
 */
public class FinishIntenseActivity extends FragmentActivity {

    @InjectView(R.id.cardio_timer)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_cardio);

        ButterKnife.inject(this);

        Fragment finishActivity = new FinishActivityFragment();
        Bundle bundle = new Bundle();
        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            bundle.putString(FinishActivityFragment.DATE, extras.getString(FinishActivityFragment.DATE));
            bundle.putString(FinishActivityFragment.START_TIME, extras.getString(FinishActivityFragment.START_TIME));
            bundle.putString(FinishActivityFragment.END_TIME, extras.getString(FinishActivityFragment.END_TIME));
            bundle.putFloat(FinishActivityFragment.CALORIES, extras.getFloat(FinishActivityFragment.CALORIES));
            bundle.putFloat(FinishActivityFragment.DISTANCE, extras.getFloat(FinishActivityFragment.DISTANCE));
            bundle.putString(FinishActivityFragment.DURATION, extras.getString(FinishActivityFragment.DURATION));
            bundle.putFloat(FinishActivityFragment.AVERAGE_SPEED, extras.getFloat(FinishActivityFragment.AVERAGE_SPEED));
            bundle.putFloat(FinishActivityFragment.MAXIMUM_SPEED, extras.getFloat(FinishActivityFragment.MAXIMUM_SPEED));
            bundle.putInt(FinishActivityFragment.AVERAGE_HR, extras.getInt(FinishActivityFragment.AVERAGE_HR));
            bundle.putIntegerArrayList(FinishActivityFragment.HEART_RATE_VALUES, extras.getIntegerArrayList(FinishActivityFragment.HEART_RATE_VALUES));
        }
        finishActivity.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cardio_container, finishActivity)
                .commit();

        title.setText("ACTIVITY FINISHED");

        //Start the service
        LogUtils.LOGE("FinishIntenseActivity", "START SERVICE");
        startService(new Intent(FinishIntenseActivity.this,
                CaloriesService.class));
        UserUtils.setIsServiceRunning(true);
    }
}
