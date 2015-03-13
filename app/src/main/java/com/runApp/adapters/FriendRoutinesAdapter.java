package com.runApp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.fragments.AddCommentFragment;
import com.runApp.fragments.FriendFullProfileFragment;
import com.runApp.models.Routine;
import com.runApp.utils.FontUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Rares on 04/01/15.
 */
public class FriendRoutinesAdapter extends BaseExpandableListAdapter {

    private ArrayList<Routine> mRoutines = new ArrayList<>();
    private ExpandableListView mExpandableListView;
    private final LayoutInflater inflater;
    private final Context mContext;
    private Fragment mFragment;
    private String friendName;

    public FriendRoutinesAdapter(Fragment fragment, Context context, ExpandableListView expandableListView, ArrayList<Routine> routines) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mExpandableListView = expandableListView;
        mRoutines = routines;
        mFragment = fragment;
    }

    public void setFriendName(String name) {
        friendName = name;
    }

    @Override
    public int getGroupCount() {
        return mRoutines.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mRoutines.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return "Bench";
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View view, ViewGroup viewGroup) {
        Routine routine = mRoutines.get(position);
        Holder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_friends_routine, viewGroup, false);
            holder = new Holder(view);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (holder != null) {
            if (mExpandableListView.isGroupExpanded(position)) {
                holder.arrowDown.setVisibility(View.GONE);
                holder.arrowUp.setVisibility(View.VISIBLE);
            } else {
                holder.arrowUp.setVisibility(View.GONE);
                holder.arrowDown.setVisibility(View.VISIBLE);
            }
//            FontUtils.getInst().setRobotoRegular(holder.title);
            holder.title.setText(routine.getName());
        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.item_friend_exercise, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.item_friend_exercise_name)).setText("Exercise 1");
        FontUtils.getInst().setRobotoLight((TextView) view.findViewById(R.id.item_friend_exercise_name));
        FontUtils.getInst().setRobotoLightItalian((TextView) view.findViewById(R.id.item_friend_exercise_add_comment));
        view.findViewById(R.id.item_friend_exercise_add_comment).setOnClickListener(new View.OnClickListener() {
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
                exerciseBundle.putString(FriendFullProfileFragment.FRIEND_NAME, friendName);
                exerciseBundle.putInt(AddCommentFragment.COMMENT_TYPE, AddCommentFragment.FRIEND_COMMENT);
                dialogFragment.setArguments(exerciseBundle);
                dialogFragment.show(fragmentTransaction, "dialog");
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    class Holder {
        @InjectView(R.id.item_friends_routine_title)
        TextView title;
        @Optional
        @InjectView(R.id.item_friends_routine_arrow_down)
        ImageView arrowDown;
        @Optional
        @InjectView(R.id.item_friends_routine_arrow_up)
        ImageView arrowUp;

        public Holder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
