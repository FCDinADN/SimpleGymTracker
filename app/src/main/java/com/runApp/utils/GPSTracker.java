package com.runApp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;

import com.runApp.database.GymDatabaseHelper;
import com.runApp.models.ComplexLocation;

/**
 * Created by Rares on 21/02/15.
 */
public class GPSTracker implements LocationListener {

    private static final String TAG = GPSTracker.class.getSimpleName();

    private final Context context;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    private Location location = null;
    protected LocationManager locationManager;
    private static double latitude;
    private static double longitude;
    private GPSLocationListener mLocationListener;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20; // 20 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 2; // 2 min

    public GPSTracker(Context context) {
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //Register location listeners
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

        getLocation();
    }

    public ComplexLocation getLocation() {
        try {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                LogUtils.LOGE(TAG, "no provider is enabled..");
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    LogUtils.LOGE(TAG, "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
            if (isGPSEnabled) {
                if (location == null) {
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ComplexLocation(latitude, longitude, UserUtils.getActualSpeed(), UserUtils.getExerciseNumber());
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        // called when the listener is notified with a location update from the GPS
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        this.location = location;

        //Add to DB only if it is tracking
        if (UserUtils.isTracking()) {
            ComplexLocation complexLocation = new ComplexLocation(location.getLatitude(), location.getLongitude(), UserUtils.getActualSpeed(), UserUtils.getExerciseNumber());
            if (mLocationListener != null) {
                mLocationListener.locationChanged(complexLocation);
            }
            GymDatabaseHelper.getInst().insertLocation(complexLocation);
        }
//        }
        LogUtils.LOGE(TAG, "[onLocationChanged]: lat: " + latitude + " lon: " + longitude + " speed " + location.getSpeed() + " altitude " + location.getAltitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        // called when the GPS provider is turned off (user turning off the GPS on the phone)
        canGetLocation = false;
    }

    @Override
    public void onProviderEnabled(String provider) {
        // called when the GPS provider is turned on (user turning on the GPS on the phone)
        canGetLocation = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // called when the status of the GPS provider changes
    }

    public void showSettingsDialog(final ActionBarActivity activity) {
        LogUtils.LOGE(TAG, "showDialog");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("dialog title");
        alertDialog.setMessage("dialog message");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.getSupportFragmentManager().popBackStackImmediate();
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void setLocationListener(GPSLocationListener mLocationListener) {
        this.mLocationListener = mLocationListener;
    }

}