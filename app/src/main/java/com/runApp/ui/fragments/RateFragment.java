package com.runApp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.utils.FontUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 04/01/15.
 */
public class RateFragment extends DialogFragment {

    @InjectView(R.id.rate_hint)
    TextView addCommentTitle;

    private String friendName;

    public static RateFragment newInstance() {
        return new RateFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rate, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        friendName = getArguments().getString(FriendFullProfileFragment.FRIEND_NAME);
        addCommentTitle.setText("Rate " + friendName + " and help him to rank up.");
        FontUtils.getInst().setRobotoLightItalian(addCommentTitle);
    }


    @OnClick(R.id.rank_save)
    void saveClicked() {
        closeKeyboard();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void closeKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

}

