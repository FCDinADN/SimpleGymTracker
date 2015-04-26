package com.runApp.ui.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rares on 26/04/15.
 */
public class DatePickerDialogFragment extends DialogFragment {

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = calendar == null ? Calendar.getInstance() : calendar;
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of TimePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), mOnDateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        return datePickerDialog;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.mOnDateSetListener = onDateSetListener;
    }

}
