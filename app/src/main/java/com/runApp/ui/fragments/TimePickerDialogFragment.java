package com.runApp.ui.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by Rares on 10/04/15.
 */
public class TimePickerDialogFragment extends DialogFragment {

    private Calendar calendar;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = calendar == null ? Calendar.getInstance() : calendar;
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), onTimeSetListener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }
}
