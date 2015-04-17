package com.runApp.ui.fragments;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.runApp.R;
import com.runApp.receivers.AlarmReceiver;
import com.runApp.utils.Constants;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.UserUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 29/03/15.
 */
public class SettingsFragment extends Fragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    @InjectView(R.id.edit_profile_alarm)
    TextView alarmTime;
    @InjectView(R.id.edit_profile_age)
    TextView age;
    @InjectView(R.id.edit_profile_weight)
    TextView weight;
    @InjectView(R.id.edit_profile_height)
    TextView height;
    @InjectView(R.id.edit_profile_man)
    TextView man;
    @InjectView(R.id.edit_profile_woman)
    TextView woman;
    @InjectView(R.id.edit_profile_save)
    TextView save;

    private int tempHeightValue = -1;
    private int tempWeightValue = -1;
    private int tempAgeValue = -1;
    private boolean isUserMan = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        setValues();
    }

    private void setValues() {
        tempAgeValue = UserUtils.getUserAge();
        tempWeightValue = UserUtils.getUserWeight();
        tempHeightValue = UserUtils.getUserHeight();
        age.setText(tempAgeValue + " years old");
        weight.setText(tempWeightValue + " kg");
        height.setText(tempHeightValue + " cm");
        if (!UserUtils.getUserGender().equals("Man")) {
            changeUserGender();
        }
        if (UserUtils.getAlarmTime() != null) {
            alarmTime.setText(SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).format(UserUtils.getAlarmTime()));
        }
    }

    private void changeUserGender() {
        isUserMan = !isUserMan;
        if (isUserMan) {
            man.setBackground(getResources().getDrawable(R.drawable.edittext_underline));
            man.setTextColor(getResources().getColor(R.color.white));
            woman.setBackground(null);
            woman.setTextColor(getResources().getColor(R.color.almost_light_grey));
        } else {
            woman.setBackground(getResources().getDrawable(R.drawable.edittext_underline));
            woman.setTextColor(getResources().getColor(R.color.white));
            man.setBackground(null);
            man.setTextColor(getResources().getColor(R.color.almost_light_grey));
        }
    }

    @OnClick(R.id.edit_profile_alarm)
    void setAlarm() {
        AlarmReceiver.cancelAlarm();
        final TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
        timePickerDialogFragment.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = new GregorianCalendar();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                alarmTime.setText(SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).format(calendar.getTime()));
            }
        });
        timePickerDialogFragment.show(getActivity().getSupportFragmentManager(), "time");
    }

    @OnClick(R.id.edit_profile_age)
    void ageChanged() {
        int defaultAge = (tempAgeValue == -1) ? UserUtils.getUserAge() : tempAgeValue;
        DialogHandler.showNumberPickerDialog(getActivity(), R.string.settings_age, Constants.MAXIMUM_AGE, Constants.MINIMUM_AGE, defaultAge, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                age.setText(tempAgeValue + " years old");
            }
        }, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                tempAgeValue = numberPicker.getValue();
            }
        });
    }

    @OnClick(R.id.edit_profile_height)
    void heightClicked() {
        int defaultHeight = (tempHeightValue == -1) ? UserUtils.getUserHeight() : tempHeightValue;
        DialogHandler.showNumberPickerDialog(getActivity(), R.string.settings_height, Constants.MAXIMUM_HEIGHT, Constants.MINIMUM_HEIGHT, defaultHeight, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                height.setText(tempHeightValue + " cm");
            }
        }, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                tempHeightValue = numberPicker.getValue();
            }
        });
    }

    @OnClick(R.id.edit_profile_weight)
    void weightClicked() {
        int defaultWeight = (tempWeightValue == -1) ? UserUtils.getUserWeight() : tempWeightValue;
        DialogHandler.showNumberPickerDialog(getActivity(), R.string.settings_weight, Constants.MAXIMUM_WEIGHT, Constants.MINIMUM_WEIGHT, defaultWeight, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                weight.setText(tempWeightValue + " kg");
            }
        }, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                tempWeightValue = numberPicker.getValue();
            }
        });
    }

    @OnClick({R.id.edit_profile_man, R.id.edit_profile_woman})
    void genderChanged() {
        changeUserGender();
    }

    @OnClick(R.id.edit_profile_save)
    void saveClicked() {
        UserUtils.setUserAge(tempAgeValue);
        UserUtils.setUserHeight(tempHeightValue);
        UserUtils.setUserWeight(tempWeightValue);
        UserUtils.setAlarmTime(alarmTime.getText().toString());
        if (isUserMan) {
            UserUtils.setUserGender("Man");
        } else {
            UserUtils.setUserGender("Woman");
        }
        AlarmReceiver.setAlarm(getActivity());
        Toast.makeText(getActivity(), "Profile saved!", Toast.LENGTH_SHORT).show();
    }
}
