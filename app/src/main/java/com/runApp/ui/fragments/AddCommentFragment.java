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
import android.widget.EditText;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.utils.FontUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 04/01/15.
 */
public class AddCommentFragment extends DialogFragment {

    public static final String COMMENT_TYPE = "comment_type";

    public static final int FRIEND_COMMENT = 0;
    public static final int LOG_WORKOUT_COMMENT = 1;
    public static final int SHOW_TIPS = 2;

    @InjectView(R.id.add_comment_title)
    TextView addCommentTitle;
    @InjectView(R.id.add_comment_hint)
    TextView addCommentHint;
    @InjectView(R.id.add_comment_text)
    EditText commentText;

    private String friendName;
    private int commentType = -1;

    public static AddCommentFragment newInstance() {
        return new AddCommentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_comment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        commentType = getArguments().getInt(COMMENT_TYPE);
        switch (commentType) {
            case FRIEND_COMMENT:
                friendName = getArguments().getString(FriendFullProfileFragment.FRIEND_NAME);
                addCommentHint.setText("Help " + friendName + " to improve his routine by leaving him encouraging comments.");
                break;
            case LOG_WORKOUT_COMMENT:
                addCommentHint.setText("Describe here how this exercise was.");
                break;
            case SHOW_TIPS:
                view.findViewById(R.id.got_it).setVisibility(View.VISIBLE);
                friendName = getArguments().getString(FriendFullProfileFragment.FRIEND_NAME);
                addCommentTitle.setText(friendName);
                if (friendName.startsWith("Lo")) {
                    addCommentHint.setText(R.string.lose_weight_tipes);
                } else if (friendName.startsWith("Ma")) {
                    addCommentHint.setText(R.string.mantain_tips);
                } else {
                    addCommentHint.setText(R.string.gain_muscles_tips);
                }
                commentText.setVisibility(View.GONE);
                break;
        }
        FontUtils.getInst().setRobotoLightItalian(addCommentHint);
        FontUtils.getInst().setRobotoLight(((TextView) view.findViewById(R.id.add_comment_text)));
    }


    @OnClick({R.id.add_comment_save, R.id.got_it})
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
