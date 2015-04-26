package com.runApp.ui.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.runApp.R;
import com.runApp.models.ComplexLocation;
import com.runApp.ui.activities.CardioActivity;
import com.runApp.ui.activities.MainActivity;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.GPSTracker;
import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;
import com.runApp.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rares on 20/03/15.
 */
public class StartActivityFragment extends Fragment implements OnMapReadyCallback {

    public static final String UPDATE_VALUES = "update_values";

    private GoogleMap googleMap;
    private SupportMapFragment supportMapFragment;

    @InjectView(R.id.start_activity_steps)
    TextView steps;
    @InjectView(R.id.start_activity_calories)
    TextView calories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(mReceiver, new IntentFilter(UPDATE_VALUES));
        steps.setText(UserUtils.getStepsNumber() + " steps today");
        calories.setText(UserUtils.getBurntCalories() + " calories burnt");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void getLocation() {
        if (getActivity() != null) {
            GPSTracker tracker = new GPSTracker(getActivity());
            if (tracker.canGetLocation()) {
                ComplexLocation location = tracker.getLocation();
                googleMap.setMyLocationEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),
                        15));
            } else {
                tracker.showSettingsDialog(((MainActivity) getActivity()));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(Utils.getContext());
        this.googleMap = googleMap;
        LogUtils.LOGE("onMapReady", "yep " + this.googleMap.getUiSettings().isRotateGesturesEnabled());
        getLocation();
    }

    @OnClick(R.id.start_long_activity_btn)
    void startLongActivity() {
        //TODO uncomment below
        if (isBlueetoothEnabled()) {
            Intent intent = new Intent(getActivity(), CardioActivity.class);
            intent.putExtra(CardioActivity.SHORT_ACTIVITY, false);
            startActivity(intent);
        } else {
            DialogHandler.showNoBluetoothDialog(getActivity());
        }
    }

    @Override
    public void onDestroyView() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.remove(supportMapFragment);
        ft.commitAllowingStateLoss();
        super.onDestroyView();
    }

    private boolean isBlueetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            calories.setText(UserUtils.getBurntCalories() + " calories burnt");
            steps.setText(UserUtils.getStepsNumber() + " steps today");
        }
    };
}
