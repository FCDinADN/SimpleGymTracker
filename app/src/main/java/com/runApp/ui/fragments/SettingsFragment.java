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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
    @InjectView(R.id.edit_profile_gender)
    TextView gender;
    @InjectView(R.id.edit_profile_activity_level)
    TextView activityLevelView;
    @InjectView(R.id.edit_profile_goal)
    TextView goalView;
    @InjectView(R.id.edit_profile_save)
    TextView save;

    private int tempHeightValue = -1;
    private int tempWeightValue = -1;
    private int tempAgeValue = -1;
    private String activityLevel;
    private String userGoal;
    private int activityLevelIndex;
    private int goalIndex;

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
            gender.setText(R.string.settings_woman);
        }
        alarmTime.setText(new SimpleDateFormat(Constants.DATE_FORMAT).format(UserUtils.getAlarmTime()).substring(11));
        activityLevel = UserUtils.getUserActivityLevel();
        activityLevelView.setText(activityLevel);
        activityLevelIndex = Arrays.asList(getResources().getStringArray(R.array.activity_levels)).indexOf(activityLevel);
        userGoal = UserUtils.getUserGoal();
        goalView.setText(userGoal);
        goalIndex = Arrays.asList(getResources().getStringArray(R.array.goals)).indexOf(userGoal);
    }

    @OnClick({R.id.edit_profile_set_alarm_layout, R.id.edit_profile_alarm})
    void setAlarm() {
        final TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
        timePickerDialogFragment.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = new GregorianCalendar();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                alarmTime.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));
            }
        });
        timePickerDialogFragment.show(getActivity().getSupportFragmentManager(), "time");
    }

    @OnClick({R.id.edit_profile_age_layout, R.id.edit_profile_age})
    void ageChanged() {
        DialogHandler.showNumberPickerDialog(getActivity(), Constants.MAXIMUM_AGE, Constants.MINIMUM_AGE, tempAgeValue, new DialogInterface.OnClickListener() {
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

    @OnClick({R.id.edit_profile_height_layout, R.id.edit_profile_height})
    void heightClicked() {
        DialogHandler.showNumberPickerDialog(getActivity(), Constants.MAXIMUM_HEIGHT, Constants.MINIMUM_HEIGHT, tempHeightValue, new DialogInterface.OnClickListener() {
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

    @OnClick({R.id.edit_profile_weight_layout, R.id.edit_profile_weight})
    void weightClicked() {
        DialogHandler.showNumberPickerDialog(getActivity(), Constants.MAXIMUM_WEIGHT, Constants.MINIMUM_WEIGHT, tempWeightValue, new DialogInterface.OnClickListener() {
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

    @OnClick({R.id.edit_profile_gender_layout, R.id.edit_profile_gender})
    void genderChanged() {
        if (!gender.getText().toString().equals(getString(R.string.settings_man))) {
            gender.setText(R.string.settings_man);
        } else {
            gender.setText(R.string.settings_woman);
        }
    }

    @OnClick({R.id.edit_profile_activity_level_layout, R.id.edit_profile_activity_level})
    void activityLevelCLicked() {
        DialogHandler.showSimpleSelectionDialog(getActivity(), R.string.settings_activity_level, R.array.activity_levels, R.string.dialog_ok,
                Arrays.asList(getResources().getStringArray(R.array.activity_levels)).indexOf(activityLevel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityLevel = getResources().getStringArray(R.array.activity_levels)[which];
                        activityLevelIndex = which;
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityLevelView.setText(activityLevel);
                    }
                }
        );
    }

    @OnClick({R.id.edit_profile_goal_layout, R.id.edit_profile_goal})
    void goalCLicked() {
        DialogHandler.showSimpleSelectionDialog(getActivity(), R.string.settings_goal, R.array.goals, R.string.dialog_ok,
                Arrays.asList(getResources().getStringArray(R.array.goals)).indexOf(userGoal),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userGoal = getResources().getStringArray(R.array.goals)[which];
                        goalIndex = which;
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goalView.setText(userGoal);
                    }
                }
        );
    }

    @OnClick(R.id.edit_profile_save)
    void saveClicked() {
        AlarmReceiver.cancelAlarm();
        UserUtils.setUserAge(tempAgeValue);
        UserUtils.setUserHeight(tempHeightValue);
        UserUtils.setUserWeight(tempWeightValue);
        UserUtils.setAlarmTime(UserUtils.getTodayDateString().substring(0, 10) + " " + alarmTime.getText().toString());
        UserUtils.setUserGender(gender.getText().toString());
        UserUtils.setUserActivityLevel(activityLevel);
        UserUtils.setUserGoal(userGoal);
        AlarmReceiver.setAlarm(getActivity());
        float neededCalories = caloriesCalculator();
        UserUtils.setUserNeededCalories(neededCalories);
        DialogHandler.showSimpleDialog(getActivity(), R.string.settings_needed_calories_title, neededCalories + "", R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Profile saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @OnClick(R.id.edit_profile_reset)
    void reset() {
        UserUtils.clear();
        setValues();
    }

    private float caloriesCalculator() {
        float BMR;
        if (gender.getText().toString().equals(getString(R.string.settings_man))) {
            BMR = 10 * tempWeightValue + 6.25f * tempHeightValue - 5 * tempAgeValue + 5;
        } else {
            BMR = 10 * tempWeightValue + 6.25f * tempHeightValue - 5 * tempAgeValue - 161;
        }
        switch (activityLevelIndex) {
            case 0:
                BMR *= 1.2;
                break;
            case 1:
                BMR *= 1.35;
                break;
            case 2:
                BMR *= 1.55;
                break;
            case 3:
                BMR *= 1.75;
                break;
            case 4:
                BMR *= 1.95;
                break;
        }
        switch (goalIndex) {
            case 0:
                BMR -= 1000;
                break;
            case 1:
                BMR -= 500;
                break;
            case 3:
                BMR += 500;
                break;
            case 4:
                BMR += 1000;
                break;
        }
        return BMR;
    }
}
