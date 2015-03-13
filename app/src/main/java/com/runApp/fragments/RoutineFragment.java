package com.runApp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.runApp.R;
import com.runApp.adapters.ExerciseAdapter;
import com.runApp.models.Exercise;
import com.runApp.models.Routine;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.FontUtils;
import com.runApp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 28/11/14.
 */
public class RoutineFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = RoutineFragment.class.getSimpleName();

    public static final int NEW_ROUTINE = 2003;
    public static final int EDIT_ROUTINE = 2004;
    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final String ROUTINE_ID = "routine_id";
    public static final String ROUTINE_NAME = "exercise_name";
    public static final String ROUTINE_TYPE = "exercise_type";
    public static final String ROUTINE_SELECTED_EXERCISES_IDS = "selected_exercises_ids";
    private final int LOADER_EXERCISES = 2001;
    private final int LOADER_ROUTINES = 2002;
    public static final String RESTART_LOADER = "restart_loader";

    @InjectView(R.id.new_routine_type_spinner)
    Spinner routineTypes;
    //    @InjectView(R.id.new_routine_add_exercise)
//    LinearLayout addExercise;
    @InjectView(R.id.new_routine_exercisesList)
    RecyclerView exercisesList;
    @InjectView(R.id.progress)
    RelativeLayout mProgress;
    @InjectView(R.id.new_routine_no_exercises)
    TextView noExercises;
    @InjectView(R.id.new_routine_select_exercises)
    TextView selectExercisesText;
    @InjectView(R.id.routine_name)
    EditText routineName;
    @InjectView(R.id.new_routine_add_exercise)
    TextView addNewExercises;

    private ExerciseAdapter mExerciseAdapter;
    private ArrayList<Exercise> exercises;
    private ArrayList<String> exercisesNames;
    private ArrayList<String> routinesName;
    private ArrayList<Integer> selectedExercisesIds;
    private int fragmentType;
    private int routineId;
    private String routineNameText;
    private int routineType;
    private RecyclerView.LayoutManager mLayoutManager;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_new_routine, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        exercisesList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(Utils.getContext());
        exercisesList.setItemAnimator(new DefaultItemAnimator());
        exercisesList.setLayoutManager(mLayoutManager);
        fragmentType = getArguments().getInt(FRAGMENT_TYPE);

        LocalBroadcastManager.getInstance(Utils.getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(RESTART_LOADER));
        initfields(view);

        switch (fragmentType) {
            case NEW_ROUTINE:
//                if (getActivity() != null && getActivity().getS() != null) {
//                    getActivity().getActionBar().setTitle(R.string.new_routine);
//                    getActivity().getActionBar().setIcon(R.drawable.app_icon);
                ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.new_routine);
//                }
                routineTypes.setEnabled(true);
                break;
            case EDIT_ROUTINE:
//                if (getActivity() != null && getActivity().getActionBar() != null) {
//                    getActivity().getActionBar().setTitle(R.string.edit_routine);
//                    getActivity().getActionBar().setIcon(R.drawable.app_icon);
                ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.edit_routine);
//                }
                routineId = getArguments().getInt(ROUTINE_ID);
                routineType = getArguments().getInt(ROUTINE_TYPE);
                selectedExercisesIds = getArguments().getIntegerArrayList(ROUTINE_SELECTED_EXERCISES_IDS);
                routineNameText = getArguments().getString(ROUTINE_NAME);
                routineName.setText(routineNameText);
                routineTypes.setSelection(routineType);
                break;
        }

        setHasOptionsMenu(true);
        mProgress.setVisibility(View.VISIBLE);

        exercises = new ArrayList<>();
        mExerciseAdapter = new ExerciseAdapter(this, exercises, getActivity(), selectedExercisesIds, false);
        exercisesList.setAdapter(mExerciseAdapter);
//        exercisesList.setEmptyView(noExercises);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_save) {
            if (routineName.getText().toString().length() > 2) {
                if (!checkName()) {
                    DialogHandler.showSimpleDialog(getActivity(),
                            R.string.dialog_save_exercise_name_exists_title,
                            R.string.dialog_save_exercise_name_exists_text,
                            R.string.dialog_ok,
                            null);
                    return true;
                } else if (mExerciseAdapter.getCheckedExercises().isEmpty()) {
                    DialogHandler.showSimpleDialog(getActivity(),
                            R.string.dialog_no_exercises_title,
                            R.string.dialog_no_exercises_text,
                            R.string.dialog_ok,
                            null);
                } else {
                    closeKeyboard();
                    Routine routine = new Routine(routineName.getText().toString(),
                            (String) routineTypes.getSelectedItem(),
                            mExerciseAdapter.getCheckedExercises());
                    switch (fragmentType) {
                        case NEW_ROUTINE:
                            Toast.makeText(Utils.getContext(), getResources().getString(R.string.new_exercise_successfull), Toast.LENGTH_SHORT).show();
                            break;
                        case EDIT_ROUTINE:
                            routine.setId(routineId);
                            Toast.makeText(Utils.getContext(), getResources().getString(R.string.edit_exercise_successfull), Toast.LENGTH_SHORT).show();
                            break;
                    }
//                    GymDatabaseHelper.getInst().insertRoutine(routine);
//                    Intent intent = new Intent(RoutinesFragment.RESTART_LOADER);
//                    LocalBroadcastManager.getInstance(Utils.getContext()).sendBroadcast(intent);
//                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            } else {
                DialogHandler.showSimpleDialog(getActivity(),
                        R.string.dialog_save_exercise_name_too_short_title,
                        R.string.dialog_save_exercise_name_too_short_text,
                        R.string.dialog_ok,
                        null);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initfields(View view) {
        FontUtils.getInst().setRobotoLightItalian(((TextView) view.findViewById(R.id.routine_item_name_title)));
        FontUtils.getInst().setRobotoLightItalian(((TextView) view.findViewById(R.id.new_routine_type_title)));
        FontUtils.getInst().setRobotoLight(routineName);
        FontUtils.getInst().setRobotoLightItalian(selectExercisesText);
        FontUtils.getInst().setRobotoRegular(addNewExercises);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(Utils.getContext(), R.layout.ui_spinner_item,
                getResources().getStringArray(R.array.new_routine_types));
        adapter.setDropDownViewResource(R.layout.ui_spinner_item);
        routineTypes.setAdapter(adapter);
    }

    @OnClick(R.id.new_routine_add_exercise)
    void addExerciseClicked() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment previousFragment = getFragmentManager().findFragmentByTag("dialog");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);

        DialogFragment dialogFragment = ExerciseFragment.newInstance();
        Bundle exerciseBundle = new Bundle();
        exerciseBundle.putInt(ExerciseFragment.FRAGMENT_TYPE, ExerciseFragment.NEW_EXERCISE);
        dialogFragment.setArguments(exerciseBundle);
        dialogFragment.show(fragmentTransaction, "dialog");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Loader<Cursor> loader;
//        switch (id) {
//            case LOADER_EXERCISES:
//                loader = new CursorLoader(Utils.getContext(), GymDBContract.Exercises.CONTENT_URI, QueryExercises.PROJECTION_SIMPLE, null, null, null);
//                break;
//            case LOADER_ROUTINES:
//                loader = new CursorLoader(Utils.getContext(), GymDBContract.Routines.CONTENT_URI, QueryRoutines.PROJECTION_SIMPLE, null, null, null);
//                break;
//            default:
//                loader = new CursorLoader(Utils.getContext(), GymDBContract.Exercises.CONTENT_URI, QueryExercises.PROJECTION_SIMPLE, null, null, null);
//        }
//        return loader;
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
//            switch (cursorLoader.getId()) {
//                case LOADER_EXERCISES:
//                    exercises = new ArrayList<>();
//                    exercisesNames = new ArrayList<>();
//                    while (cursor.moveToNext()) {
//                        exercisesNames.add(cursor.getString(QueryExercises.NAME));
//                        Exercise exercise = new Exercise(cursor.getInt(QueryExercises.ID),
//                                cursor.getString(QueryExercises.NAME),
//                                cursor.getString(QueryExercises.TYPE),
//                                cursor.getString(QueryExercises.NOTE));
//                        exercises.add(exercise);
//                    }
//                    if (exercises.size() > 0) {
//                        mExerciseAdapter.setExercises(exercises);
//                        mExerciseAdapter.notifyDataSetChanged();
//                        selectExercisesText.setText(R.string.new_routine_select_exercises);
//                    } else {
//                        selectExercisesText.setText(R.string.new_routine_add_exercise);
//                    }
//                    mProgress.setVisibility(View.GONE);
//                    exercisesList.setVisibility(View.VISIBLE);
//                    break;
//                case LOADER_ROUTINES:
//                    routinesName = new ArrayList<>();
//                    while (cursor.moveToNext()) {
//                        routinesName.add(cursor.getString(QueryRoutines.NAME));
//                    }
//                    // to except the case when it is the same as the one clicked
//                    routinesName.remove(routineNameText);
//                    break;
//            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ButterKnife.inject(this, mView);
        mProgress.setVisibility(View.VISIBLE);
        exercisesList.setVisibility(View.GONE);

        getLoaderManager().restartLoader(LOADER_EXERCISES, null, this);
        getLoaderManager().restartLoader(LOADER_ROUTINES, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ButterKnife.reset(this);
        LocalBroadcastManager.getInstance(Utils.getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    private boolean checkName() {
        if (routinesName != null) {
            for (int i = 0; i < routinesName.size(); i++) {
                if (routineName.getText().toString().equals(routinesName.get(i))) {
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
