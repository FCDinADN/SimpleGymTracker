package com.runApp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.ui.fragments.AddCommentFragment;
import com.runApp.models.Log;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rares on 11/01/15.
 */
public class LogWorkoutAdapter extends BaseAdapter {

    private Context mContext;
    private Fragment mFragment;
    private ArrayList<Log> mLogs;

    public LogWorkoutAdapter(Context context, Fragment fragment, ArrayList<Log> logs) {
        mContext = context;
        mFragment = fragment;
        mLogs = logs;
    }

    @Override
    public int getCount() {
        return mLogs.size();
    }

    @Override
    public Log getItem(int position) {
        return mLogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_previous_log, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = ((ViewHolder) view.getTag());
        }

        com.runApp.models.Log log = mLogs.get(position);
        if (holder != null) {
            holder.setNumber.setText(log.getLogNumber() + "");
            holder.workout.setText(log.getReps() + " X " + log.getWeight());
            holder.addNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = mFragment.getFragmentManager().beginTransaction();
                    Fragment previousFragment = mFragment.getFragmentManager().findFragmentByTag("dialog");
                    if (previousFragment != null) {
                        fragmentTransaction.remove(previousFragment);
                    }
                    fragmentTransaction.addToBackStack(null);
                    DialogFragment dialogFragment = AddCommentFragment.newInstance();
                    Bundle exerciseBundle = new Bundle();
                    exerciseBundle.putInt(AddCommentFragment.COMMENT_TYPE, AddCommentFragment.LOG_WORKOUT_COMMENT);
                    dialogFragment.setArguments(exerciseBundle);
                    dialogFragment.show(fragmentTransaction, "dialog");
                }
            });
        }
        return view;
    }

    public void setLogs(ArrayList<Log> logs) {
        mLogs = logs;
    }

    static class ViewHolder { //extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_previous_log_set_number)
        TextView setNumber;
        @InjectView(R.id.item_previous_log_set_exercises_number)
        TextView workout;
        @InjectView(R.id.item_preivous_log_add_note)
        ImageView addNote;

        public ViewHolder(View view) {
//            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
