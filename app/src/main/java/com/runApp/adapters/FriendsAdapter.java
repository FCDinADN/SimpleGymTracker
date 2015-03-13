package com.runApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.runApp.R;
import com.runApp.models.Friend;

import java.util.ArrayList;

/**
 * Created by Rares on 04/01/15.
 */
public class FriendsAdapter extends BaseAdapter {

    private ArrayList<Friend> mFriends;
    private Context mContext;

    public FriendsAdapter(ArrayList<Friend> friends, Context context) {
        mFriends = friends;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mFriends.size();
    }

    @Override
    public Friend getItem(int position) {
        return mFriends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.friends_item, parent, false);
        }

        Friend mFriend = mFriends.get(position);
        ((TextView) view.findViewById(R.id.friends_item_ranking)).setText(String.valueOf(mFriend.getRank()));
        ((TextView) view.findViewById(R.id.friends_item_name)).setText(mFriend.getName());
        ((TextView) view.findViewById(R.id.friends_item_score)).setText(String.valueOf(mFriend.getScore()));

        return view;
    }
}
