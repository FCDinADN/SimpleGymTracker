package com.runApp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.adapters.FriendRoutinesAdapter;
import com.runApp.models.Routine;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 04/01/15.
 */
public class FriendFullProfileFragment extends Fragment {

    @InjectView(R.id.friendsRoutines)
    ExpandableListView friendsRoutinesList;
    @InjectView(R.id.friend_full_profile_title)
    TextView title;

    public static final String FRIEND_NAME = "friend_name";

    private String friendName;
    private ArrayList<Routine> routinesList;
    private FriendRoutinesAdapter mFriendRoutinesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_full_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        routinesList = new ArrayList<>();
        friendName = getArguments().getString(FRIEND_NAME);
        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(friendName);
        }
        title.setText(friendName + "'s exercises:");
        routinesList = new ArrayList<>();
        Routine mRoutine = new Routine("Chest", "Workout", null);
        routinesList.add(mRoutine);
        mRoutine = new Routine("Arms", "Workout", null);
        routinesList.add(mRoutine);
        mRoutine = new Routine("Abs", "Workout", null);
        routinesList.add(mRoutine);
        mRoutine = new Routine("Cardio", "Runnning", null);
        routinesList.add(mRoutine);

        mFriendRoutinesAdapter = new FriendRoutinesAdapter(this, getActivity(), friendsRoutinesList, routinesList);
        friendsRoutinesList.setAdapter(mFriendRoutinesAdapter);
        mFriendRoutinesAdapter.setFriendName(friendName);
    }

    @OnClick(R.id.friend_full_profile_rate)
    void rateClicked() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment previousFragment = getFragmentManager().findFragmentByTag("dialog");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);

        DialogFragment dialogFragment = RateFragment.newInstance();
        Bundle exerciseBundle = new Bundle();
        exerciseBundle.putString(FriendFullProfileFragment.FRIEND_NAME, friendName);
        dialogFragment.setArguments(exerciseBundle);
        dialogFragment.show(fragmentTransaction, "dialog");
    }

}
