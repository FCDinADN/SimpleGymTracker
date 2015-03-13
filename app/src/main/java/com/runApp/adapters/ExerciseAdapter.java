package com.runApp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.fragments.ExerciseFragment;
import com.runApp.fragments.LogWorkoutFragment;
import com.runApp.models.Exercise;
import com.runApp.utils.FontUtils;
import com.runApp.utils.LogUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rares on 28/11/14.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private static final String TAG = ExerciseAdapter.class.getSimpleName();
    private ArrayList<Exercise> exercises;
    private ArrayList<Integer> checkedExercises;
    private Context mContext;
    private Fragment mFragment;
    private Exercise mExercise;
    private int lastPosition = -1;
    private final String ADD = "add_item";
    private final String DELETE = "delete_item";
    private boolean isSelectable = false;

    public ExerciseAdapter(Fragment fragment, ArrayList<Exercise> exercises, Context context, ArrayList<Integer> selectedExercisesIds, boolean isSelectable) {
        this.exercises = exercises;
        mContext = context;
        this.mFragment = fragment;
        if (selectedExercisesIds == null) {
            checkedExercises = new ArrayList<>();
        } else {
            checkedExercises = selectedExercisesIds;
        }
        this.isSelectable = isSelectable;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mExercise = exercises.get(position);
//        setAnimation(holder.container, position, ADD);
        holder.name.setText(mExercise.getName());
        FontUtils.getInst().setRobotoLight(holder.name);
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExercise = exercises.get(position);
                FragmentTransaction fragmentTransaction = mFragment.getActivity().getSupportFragmentManager().beginTransaction();
                Fragment previousFragment = mFragment.getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
                if (previousFragment != null) {
                    fragmentTransaction.remove(previousFragment);
                }
                fragmentTransaction.addToBackStack(null);

                DialogFragment dialogFragment = ExerciseFragment.newInstance();
                Bundle exerciseBundle = new Bundle();
                exerciseBundle.putInt(ExerciseFragment.FRAGMENT_TYPE, ExerciseFragment.NEW_EXERCISE);
                exerciseBundle.putInt(ExerciseFragment.FRAGMENT_TYPE, ExerciseFragment.EDIT_EXERCISE);
                exerciseBundle.putInt(ExerciseFragment.EXERCISE_ID, mExercise.getId());
                exerciseBundle.putString(ExerciseFragment.EXERCISE_NAME, mExercise.getName());
                exerciseBundle.putInt(ExerciseFragment.EXERCISE_TYPE, mExercise.getTypeInt());
                //TODO check for note
                LogUtils.LOGE(TAG, "note:" + mExercise.getNote());
                exerciseBundle.putString(ExerciseFragment.EXERCIST_NOTE, mExercise.getNote());
                dialogFragment.setArguments(exerciseBundle);
                dialogFragment.show(fragmentTransaction, "dialog");
            }
        });
//        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogHandler.showConfirmDialog(mFragment.getActivity(),
//                        R.string.dialog_delete_title,
//                        R.string.dialog_delete_exercise_text,
//                        R.string.dialog_delete,
//                        R.string.dialog_cancel,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (which == DialogInterface.BUTTON_POSITIVE) {
//                                    GymDatabaseHelper.getInst().deleteExercise(mExercise);
//                                    setAnimation(holder.container, position, DELETE);
//                                    exercises.remove(position);
//                                    new Timer().schedule(new TimerTask() {
//                                        @Override
//                                        public void run() {
//                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    notifyItemRemoved(position);
//                                                    notifyItemRangeChanged(position, exercises.size());
//                                                }
//                                            });
//                                        }
//                                    }, 400);
//                                }
//                            }
//                        });
//            }
//        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!checkedExercises.contains(exercises.get(position).getId())) {
                        checkedExercises.add(exercises.get(position).getId());
                    }
                } else {
                    checkedExercises.remove(Integer.valueOf(exercises.get(position).getId()));
                }
            }
        });

        if (isSelectable) {
            holder.checkBox.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.editButton.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.editButton.setLayoutParams(params);
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = mFragment.getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction
//                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                            .replace(R.id.container, new LogWorkoutFragment())
                            .addToBackStack("")
                            .commit();
                    mFragment.getActivity().getSupportFragmentManager().executePendingTransactions();
                }
            });
        }

        if (isChecked(exercises.get(position).getId())) {
            holder.checkBox.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void resetCheckedExercises() {
        checkedExercises = new ArrayList<>();
    }

    public ArrayList<Integer> getCheckedExercises() {
        return checkedExercises;
    }

    private void setAnimation(View viewToAnimate, int position, String type) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right);
        switch (type) {
            case ADD:
                //if the bound view wasn't previously displayed on the screen
                if (position > lastPosition) {
                    viewToAnimate.startAnimation(animation);
                    lastPosition = position;
                }
                break;
            case DELETE:
                animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_right);
                viewToAnimate.startAnimation(animation);
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.exercise_item_divider)
        View divider;
        @InjectView(R.id.exercise_item_layout)
        RelativeLayout container;
        @InjectView(R.id.item_exercise_checkbox)
        CheckBox checkBox;
        @InjectView(R.id.item_exercise_name)
        TextView name;
        @InjectView(R.id.exercise_item_edit)
        ImageButton editButton;
        @InjectView(R.id.exercise_item_delete)
        ImageButton deleteButton;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    private boolean isChecked(int id) {
        if (checkedExercises != null) {
            for (int i = 0; i < checkedExercises.size(); i++) {
                if (checkedExercises.get(i) > id) {
                    return false;
                } else {
                    if (id == checkedExercises.get(i))
                        return true;
                }
            }
        }
        return false;
    }

}
