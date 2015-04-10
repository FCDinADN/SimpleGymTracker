package com.runApp.ui.fragments;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import butterknife.OnCheckedChanged;
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
    CheckBox man;
    @InjectView(R.id.edit_profile_woman)
    CheckBox woman;
    @InjectView(R.id.edit_profile_save)
    TextView save;

    private int tempHeightValue = -1;
    private int tempWeightValue = -1;
    private int tempAgeValue = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        man.setTag(false);
        woman.setTag(false);

        weight.setTag(false);
        height.setTag(false);
        age.setTag(false);

        if (UserUtils.getAlarmTime() != null) {
            alarmTime.setText(SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).format(UserUtils.getAlarmTime()));
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
                if (((boolean) age.getTag())) {
                    age.setText(tempAgeValue + " years old");
                    age.setTag(false);
                } else {
                    age.setText(UserUtils.getUserAge() + " years old");
                }
            }
        }, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                tempAgeValue = numberPicker.getValue();
                age.setTag(true);
            }
        });
    }

    @OnClick(R.id.edit_profile_height)
    void heightClicked() {
        int defaultHeight = (tempHeightValue == -1) ? UserUtils.getUserHeight() : tempHeightValue;
        DialogHandler.showNumberPickerDialog(getActivity(), R.string.settings_height, Constants.MAXIMUM_HEIGHT, Constants.MINIMUM_HEIGHT, defaultHeight, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (((boolean) height.getTag())) {
                    height.setText(tempHeightValue + " cm");
                    height.setTag(false);
                } else {
                    height.setText(UserUtils.getUserHeight() + " cm");
                }
            }
        }, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                tempHeightValue = numberPicker.getValue();
                height.setTag(true);
            }
        });
    }

    @OnClick(R.id.edit_profile_weight)
    void weightClicked() {
        int defaultWeight = (tempWeightValue == -1) ? UserUtils.getUserWeight() : tempWeightValue;
        DialogHandler.showNumberPickerDialog(getActivity(), R.string.settings_weight, Constants.MAXIMUM_WEIGHT, Constants.MINIMUM_WEIGHT, defaultWeight, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (((boolean) weight.getTag())) {
                    weight.setText(tempWeightValue + " kg");
                    weight.setTag(false);
                } else {
                    weight.setText(UserUtils.getUserWeight() + " kg");
                }
            }
        }, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                tempWeightValue = numberPicker.getValue();
                weight.setTag(true);
            }
        });
    }

    @OnCheckedChanged({R.id.edit_profile_man, R.id.edit_profile_woman})
    void genderChanged(CompoundButton view) {
        switch (view.getId()) {
            case R.id.edit_profile_man:
                if (!(boolean) woman.getTag()) {
                    man.setTag(true);
                    if (woman.isChecked()) {
                        woman.setChecked(false);
                        man.setChecked(true);
                    }
                } else {
                    woman.setTag(false);
                }
                break;
            case R.id.edit_profile_woman:
                if (!(boolean) man.getTag()) {
                    woman.setTag(true);
                    if (man.isChecked()) {
                        man.setChecked(false);
                        woman.setChecked(true);
                    }
                } else {
                    man.setTag(false);
                }
                break;
        }
    }

    @OnClick(R.id.edit_profile_save)
    void saveClicked() {
        UserUtils.setUserAge(tempAgeValue);
        UserUtils.setUserHeight(tempHeightValue);
        UserUtils.setUserWeight(tempWeightValue);
        UserUtils.setAlarmTime(alarmTime.getText().toString());
        AlarmReceiver.setAlarm(getActivity());
        Toast.makeText(getActivity(), "Profile saved!", Toast.LENGTH_SHORT).show();
    }
}
