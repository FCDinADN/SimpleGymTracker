package com.runApp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.runApp.R;
import com.runApp.adapters.FriendsAdapter;
import com.runApp.models.Friend;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by Rares on 04/01/15.
 */
public class FriendsFragment extends Fragment {

    @InjectView(R.id.friends_list)
    ListView mListView;

    private ArrayList<Friend> mFriends;
    private FriendsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(R.string.friends_selection);
        }
        mFriends = new ArrayList<>();
        Friend mFriend = new Friend(1, "Alexis", 4.9f);
        mFriends.add(mFriend);
        mFriend = new Friend(2, "Johnny", 4.6f);
        mFriends.add(mFriend);
        mFriend = new Friend(3, "Paul", 4.4f);
        mFriends.add(mFriend);
        mFriend = new Friend(4, "Alex", 4.1f);
        mFriends.add(mFriend);
        mFriend = new Friend(5, "Cristiano", 4f);
        mFriends.add(mFriend);
        mFriend = new Friend(6, "Thierry", 3.9f);
        mFriends.add(mFriend);

        mAdapter = new FriendsAdapter(mFriends, getActivity());
        mListView.setAdapter(mAdapter);
    }

    @OnItemClick(R.id.friends_list)
    void friendClicked(int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = new FriendFullProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FriendFullProfileFragment.FRIEND_NAME, mFriends.get(position).getName());
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }
}
