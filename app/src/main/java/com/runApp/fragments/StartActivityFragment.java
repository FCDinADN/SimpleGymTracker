package com.runApp.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.runApp.R;
import com.runApp.activities.CardioActivity;
import com.runApp.activities.MainActivity;
import com.runApp.models.ComplexLocation;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.GPSTracker;
import com.runApp.utils.LogUtils;
import com.runApp.utils.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rares on 20/03/15.
 */
public class StartActivityFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private SupportMapFragment supportMapFragment;

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
        super.onResume();
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
}
