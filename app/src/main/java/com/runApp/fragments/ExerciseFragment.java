package com.runApp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.runApp.R;
import com.runApp.database.GymDBContract;
import com.runApp.database.QueryExercises;
import com.runApp.models.Exercise;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.FontUtils;
import com.runApp.utils.LogUtils;
import com.runApp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

/**
 * Created by Rares on 28/11/14.
 */
public class ExerciseFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ExerciseFragment.class.getSimpleName();

    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final String EXERCISE_ID = "exercise_id";
    public static final String EXERCISE_NAME = "exercise_name";
    public static final String EXERCISE_TYPE = "exercise_type";
    public static final String EXERCIST_NOTE = "exercise_note";
    public static final int NEW_EXERCISE = 1000;
    public static final int EDIT_EXERCISE = 1001;

    public static final int REPETITIONS_TYPE = 0;
    public static final int REPETITIONS_WEIGHT_TYPE = 1;
    public static final int DISTANCE_TYPE = 2;
    public static final int TIME_TYPE = 3;

    @InjectView(R.id.new_exercise_measurement_unit_layout)
    LinearLayout measurementUnitLayout;
    @InjectView(R.id.new_exercise_repetitions_units_layout)
    LinearLayout repetitionsUnitLayout;
    @InjectView(R.id.new_exercise_distance_units_layout)
    LinearLayout distanceUnitLayout;
    @InjectView(R.id.new_exercise_type_spinner)
    Spinner exerciseTypeSpinner;
    @InjectView(R.id.exercise_name)
    EditText exerciseNameView;
    @InjectView(R.id.new_exercise_note)
    TextView exerciseNoteView;
    @InjectView(R.id.item_exercise_name_clear)
    View clear;
    @InjectView(R.id.exercise_error)
    View error;
    @InjectView(R.id.exercise_item_title)
    TextView title;

    private View mView;
    private int LOADER_EXERCISES = 2002;
    private ArrayList<String> exercisesNames;
    //used to know if it is ADD or EDIT
    private int fragmentType;
    //used for editing exercises in DB
    private int exerciseId;
    private String exerciseName;
    private int exerciseType;
    private String exerciseNote;

    public static ExerciseFragment newInstance() {
        return new ExerciseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_exercise, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        fragmentType = getArguments().getInt(FRAGMENT_TYPE);

        initFields();
        switch (fragmentType) {
            case NEW_EXERCISE:
                if (getActivity() != null && getActivity().getActionBar() != null) {
                    title.setText(R.string.new_exercise);
                    getActivity().getActionBar().setIcon(R.drawable.app_icon);
                }
                exerciseTypeSpinner.setEnabled(true);
                break;
            case EDIT_EXERCISE:
                title.setText(R.string.edit_exercise);
                exerciseId = getArguments().getInt(EXERCISE_ID);
                exerciseName = getArguments().getString(EXERCISE_NAME);
                exerciseNameView.setText(exerciseName);
                exerciseType = getArguments().getInt(EXERCISE_TYPE);
                exerciseTypeSpinner.setSelection(exerciseType);
                exerciseNote = getArguments().getString(EXERCIST_NOTE);
                LogUtils.LOGE(TAG, "note from adapter:" + exerciseNote);
                exerciseNoteView.setText(exerciseNote);
                break;
        }
//        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick(R.id.exercise_item_save)
    void saveClicked() {
        if (exerciseNameView.getText().toString().length() > 2) {
            if (!checkName()) {
                DialogHandler.showSimpleDialog(getActivity(),
                        R.string.dialog_save_exercise_name_exists_title,
                        R.string.dialog_save_exercise_name_exists_text,
                        R.string.dialog_ok,
                        null);
            } else {
                closeKeyboard();
                Exercise exercise = new Exercise(exerciseNameView.getText().toString(), ((String) exerciseTypeSpinner.getSelectedItem()), exerciseNoteView.getText().toString());
                switch (fragmentType) {
                    case NEW_EXERCISE:
                        Toast.makeText(Utils.getContext(), getResources().getString(R.string.new_exercise_successfull), Toast.LENGTH_SHORT).show();
                        break;
                    case EDIT_EXERCISE:
                        exercise.setId(exerciseId);
                        Toast.makeText(Utils.getContext(), getResources().getString(R.string.edit_exercise_successfull), Toast.LENGTH_SHORT).show();
                        break;
                }
                LogUtils.LOGE(TAG, "note" + exerciseNoteView.getText().toString());
//                GymDatabaseHelper.getInst().insertExercise(exercise);
//                Intent intent = new Intent(RoutineFragment.RESTART_LOADER);
//                LocalBroadcastManager.getInstance(Utils.getContext()).sendBroadcast(intent);
//                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        } else {
            DialogHandler.showSimpleDialog(getActivity(),
                    R.string.dialog_save_exercise_name_too_short_title,
                    R.string.dialog_save_exercise_name_too_short_text,
                    R.string.dialog_ok,
                    null);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_item_save) {
//            LogUtils.LOGE(TAG, "note save clicked");
//            if (exerciseNameView.getText().toString().length() > 2) {
//                if (!checkName()) {
//                    DialogHandler.showSimpleDialog(getActivity(),
//                            R.string.dialog_save_exercise_name_exists_title,
//                            R.string.dialog_save_exercise_name_exists_text,
//                            R.string.dialog_ok,
//                            null);
//                    return true;
//                } else {
//                    closeKeyboard();
//                    Exercise exercise = new Exercise(exerciseNameView.getText().toString(), ((String) exerciseTypeSpinner.getSelectedItem()), exerciseNoteView.getText().toString());
//                    LogUtils.LOGE(TAG, "note:" + exerciseNoteView.getText().toString());
//                    switch (fragmentType) {
//                        case NEW_EXERCISE:
//                            Toast.makeText(Utils.getContext(), getResources().getString(R.string.new_exercise_successfull), Toast.LENGTH_SHORT).show();
//                            break;
//                        case EDIT_EXERCISE:
//                            exercise.setId(exerciseId);
//                            Toast.makeText(Utils.getContext(), getResources().getString(R.string.edit_exercise_successfull), Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//                    LogUtils.LOGE(TAG, "note" + exerciseNoteView.getText().toString());
//                    GymDatabaseHelper.getInst().insertExercise(exercise);
//                    getActivity().getSupportFragmentManager().popBackStackImmediate();
//                }
//            } else {
//                DialogHandler.showSimpleDialog(getActivity(),
//                        R.string.dialog_save_exercise_name_too_short_title,
//                        R.string.dialog_save_exercise_name_too_short_text,
//                        R.string.dialog_ok,
//                        null);
//            }
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void initFields() {
        FontUtils.getInst().setRobotoLightItalian(((TextView) mView.findViewById(R.id.exercise_item_name_title)));
        FontUtils.getInst().setRobotoLightItalian(((TextView) mView.findViewById(R.id.exercise_item_type_title)));
        FontUtils.getInst().setRobotoLightItalian(((TextView) mView.findViewById(R.id.exercise_item_note_title)));
        FontUtils.getInst().setRobotoLightItalian(((TextView) mView.findViewById(R.id.exercise_item_measurement_title)));
        FontUtils.getInst().setRobotoLight(exerciseNameView);
        FontUtils.getInst().setRobotoLight(exerciseNoteView);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(Utils.getContext(), R.layout.ui_spinner_item,
                getResources().getStringArray(R.array.new_exercise_types));
        adapter.setDropDownViewResource(R.layout.ui_spinner_item);
        exerciseTypeSpinner.setAdapter(adapter);
        initLayout();
    }

    private void initLayout() {
        switch (exerciseType) {
            case REPETITIONS_TYPE:
                measurementUnitLayout.setVisibility(View.GONE);
                break;
            case REPETITIONS_WEIGHT_TYPE:
                measurementUnitLayout.setVisibility(View.VISIBLE);
                repetitionsUnitLayout.setVisibility(View.VISIBLE);
                FontUtils.getInst().setRobotoLight(((TextView) repetitionsUnitLayout.findViewById(R.id.new_exercise_kg_unit)));
                FontUtils.getInst().setRobotoLight(((TextView) repetitionsUnitLayout.findViewById(R.id.new_exercise_second_unit)));
                FontUtils.getInst().setRobotoLight(((TextView) repetitionsUnitLayout.findViewById(R.id.new_exercise_third_unit)));
                distanceUnitLayout.setVisibility(View.GONE);
                break;
            case DISTANCE_TYPE:
                measurementUnitLayout.setVisibility(View.VISIBLE);
                distanceUnitLayout.setVisibility(View.VISIBLE);
                FontUtils.getInst().setRobotoLight(((TextView) distanceUnitLayout.findViewById(R.id.new_exercise_km_unit)));
                FontUtils.getInst().setRobotoLight(((TextView) distanceUnitLayout.findViewById(R.id.new_exercise_ft_unit)));
                repetitionsUnitLayout.setVisibility(View.GONE);
                break;
            case TIME_TYPE:
                measurementUnitLayout.setVisibility(View.GONE);
                break;
        }
    }

    @OnItemSelected(R.id.new_exercise_type_spinner)
    void typedChanged() {
        exerciseType = exerciseTypeSpinner.getSelectedItemPosition();
        initLayout();
    }

    @OnTextChanged(value = R.id.exercise_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterInput(Editable s) {
        if (s.length() > 0) {
            clear.setVisibility(View.VISIBLE);
            if (checkName()) {
//                error.setVisibility(View.GONE);
//                exerciseNameView.setError;
                exerciseNameView.setError(null);
            } else {
//                error.setVisibility(View.VISIBLE);
                exerciseNameView.setError("ERROR");
            }
        } else {
            exerciseNameView.setError(null);
            clear.setVisibility(View.GONE);
//            error.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.item_exercise_name_clear)
    void clearPressed() {
        exerciseNameView.setText("");
    }

    @OnClick(R.id.exercise_error)
    void errorClicked() {
        DialogHandler.showSimpleDialog(getActivity(),
                R.string.dialog_save_exercise_name_exists_title,
                R.string.dialog_save_exercise_name_exists_text,
                R.string.dialog_ok,
                null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        loader = new CursorLoader(Utils.getContext(), GymDBContract.Exercises.CONTENT_URI, QueryExercises.PROJECTION_SIMPLE, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            exercisesNames = new ArrayList<>();
//            while (cursor.moveToNext()) {
//                exercisesNames.add(cursor.getString(QueryExercises.NAME));
//            }
            // to except the case when it is the same as the one clicked
            exercisesNames.remove(exerciseName);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_EXERCISES, null, this);
    }

    private boolean checkName() {
        if (exercisesNames != null) {
            for (int i = 0; i < exercisesNames.size(); i++) {
                if (exerciseNameView.getText().toString().equals(exercisesNames.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void closeKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }
}
