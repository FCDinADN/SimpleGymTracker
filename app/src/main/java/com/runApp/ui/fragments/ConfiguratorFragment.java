package com.runApp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.runApp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by Rares on 12/01/15.
 */
public class ConfiguratorFragment extends Fragment {

    @InjectView(R.id.loseWeight)
    RadioButton loseWeight;
    @InjectView(R.id.maintain)
    RadioButton maintain;
    @InjectView(R.id.gainMuscles)
    RadioButton gainMuscles;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configurator, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        loseWeight.setChecked(true);
    }

    @OnCheckedChanged({R.id.loseWeight, R.id.maintain, R.id.gainMuscles})
    void checkedChanged(CompoundButton view, boolean checked) {
        switch (view.getId()) {
            case R.id.loseWeight:
                if (checked) {
                    if (gainMuscles.isChecked()) {
                        gainMuscles.setChecked(false);
                    } else if (maintain.isChecked()) {
                        maintain.setChecked(false);
                    }
                }
                break;
            case R.id.maintain:
                if (checked) {
                    if (gainMuscles.isChecked()) {
                        gainMuscles.setChecked(false);
                    } else if (loseWeight.isChecked()) {
                        loseWeight.setChecked(false);
                    }
                }
                break;
            case R.id.gainMuscles:
                if (checked) {
                    if (loseWeight.isChecked()) {
                        loseWeight.setChecked(false);
                    } else if (maintain.isChecked()) {
                        maintain.setChecked(false);
                    }
                }
                break;
        }
    }

    @OnClick(R.id.getTips)
    void tipsClicked() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment previousFragment = getFragmentManager().findFragmentByTag("dialog");
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);

        DialogFragment dialogFragment = AddCommentFragment.newInstance();
        Bundle exerciseBundle = new Bundle();
        exerciseBundle.putInt(AddCommentFragment.COMMENT_TYPE, AddCommentFragment.SHOW_TIPS);
        if (loseWeight.isChecked()) {
            exerciseBundle.putString(FriendFullProfileFragment.FRIEND_NAME, "Lose Weight Tips");
        } else if (maintain.isChecked()) {
            exerciseBundle.putString(FriendFullProfileFragment.FRIEND_NAME, "Maintain Weight Tips");
        } else if (gainMuscles.isChecked()) {
            exerciseBundle.putString(FriendFullProfileFragment.FRIEND_NAME, "Gain Muscles Tips");
        }
        dialogFragment.setArguments(exerciseBundle);
        dialogFragment.show(fragmentTransaction, "dialog");
    }
}
