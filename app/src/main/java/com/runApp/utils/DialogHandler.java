package com.runApp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.runApp.R;

/**
 * Created by Rares on 13.12.14.
 */
public class DialogHandler {


    public static void showSimpleDialog(Activity activity, int title, int text, int posBtn, DialogInterface.OnClickListener callback) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(posBtn, callback);
            builder.setCancelable(false);
            builder.create().show();
        }
    }

    public static void showSimpleDialog(Activity activity, int title, String text, int posBtn, DialogInterface.OnClickListener callback) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(posBtn, callback);
            builder.setCancelable(false);
            builder.create().show();
        }
    }

    public static void showSimpleCancelableDialog(Activity activity, int title, int text, int posBtn, DialogInterface.OnClickListener callback) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(posBtn, callback);
            builder.setCancelable(true);
            builder.create().show();
        }
    }

    public static AlertDialog getSimpleDialog(Activity activity, int title, int text, int posBtn, int negBtn, DialogInterface.OnClickListener callback) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(posBtn, callback);
            builder.setNegativeButton(negBtn, callback);
            builder.setCancelable(true);
            return builder.create();
        }
        return null;
    }

    public static AlertDialog getSimpleDialog(Activity activity, int title, int text, int posBtn, DialogInterface.OnClickListener callback) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(posBtn, callback);
            builder.setCancelable(true);
            return builder.create();
        }
        return null;
    }

    public static void showSimpleDialogNoTitle(Activity activity, int text, int posBtn, DialogInterface.OnClickListener callback) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(text);
            builder.setPositiveButton(posBtn, callback);
            builder.setCancelable(false);
            builder.create().show();
        }
    }

    public static void showConfirmDialog(Activity activity, int title, int text, int posBtn, int negBtn, DialogInterface.OnClickListener callback) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(posBtn, callback);
            builder.setNegativeButton(negBtn, callback);
            builder.setCancelable(true);
            builder.create().show();
        }
    }

    public static void showConfirmDialogWithTextArgs(Activity activity, int title, int text,
                                                     int posBtn, int negBtn, DialogInterface.OnClickListener callback, Object... args) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(activity.getString(text, args));
            builder.setPositiveButton(posBtn, callback);
            builder.setNegativeButton(negBtn, callback);
            builder.setCancelable(true);
            builder.create().show();
        }
    }

    public static void showConfirmDialogNoTitle(Activity activity, int text, int posBtn, int negBtn, DialogInterface.OnClickListener callback) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(text);
            builder.setPositiveButton(posBtn, callback);
            builder.setNegativeButton(negBtn, callback);
            builder.setCancelable(true);
            builder.create().show();
        }
    }

//    public static void showNoConnectionDialog(Activity activity) {
//        if (activity != null) {
//            showSimpleDialog(activity
//                    , R.string.dialog_title_no_connection
//                    , R.string.dialog_text_no_connection
//                    , R.string.dialog_ok, null);
//        }
//    }
//
//    public static void showFreeRegisterDialog(final Activity activity) {
//        if (activity != null) {
//            showConfirmDialog(activity,
//                    R.string.doalog_title_free_register,
//                    R.string.doalog_text_free_register,
//                    R.string.dialog_ok,
//                    R.string.dialog_cancel,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (which == DialogInterface.BUTTON_POSITIVE) {
//                                Intent i = new Intent(activity, IntroActivity.class);
//                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                SessionHandler.getInstance().setFreeMode(false);
//                                activity.startActivity(i);
//                            }
//                        }
//                    });
//        }
//    }
//
//    public interface OnSelectionDialogListener {
//        DialogInterface.OnClickListener loadData(ListView list, View progress);
//    }
//
//    public static void showSelectionDialog(Activity activity, int title, int text, int posBtn, OnSelectionDialogListener loadListener) {
//        if (activity != null) {
//            View content = LayoutInflater.from(activity).inflate(R.layout.ui_dialog_selector, null);
//            TextView textView = (TextView) content.findViewById(R.id.ui_dialog_text);
//            textView.setText(text);
//            ListView list = (ListView) content.findViewById(R.id.ui_dialog_list);
//            View progress = content.findViewById(R.id.progress);
//            final DialogInterface.OnClickListener callback = loadListener.loadData(list, progress);
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//            builder.setTitle(title);
//            builder.setPositiveButton(posBtn, callback);
//            builder.setView(content);
//            final AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//
//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    callback.onClick(alertDialog, position);
//                    alertDialog.dismiss();
//                }
//            });
//        }
//    }

    public static void showSimpleSelectionDialog(@NonNull Activity activity, int title, @ArrayRes int array,int posBtn, int selection, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener buttonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setSingleChoiceItems(array, selection, listener);
        builder.setPositiveButton(posBtn, buttonListener);
        builder.create().show();
    }

    public static void showSimpleSelectionDialog(@NonNull Activity activity, int title, CharSequence[] array,int posBtn, int selection, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener buttonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setSingleChoiceItems(array, selection, listener);
        builder.setPositiveButton(posBtn, buttonListener);
        builder.create().show();
    }

    public static void showMultiSelectionDialog(Activity activity, int title, int array, boolean[] checkedItems, int posBtn, DialogInterface.OnClickListener posListnener, DialogInterface.OnMultiChoiceClickListener selectionListener) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMultiChoiceItems(array, checkedItems, selectionListener);
            builder.setPositiveButton(posBtn, posListnener);
            builder.create().show();
        }
    }

    public static ProgressDialog showProgressDialog(Activity activity, int title, int text, int cancelButton, DialogInterface.OnClickListener clickListener) {
        ProgressDialog progress = new ProgressDialog(activity);
        progress.setTitle(title);
        progress.setMessage(activity.getString(text));
        progress.setMax(100);
        progress.setProgress(0);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(cancelButton), clickListener);
        progress.setCancelable(false);
        progress.show();
        return progress;
    }

    public static void showNoBluetoothDialog(final Activity activity) {
        showConfirmDialog(activity,
                R.string.dialog_no_bluetooth_title,
                R.string.dialog_no_bluetooth_text,
                R.string.dialog_go_to_settings,
                R.string.dialog_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            activity.startActivity(enableBtIntent);
                        }
                    }
                });
    }

    public static void showNumberPickerDialog(final Activity activity, int maxValue, int minValue, int value, DialogInterface.OnClickListener callback, NumberPicker.OnValueChangeListener valueCallback) {
        if (activity != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            final View v = LayoutInflater.from(activity).inflate(R.layout.number_picker, null);
            final NumberPicker np = (NumberPicker) v.findViewById(R.id.numberPicker1);
            final TextView titleView = ((TextView) v.findViewById(R.id.number_picker_title));
            final TextView unitView = ((TextView) v.findViewById(R.id.number_picker_unit));
            np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            builder.setView(v);
            builder.setPositiveButton(R.string.dialog_ok, callback);
            AlertDialog dialog = builder.create();
            dialog.show();
            np.setMaxValue(maxValue);
            np.setMinValue(minValue);
            np.setWrapSelectorWheel(false);
            np.setValue(value);
            np.setOnValueChangedListener(valueCallback);
            switch (minValue) {
                case Constants.MINIMUM_AGE:
                    unitView.setText(R.string.settings_yo);
                    titleView.setText(R.string.settings_age);
                    break;
                case Constants.MINIMUM_HEIGHT:
                    unitView.setText(R.string.settings_cm);
                    titleView.setText(R.string.settings_height);
                    break;
                case Constants.MINIMUM_WEIGHT:
                    unitView.setText(R.string.settings_kg);
                    titleView.setText(R.string.settings_weight);
                    break;
            }
        }
    }
}
