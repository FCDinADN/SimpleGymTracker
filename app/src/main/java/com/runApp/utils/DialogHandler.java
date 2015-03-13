package com.runApp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;

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

    public static void showSimpleSelectionDialog(@NonNull Activity activity, @ArrayRes int array, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(array, listener);
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

}
