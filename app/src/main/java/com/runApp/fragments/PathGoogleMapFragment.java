package com.runApp.fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.runApp.MainActivity;
import com.runApp.R;
import com.runApp.database.GymDBContract;
import com.runApp.database.GymDatabaseHelper;
import com.runApp.database.QueryLocations;
import com.runApp.models.ComplexLocation;
import com.runApp.utils.DirectionsJSONParser;
import com.runApp.utils.DumbData;
import com.runApp.utils.GPSLocationListener;
import com.runApp.utils.GPSTracker;
import com.runApp.utils.LogUtils;
import com.runApp.utils.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import static com.runApp.database.QueryLocations.PROJECTION_SIMPLE;

/**
 * Created by Rares on 21/02/15.
 */
public class PathGoogleMapFragment extends Fragment implements GPSLocationListener, LoaderManager.LoaderCallbacks<Cursor>
        , OnMapReadyCallback {

    private final String TAG = PathGoogleMapFragment.class.getSimpleName();
    private final int LOADER_LOCATIONS = 3000;

    public static LatLng MY_LOCATION;

    private GoogleMap googleMap;
    private boolean locationGot = false;
    private boolean dataLoaded = false;

    private ArrayList<ComplexLocation> latLngs;

    private LatLngBounds.Builder bld;
    private SupportMapFragment supportMapFragment;
//    private HashMap<Integer, String> responses;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_path_google_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Start the loader
        latLngs = new ArrayList<>();
        bld = new LatLngBounds.Builder();
        if (!locationGot) {
            getLocation();

            //DUMMY DATA to test the map drawing!
//            Timer timer = new Timer();
//            timer.schedule(dumbTimer, 10000, 2000);
//        } else {
//            Log.e(TAG, "DUMMY DATA TAKEN FROM DB");
        }
        getLoaderManager().restartLoader(LOADER_LOCATIONS, null, this);
    }

    private void addLocation(ComplexLocation location) {
        if (dataLoaded) {
            LogUtils.LOGE(TAG, "adding new location " + location.getExerciseNumber());
            latLngs.add(location);
            bld.include(new LatLng(location.getLatitude(), location.getLongitude()));
            updateMap(true);
        }
    }

    // drawOneRouteOnly to draw just one root after receiveing data from DB
    private void updateMap(boolean drawOneRouteOnly) {
//        drawStartStopMarkers(latLngs);
        if (drawOneRouteOnly) {
            if (latLngs.size() > 1) {
                drawOneRoutePath(latLngs.get(latLngs.size() - 2), latLngs.get(latLngs.size() - 1));
            }
        } else {
            drawRoutePath(latLngs);
        }
        recalculateMapBounds();
    }

    private void recalculateMapBounds() {
        LatLngBounds bounds = bld.build();
//        if (getActivity() != null)
//            googleMap.moveCamera(
        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        6 * getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin)));
    }

    private void getLocation() {
        Log.e(TAG, "getLocation");

        // Location stuff - start getting the location when user loads the map
        GPSTracker tracker = ((MainActivity) getActivity()).getTracker();

        if (tracker != null) {
            Log.e("getLocation", "tracker not NULL");

            tracker.setLocationListener(this);
            locationGot = true;


            if (tracker.canGetLocation()) {
                ComplexLocation location = tracker.getLocation();
                if (location != null) {
//                if (longitude != location.getLongitude() && latitude != location.getLatitude()) {
                    LogUtils.LOGE(TAG, "speed:" + location.getSpeed());
                    double latitude = location.getLongitude();
                    double longitude = location.getLatitude();
                    MY_LOCATION = new LatLng(latitude, longitude);

//                    googleMap.setMyLocationEnabled(true);
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(AUREL_VLAICU_4));
//                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                    addLocation(tracker.getLocation());

                    LogUtils.LOGE(TAG, "[locationClick]: lat: " + latitude + " lon: " + longitude);
                } else {
                    //???
                    LogUtils.LOGE(TAG, "location null");
                }
            } else {
                locationGot = false;
                tracker.showSettingsDialog();    // show "enable gps" dialog box
            }
        } else
            Log.e("getLocation", "tracker NULL");
    }

    public void drawOneRoutePath(ComplexLocation locationSource, ComplexLocation locationDestination) {
        LatLng origin = locationSource.getLatLng();
        LatLng dest = locationDestination.getLatLng();

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask task = new DownloadTask();
        ArrayList<String> toSend = new ArrayList<>();
        toSend.add((latLngs.size() - 1) + "");
        toSend.add(url);
        // Start downloading json data from Google Directions API
        task.execute(toSend);
    }

    public void drawRoutePath(ArrayList<ComplexLocation> latLngs) {
        //Start on "Torget" because of issues with route path in city center
        for (int i = 0; i < (latLngs.size() - 1); i++) {
            LatLng origin = latLngs.get(i).getLatLng();
            LatLng dest = latLngs.get(i + 1).getLatLng();

            if (latLngs.get(i).getGoogle_url().equals("")) {
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask task = new DownloadTask();
                ArrayList<String> toSend = new ArrayList<>();
                toSend.add(i + "");
                toSend.add(url);
                // Start downloading json data from Google Directions API
                task.execute(toSend);
            } else {
                // PARSE DIRRECTLY FROM DB
                ParserTask parserTask = new ParserTask();

                LogUtils.LOGE(TAG, "parse from DB" + latLngs.get(i).getGoogle_url());

                ArrayList<String> toParse = new ArrayList<>();
                toParse.add(i + "");
                toParse.add(latLngs.get(i).getGoogle_url());

                // Invokes the thread for parsing the JSON data
                parserTask.execute(toParse);
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        LogUtils.LOGE(TAG, "downloadUrl");
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void locationChanged(ComplexLocation location) {
        LogUtils.LOGE(TAG, "location changed");
        addLocation(location);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader;
        String selection = GymDBContract.LocationsColumns.NUMBER + " = 23";//+ UserUtils.getExerciseNumber();
        loader = new CursorLoader(Utils.getContext(), GymDBContract.Locations.CONTENT_URI, PROJECTION_SIMPLE, selection, null, GymDBContract.Locations.CONTENT_URI_DATE_ORDER);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            switch (cursorLoader.getId()) {
                case LOADER_LOCATIONS:
                    latLngs = new ArrayList<>();
                    ComplexLocation complexLocation = null;
                    while (cursor.moveToNext()) {
                        complexLocation = new ComplexLocation(cursor.getInt(QueryLocations.ID),
                                cursor.getFloat(QueryLocations.LATITUDE),
                                cursor.getFloat(QueryLocations.LONGITUDE),
                                cursor.getFloat(QueryLocations.SPEED),
                                cursor.getInt(QueryLocations.NUMBER),
                                cursor.getString(QueryLocations.GOOGLE_LOCATIONS));
//                        Log.e(TAG, "got @ " + complexLocation.getLatitude() + " , " + complexLocation.getLongitude() + " from " + complexLocation.getExerciseNumber());
                        latLngs.add(complexLocation);
                        bld.include(new LatLng(complexLocation.getLatitude(), complexLocation.getLongitude()));
                    }
                    if (complexLocation != null) {
                        dataLoaded = true;
                        updateMap(false);
                    }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(Utils.getContext());
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
        ArrayList<String> toSend = new ArrayList<>();

        // Downloading data in non-ui thread
        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... url) {

            // For storing data from web service
            String data = "";
            ArrayList<String> urls = url[0];
            toSend.add(urls.get(0));

            try {
                LogUtils.LOGE(TAG, "download " + url[0]);
                // Fetching the data from web service
                data = downloadUrl(urls.get(1));
                toSend.add(data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            // save the urls to avoid over query the api
//            if (data.equals("{   \"error_message\" : \"You have exceeded your daily request quota for this API.\",   \"routes\" : [],   \"status\" : \"OVER_QUERY_LIMIT\"}")) {
//                LogUtils.LOGE(TAG, "limit exceed!");
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                responses.put(Integer.parseInt(urls.get(0)), data);
            ComplexLocation complexLocation = latLngs.get(Integer.parseInt(urls.get(0)));
            complexLocation.setGoogle_url(data);
            GymDatabaseHelper.getInst().insertLocation(complexLocation);
//            }
            return toSend;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            LogUtils.LOGE(TAG, "parse " + result);

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<ArrayList<String>, Integer, List<List<HashMap<String, String>>>> {

        private int coordinateIndex;

        // Parsing the data in non-ui thread
        @Override
        protected final List<List<HashMap<String, String>>> doInBackground(ArrayList<String>... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            ArrayList<String> passed = jsonData[0];
            coordinateIndex = Integer.parseInt(passed.get(0));
//            pointsNumber = Integer.parseInt(passed.get(1));

            try {
                jObject = new JSONObject(passed.get(1));
                LogUtils.LOGE(TAG, "trying to parse the result");
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();

            if (result != null) {
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);

                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);

                    if (latLngs.get(coordinateIndex).getSpeed() == 0.0f)
                        lineOptions.color(Color.BLUE);
                    else lineOptions.color(Color.YELLOW);
                }

                // Drawing polyline in the Google Map for the i-th route
                googleMap.addPolyline(lineOptions);
            }
        }
    }

    // Drawing Start and Stop locations
    private void drawStartStopMarkers(ArrayList<ComplexLocation> markerPoints) {

        for (int i = 0; i < markerPoints.size(); i++) {

            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker
            options.position(markerPoints.get(i).getLatLng());

            // Setting the title of the marker
            options.title(markerPoints.get(i).getSpeed() + "");

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED.
             */
            if (i == 0) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (i == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            // Add new marker to the Google Map Android API V2
            googleMap.addMarker(options);
        }
    }

    @Override
    public void onDestroyView() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.remove(supportMapFragment);
        ft.commitAllowingStateLoss();
        super.onDestroyView();
    }

    private int number;

    final Handler dumbHandker = new Handler();

    private TimerTask dumbTimer = new TimerTask() {
        private ComplexLocation complexLocation;

        @Override
        public void run() {

            dumbHandker.post(new Runnable() {
                                 @Override
                                 public void run() {

                                     LogUtils.LOGE(TAG, "posting...");
                                     switch (number) {
                                         case 0:
                                             complexLocation = new ComplexLocation(DumbData.POSITION_1.latitude, DumbData.POSITION_1.longitude, 0.0f, 23);
                                             break;
                                         case 1:
                                             complexLocation = new ComplexLocation(DumbData.POSITION_2.latitude, DumbData.POSITION_2.longitude, 0.0f, 23);
                                             break;
                                         case 2:
                                             complexLocation = new ComplexLocation(DumbData.POSITION_3.latitude, DumbData.POSITION_3.longitude, 0.0f, 23);
                                             break;
                                         case 3:
                                             complexLocation = new ComplexLocation(DumbData.POSITION_4.latitude, DumbData.POSITION_4.longitude, 0.0f, 23);
                                             break;
                                         case 4:
                                             complexLocation = new ComplexLocation(DumbData.POSITION_5.latitude, DumbData.POSITION_5.longitude, 0.0f, 23);
                                             break;
                                         case 5:
                                             complexLocation = new ComplexLocation(DumbData.POSITION_6.latitude, DumbData.POSITION_6.longitude, 0.0f, 23);
                                             break;
                                     }
                                     number++;
                                     if (number < 6) {
                                         GymDatabaseHelper.getInst().insertLocation(complexLocation);
                                         latLngs.add(complexLocation);
                                         bld.include(new LatLng(complexLocation.getLatitude(), complexLocation.getLongitude()));
                                         updateMap(true);
                                     } else {
                                         dumbTimer.cancel();
                                     }
                                 }
                             }
            );
        }
    };

}
