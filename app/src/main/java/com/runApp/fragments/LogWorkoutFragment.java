package com.runApp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.runApp.R;
import com.runApp.adapters.LogWorkoutAdapter;
import com.runApp.models.Log;
import com.runApp.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 11/01/15.
 */
public class LogWorkoutFragment extends Fragment {

    @InjectView(R.id.log_workout_reps_spinner)
    Spinner repsSpinner;
    @InjectView(R.id.log_workout_weights_spinner)
    Spinner weightsSpinner;
    @InjectView(R.id.log_workout_previous_list_view)
    ListView mListView;

    private ArrayList<Log> logs;
    private LogWorkoutAdapter mLogWorkoutAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_workout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initFields(view);
        logs = new ArrayList<>();
        Log log = new Log("Piept", 1, 10, 15);
        logs.add(log);
        log = new Log("Piept", 2, 8, 20);
        logs.add(log);
        mLogWorkoutAdapter = new LogWorkoutAdapter(getActivity(), this, logs);
        mListView.setAdapter(mLogWorkoutAdapter);
    }

    private void initFields(View view) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 5; i < 100; i = i + 5) {
            numbers.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(Utils.getContext(), R.layout.ui_spinner_item,
                numbers);
        adapter.setDropDownViewResource(R.layout.ui_spinner_item);
        repsSpinner.setAdapter(adapter);
        weightsSpinner.setAdapter(adapter);
    }

    @OnClick(R.id.log_workout_add_log)
    void addLog() {
        Log log = new Log("Piept", logs.get(logs.size() - 1).getLogNumber() + 1, ((Integer) repsSpinner.getSelectedItem()), ((Integer) weightsSpinner.getSelectedItem()));
        logs.add(log);
        mLogWorkoutAdapter.setLogs(logs);
        mLogWorkoutAdapter.notifyDataSetChanged();
    }
}
