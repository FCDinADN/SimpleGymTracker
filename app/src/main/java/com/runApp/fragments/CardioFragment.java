package com.runApp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runApp.R;
import com.runApp.activities.CardioActivity;
import com.runApp.database.GymDatabaseHelper;
import com.runApp.models.History;
import com.runApp.models.HxMMessage;
import com.runApp.utils.DialogHandler;
import com.runApp.utils.HxMConnection;
import com.runApp.utils.HxMListener;
import com.runApp.utils.LogUtils;
import com.runApp.utils.UserUtils;
import com.runApp.utils.Utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Rares on 27/11/14.
 */
public class CardioFragment extends Fragment implements HxMListener {
    private final static String TAG = CardioFragment.class.getSimpleName();

    @InjectView(R.id.heartRate_value)
    TextView heartRateValue;
    @InjectView(R.id.distance_value)
    TextView distanceValue;
    @InjectView(R.id.speed_value)
    TextView speedValue;
    @InjectView(R.id.connectButton)
    TextView connect;
    @InjectView(R.id.linlaHeaderProgress)
    LinearLayout mProgress;
    @InjectView(R.id.cardio_device_status)
    TextView status;
    @InjectView(R.id.cardio_date)
    TextView date;
    @InjectView(R.id.cardio_exercise_number)
    TextView exerciseNumber;
    @InjectView(R.id.cardio_battery)
    TextView hxMBattery;

    private boolean pressed;
    private HxMConnection mHxMConnection;
    // value user when starting app and it was started before
    private short initialValue = -1;
    //the very previous value sent from server
    private short previousValue = -1;
    private float maxSpeed = -1;
    private float valueTravelledBefore = 0;
    private float intermediateValue = 0;
    private boolean showBattery = false;
    private boolean dataInitialised = false;
    private boolean mapShown;
    private CubicLineChartFragment historyChartFragment;
    private int seconds = 0;
    private int minutes = 0;
    private Timer timer;
    private TimerTask timerTask;

    private ImageView showMapImage;


    //values used for chart
    private ArrayList<Integer> entryValues;

    private Date startTime;

    private Handler mHandler;

    private static final Style INFINITE = new Style.Builder().
            setBackgroundColorValue(Style.holoBlueLight).build();
    private static final Configuration CONFIGURATION_INFINITE = new Configuration.Builder()
            .setDuration(Configuration.DURATION_INFINITE)
            .build();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cardio, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
//        setHasOptionsMenu(true);

        historyChartFragment = new CubicLineChartFragment();

        mHxMConnection = new HxMConnection(this);
        mHxMConnection.setHxMListener(this);

        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getActivity() != null) {
                    if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 1) {
                        if (mapShown) {
                            setHasOptionsMenu(false);
                            ((CardioActivity) getActivity()).setToolbarTitle(getString(R.string.map_selection));
                        } else {
                            setHasOptionsMenu(true);
                            ((CardioActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection));
                        }
                    } else if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 2) {
                        mapShown = false;
                        setHasOptionsMenu(false);
                    } else {
                        mapShown = false;
                        setHasOptionsMenu(true);
                        ((CardioActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection));
                    }
                }
            }
        });

        showMapImage = ((ImageView) getActivity().findViewById(R.id.cardio_show_map));
        showMapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable()) {
                    mapShown = true;
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.cardio_container, new PathGoogleMapFragment())
                            .addToBackStack("")
                            .commit();
                    getActivity().getSupportFragmentManager().executePendingTransactions();
                } else {
                    Toast.makeText(Utils.getContext(), "network unavailable!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!pressed) {
            tryToConnectToHxM();
        }
    }

    @OnClick(R.id.connectButton)
    void connectClicked() {
        if (!pressed) {
            tryToConnectToHxM();
        } else {
            closeConnection(true);
        }
    }

    private void tryToConnectToHxM() {
//        ((MainActivity) getActivity()).startTracker();
        mProgress.setVisibility(View.VISIBLE);
        connect.setText("CONNECTING...");
        pressed = true;
        mHxMConnection.findBT();
    }

    private void initData() {
        if (UserUtils.getDeviceBattery() < 0) {
            showBattery = true;
        } else {
            hxMBattery.setText(UserUtils.getDeviceBattery() + " %");
        }
        maxSpeed = -1;
        initialValue = -1;
        previousValue = -1;
        valueTravelledBefore = 0.0f;
        intermediateValue = 0;
        UserUtils.setExerciseNumber(UserUtils.getExerciseNumber() + 1);
        exerciseNumber.setText(UserUtils.getExerciseNumber() + "");
        date.setText(UserUtils.getDate());
        startTime = new Date();
        entryValues = new ArrayList<>();
    }

//    @OnClick(R.id.mapButton)
//    void showMap() {
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, new PathGoogleMapFragment())
//                .addToBackStack("")
//                .commit();
//        getActivity().getSupportFragmentManager().executePendingTransactions();
//    }

    @OnClick(R.id.cardio_stats)
    void statsClicked() {
        Bundle mBundle = new Bundle();
        mBundle.putInt(CubicLineChartFragment.EXERCISE_NUMBER, UserUtils.getExerciseNumber());
        mBundle.putString(CubicLineChartFragment.EXERCISE_DATE, UserUtils.getDate());
        mBundle.putIntegerArrayList(CubicLineChartFragment.EXERCISE_VALUES, entryValues);
        historyChartFragment.setArguments(mBundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.cardio_container, historyChartFragment)
                .addToBackStack("")
                .commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void sendMessage(final HxMMessage hxMMessage) {
        if (!UserUtils.isTracking()) {
            UserUtils.setIsTracking();
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {

//                if (seconds < 10 && minutes < 10) {
//                    ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection) + " - 0" + String.valueOf(minutes) + ":0" + String.valueOf(seconds));
//                } else if (seconds < 10 && minutes > 10) {
//                    ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection) + " - " + String.valueOf(minutes) + ":0" + String.valueOf(seconds));
//                }
//                if (seconds == 59) {
//                    seconds = 0;
//                    minutes += 1;
//                } else {
//                    seconds++;
//                }

                //it executes only once
                if (!dataInitialised) {
                    initData();
                    dataInitialised = true;
                } else {

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                    long startTimeInMillis = (int) (System.currentTimeMillis() - startTime.getTime());
                    String time;
                    String h = (int) ((startTimeInMillis / 1000) / 3600) + "";
                    String m = (int) (((startTimeInMillis / 1000) / 60) % 60) + "";
                    String s = (int) ((startTimeInMillis / 1000) % 60) + "";

                    if (h.length() == 1) {
                        h = "0" + h;
                    }
                    if (m.length() == 1) {
                        m = "0" + m;
                    }
                    if (s.length() == 1) {
                        s = "0" + s;
                    }
//                    if (startTimeInMillis / 3600 < 10) {
//                        time = "0" + startTimeInMillis / 3600;
//                    } else {
//                        time = "" + startTimeInMillis / 3600;
//                    }
//                    if ((((startTimeInMillis / 1000) / 60) % 60) < 10) {
//                        time += ":0" + ((startTimeInMillis / 1000) / 60) % 60;
//                    } else {
//                        time += ":" + ((startTimeInMillis / 1000) / 60) % 60;
//                    }
//                    if (((startTimeInMillis / 1000) % 60) < 10) {
//                        time += ":0" + (startTimeInMillis / 1000) % 60;
//                    } else {
//                        time += ":" + (startTimeInMillis / 1000) % 60;
//                    }
//                    calendar.setTimeInMillis(System.currentTimeMillis() - startTime.getTime());
//                    LogUtils.LOGE(TAG, "start tme:" + startTime.getTime() + " - " + System.currentTimeMillis() + " millis:" + (System.currentTimeMillis() - startTime.getTime()) + " result: " + new SimpleDateFormat("hh:mm:ss").format(calendar.getTime()));
//                    ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection) + " - " + new SimpleDateFormat("hh:mm:ss").format(calendar.getTime()));
//                    ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection) + " - " + time);
//                    ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection) + " - " + h + ":" + m + ":" + s);
//                    Style style = new Style.Builder()
//                            .setBackgroundColor(R.color.actionbar_background)
//                            .setGravity(Gravity.CENTER_HORIZONTAL)
//                            .setTextColor(R.color.black)
//                            .setHeight(heightInPx)
//                            .build();
//                    Crouton crouton;
//                    crouton = Crouton.makeText(getActivity(), h + ":" + m + ":" + s, INFINITE)
//                            .setConfiguration(new Configuration.Builder()
//                                    .setDuration(Configuration.DURATION_INFINITE)
//                                    .build());
//                    crouton = Crouton.make(getActivity(), connect, R.id.connectButton, CONFIGURATION_INFINITE);
//                    crouton.show();

                    ((CardioActivity) getActivity()).setToolbarTitle(h + ":" + m + ":" + s);

                    //used for map
                    UserUtils.setActualSpeed(hxMMessage.getSpeed());
                    historyChartFragment.addEntry((float) hxMMessage.getHeartRate());
                    mProgress.setVisibility(View.GONE);
                    connect.setText("DISCONNECT");
                    GymDatabaseHelper.getInst().insertHeartRate(hxMMessage);
                    mProgress.setVisibility(View.GONE);
                    heartRateValue.setText(hxMMessage.getHeartRate() + "");

                    if (hxMMessage.getBattery() < UserUtils.getDeviceBattery()) {
                        UserUtils.setDeviceBattery(hxMMessage.getBattery());
                    }

                    if (showBattery) {
                        showBattery = false;
                        hxMBattery.setText(hxMMessage.getBattery() + " %");
                    }

                    entryValues.add(hxMMessage.getHeartRate());

                    if (initialValue == -1) {
                        initialValue = hxMMessage.getDistance();
                    }

//                    Log.e("@sendMessage", "hxm [" + hxMMessage.getDistance() + "], previous [" + previousValue + " ], traveledBefore [" + valueTravelledBefore + "], " +
//                            "intermediate value [" + intermediateValue + "]");
                    if (hxMMessage.getDistance() < previousValue) {
                        previousValue = hxMMessage.getDistance();
                        initialValue = 0;
                        //TODO here was
//                        valueTravelledBefore += intermediateValue;
                        valueTravelledBefore = intermediateValue;
                    } else {
                        previousValue = hxMMessage.getDistance();
                        intermediateValue = valueTravelledBefore + (((hxMMessage.getDistance() - initialValue) * 6.25f) / 100.0f);
//                        intermediateValue += (((hxMMessage.getDistance() - initialValue) * 6.25f) / 100.0f);
                        distanceValue.setText(new DecimalFormat("##.##").format(intermediateValue));
//                        LogUtils.LOGE(TAG, "final value:[" + intermediateValue + "]");
                    }

                    if (hxMMessage.getSpeed() > maxSpeed) {
                        maxSpeed = hxMMessage.getSpeed();
                    }
                    status.setText("maximum speed " + new DecimalFormat("##.##").format(maxSpeed));

                    speedValue.setText(new DecimalFormat("##.##").format(hxMMessage.getSpeed()));
                }
            }
        });
    }

    @Override
    public void socketClosed() {
        if (pressed) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
            mProgress.setVisibility(View.GONE);
            DialogHandler.showSimpleDialog(getActivity(), R.string.dialog_socket_closed_title, R.string.dialog_socket_closed_text,
                    R.string.dialog_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            closeConnection(true);
                        }
                    });
//                }
//            });
        }
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void setStatusMessage(String s) {
        status.setText(s);
    }

    @Override
    public void resetValues() {
        heartRateValue.setText("?");
        distanceValue.setText("?");
        speedValue.setText("?");
        exerciseNumber.setText("");
        date.setText("");
        hxMBattery.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.LOGE("onPause", "yep");
    }

    @Override
    public void setProgressVisibility(int visibility) {
        mProgress.setVisibility(visibility);
    }

    @OnClick(R.id.linlaHeaderProgress)
    void progressClicked() {
        //do nothing
    }

    public void closeConnection(boolean saveToDB) {
        minutes = 0;
        seconds = 0;
        ((CardioActivity) getActivity()).stopTracker();
        connect.setText("CONNECT");
        pressed = false;
        dataInitialised = false;
        UserUtils.setActualSpeed(0.0f);
        historyChartFragment.resetValues();
        if (initialValue != -1 && saveToDB) {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            GymDatabaseHelper.getInst().insertExercise(new History(UserUtils.getExerciseNumber(), df.format(startTime.getTime()), df.format(new Date().getTime()), intermediateValue));
            UserUtils.setIsTracking();
        }
        try {
            mHxMConnection.closeBT();
            if (!saveToDB) {
                UserUtils.setIsTracking();
                getActivity().finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.maps_menu, menu);
//    }
//
//    @DebugLog
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_item_maps) {
//            if (Utils.isNetworkAvailable()) {
//                mapShown = true;
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .add(R.id.cardio_container, new PathGoogleMapFragment())
//                        .addToBackStack("")
//                        .commit();
//                getActivity().getSupportFragmentManager().executePendingTransactions();
//            } else {
//                Toast.makeText(Utils.getContext(), "network unavailable!", Toast.LENGTH_LONG).show();
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    private void startTimer() {
//        //Declare the timer
//        Timer t = new Timer();
//        //Set the schedule function and rate
//        t.scheduleAtFixedRate(new TimerTask() {
//
//            @Override
//            public void run() {
//                getActivity().runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        LogUtils.LOGE("run", "seconds:" + seconds);
//
//                        ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection) + " - " + String.valueOf(minutes) + ":" + String.valueOf(seconds));
//                        if (seconds == 59) {
////                            ((MainActivity) getActivity()).setToolbarTitle(getString(R.string.cardio_selection) + " - " + String.valueOf(minutes) + ":" + String.valueOf(seconds));
////                            seconds = 60;
//                            minutes += 1;
//                            seconds = 0;
////                            minutes = minutes - 1;
//                        } else {
//                            seconds += 1;
//                        }
//                    }
//
//                });
//            }
//
//        }, 0, 1000);
//    }

//    private final int HEART_RATE = 0;
//    private final int INSTANT_SPEED = 1;
//    private final int DISTANCE = 2;
//
//    private float previousDistance = -1;
//    private float totalDistance;
//    private int heartRateValueInteger;
//    private BufferedReader mBufferedReader = null;
//
//    private BluetoothSocket mSocket = null;
//    private static final String UUID_SERIAL_PORT_PROFILE
//            = "00:07:80:9D:8A:E8";
            /*Sending a message to android that we are going to initiate a pairing request*/
//        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
            /*Registering a new BTBroadcast receiver from the Main Activity context with pairing request event*/
//        getActivity().registerReceiver(new BTBroadcastReceiver(), filter);
//        Registering the BTBondReceiver in the application that the status of the receiver
//        has changed to Paired
//        IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
//        getActivity().registerReceiver(new BTBondReceiver(), filter2);
//        pressed = false;
//        timer = new Timer();
//        ((TextView) view.findViewById(R.id.heartRate_value)).setText("?");
//        ((TextView) view.findViewById(R.id.distance_value)).setText("?");
//        ((TextView) view.findViewById(R.id.speed_value)).setText("?");
//                                       }
//                if (!pressed) {
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    DialogHandler.showSimpleDialog(getActivity(),
//                                            R.string.cardio_dialog_connection_successful_title,
//                                            R.string.cardio_dialog_connection_successful_text,
//                                            R.string.cardio_dialog_connection_successful_ready,
//                                            new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    mProgress.setVisibility(View.GONE);
//                                                    connect.setText("DISCONNECT!");
//                                                    ((TextView) view.findViewById(R.id.heartRate_value)).setText("82");
//                                                    ((TextView) view.findViewById(R.id.distance_value)).setText("10 m");
//                                                    ((TextView) view.findViewById(R.id.speed_value)).setText("5 km/h");
//                                                }
//                                            });
//                                }
//                            });
//                        }
//                    }, 2000);
//                    mProgress.setVisibility(View.VISIBLE);
//                    connect.setText("CONNECTING...");
//                } else {
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mProgress.setVisibility(View.GONE);
//                                    connect.setText("CONNECT!");
//                                    ((TextView) view.findViewById(R.id.heartRate_value)).setText("?");
//                                    ((TextView) view.findViewById(R.id.distance_value)).setText("?");
//                                    ((TextView) view.findViewById(R.id.speed_value)).setText("?");
//                                }
//                            });
//                        }
//                    }, 1000);
//                    mProgress.setVisibility(View.VISIBLE);
//                    connect.setText("DISCONNECTING...");
//                }
//
//                pressed = !pressed;

//                                           if (!connected) {

//                                           mProgress.setVisibility(View.VISIBLE);

//                                           String BhMacID = "00:07:80:9D:8A:E8";
//                                           //String BhMacID = "00:07:80:88:F6:BF";
//                                           adapter = BluetoothAdapter.getDefaultAdapter();
//
//                                           Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
//
//                                           if (pairedDevices.size() > 0)
//
//                                           {
//                                               for (BluetoothDevice device : pairedDevices) {
//                                                   if (device.getName().startsWith("HXM")) {
//                                                       Log.e("found!", "device found");
//                                                       Toast.makeText(getActivity(), "device found!", Toast.LENGTH_LONG).show();
//
//                                                       BhMacID = device.getAddress();
//                                                       break;
//
//                                                   } else {
//                                                       Log.e("found!", "device not found");
//                                                       Toast.makeText(getActivity(), "device not found!", Toast.LENGTH_LONG).show();
//                                                   }
//                                               }
//                                           }
//                                           //MY IMPLEMENTATION
//                                           final BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
//                                           try {
//                                               openDeviceConnection(Device);
//                                           } catch (IOException e) {
//                                               e.printStackTrace();
//                                           }


//                                           try {
//                                               LogUtils.LOGE("from HxM", mBufferedReader.readLine());
//                                           } catch (IOException e) {
//                                               e.printStackTrace();
//                                           }

//FROM HERE USING HXM
//                                               //BhMacID = btDevice.getAddress();
//                                               BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
//                                               String DeviceName = Device.getName();
//                                               _bt = new
//
//                                                       BTClient(adapter, BhMacID);
//
//                                               _NConnListener = new NewConnectorListener(Newhandler, Newhandler);
//
//                                               _bt.addConnectedEventListener(_NConnListener);
//
////        heartRate.setText("000");
//
//                                               //tv1 = 	(EditText)findViewById(R.id.labelSkinTemp);
////                                           sp.setText("0.0");
//
//                                               //tv1 = 	(EditText)findViewById(R.id.labelPosture);
////                                           tv1.setText("000");
//
//                                               //tv1 = 	(EditText)findViewById(R.id.labelPeakAcc);
////                                           tv1.setText("0.0");
//                                               if (_bt.IsConnected())
//
//                                               {
//                                                   mProgress.setVisibility(View.GONE);
//
//                                                   _bt.start();
//                                                   TextView tv = (TextView) myView.findViewById(R.id.cardio_device_status);
//                                                   String ErrorText = "Connected to HxM " + DeviceName;
//                                                   status.setText(ErrorText);
//
//                                                   connected = true;
//                                                   connect.setText("Disconnect");
//
//                                                   //Reset all the values to 0s
//
//                                               } else
//
//                                               {
//                                                   TextView tv = (TextView) myView.findViewById(R.id.cardio_device_status);
//                                                   String ErrorText = "Unable to Connect !";
//                                                   status.setText(ErrorText);
//                                                   connected = false;
//                                               }
//                                           } else {
//                                               connect.setText("Connect");
//                                               connected = false;
//                                               ((TextView) view.findViewById(R.id.heartRate_value)).setText("?");
//                                               ((TextView) view.findViewById(R.id.distance_value)).setText("?");
//                                               ((TextView) view.findViewById(R.id.speed_value)).setText("?");
//                                               _bt.removeConnectedEventListener(_NConnListener);
//                                               _bt.Close();
//                                           }


//    private class BTBondReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Bundle b = intent.getExtras();
//            BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
//            Log.e("Bond state", "BOND_STATED = " + device.getBondState());
//        }
//    }
//
//    private class BTBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.e("BTIntent", intent.getAction());
//            Bundle b = intent.getExtras();
//            Log.e("BTIntent", b.get("android.bluetooth.device.extra.DEVICE").toString());
//            Log.e("BTIntent", b.get("android.bluetooth.device.extra.PAIRING_VARIANT").toString());
//            try {
//                BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
//                Method m = BluetoothDevice.class.getMethod("convertPinToBytes", new Class[]{String.class});
//                byte[] pin = (byte[]) m.invoke(device, "1234");
//                m = device.getClass().getMethod("setPin", new Class[]{pin.getClass()});
//                Object result = m.invoke(device, pin);
//                Log.e("BTTest", result.toString());
//            } catch (SecurityException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            } catch (NoSuchMethodException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            } catch (IllegalArgumentException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//
//    final Handler Newhandler = new Handler() {
//        public void handleMessage(Message msg) {
//            TextView tv;
//            switch (msg.what) {
//                case HEART_RATE:
//                    String HeartRatetext = msg.getData().getString("HeartRate");
//                    tv = (TextView) myView.findViewById(R.id.heartRate_value);
//                    if (tv != null) tv.setText(HeartRatetext);
//                    break;
//                case INSTANT_SPEED:
//                    String SpeedText = msg.getData().getString("InstantSpeed");
//                    tv = (TextView) myView.findViewById(R.id.speed_value);
//                    if (tv != null) tv.setText(SpeedText);
//                    break;
//                case DISTANCE:
//                    String DistanceText = msg.getData().getString("Distance");
//                    tv = (TextView) myView.findViewById(R.id.distance_value);
//                    if (initialDistance == -1) {
//                        initialDistance = Float.parseFloat(DistanceText);
//                        LogUtils.LOGE("initial:", initialDistance + "");
//                    } else {
//                        tv.setText((Float.parseFloat(DistanceText) - initialDistance) + "");
//                    }
//                    break;
//            }
//        }
//    };

//    private void openDeviceConnection(BluetoothDevice aDevice)
//            throws IOException {
//        InputStream aStream = null;
//        InputStreamReader aReader = null;
//        try {
//            mSocket = aDevice
//                    .createRfcommSocketToServiceRecord(getSerialPortUUID());
//            mSocket.connect();
//            aStream = mSocket.getInputStream();
//            aReader = new InputStreamReader(aStream);
//            mBufferedReader = new BufferedReader(aReader);
////            int i = -1;
////            int counter = 0;
////            while ((i = aStream.read()) != -1) {
////                byte b = ((byte) i);
////                if (counter == 60) {
////                    LogUtils.LOGE("OPen connection", "§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
////                    counter = 0;
////                } else if (counter == 12) {
//////                    heartRateValue.setText(i + "");
////                    heartRateValueInteger = i;
//////                    ((TextView) myView.findViewById(R.id.heartRate_value)).setText(i + "");
////                    LogUtils.LOGE("open connection", "hear rate " + b + " counter = " + counter);
////                }
//////                LogUtils.LOGE("open connection", "have byte " + b + " counter = " + counter);
////                counter++;
////            }
//        } catch (IOException e) {
//            Log.e("CARDIO", "Could not connect to device", e);
//            close(mBufferedReader);
//            close(aReader);
//            close(aStream);
//            close(mSocket);
//            throw e;
//        }
//    }
//
//    BluetoothAdapter mBluetoothAdapter;
//    BluetoothSocket mmSocket;
//    BluetoothDevice mmDevice;
//    OutputStream mmOutputStream;
//    DataInputStream mmDataInputStream;
//    InputStream mmInputStream;

//    void findBT() {
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter == null) {
//            status.setText("No bluetooth adapter available");
//        }
//
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBluetooth, 0);
//        }
//
//        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice device : pairedDevices) {
//                if (device.getName().startsWith("HXM")) {
//                    mmDevice = device;
//                    break;
//                }
//            }
//        }
//        status.setText("Bluetooth Device Found");
//    }
//
//    void openBT() throws IOException {
//        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
//        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
//        mmSocket.connect();
//        mmOutputStream = mmSocket.getOutputStream();
//        mmInputStream = mmSocket.getInputStream();
//        mmDataInputStream = new DataInputStream(mmSocket.getInputStream());
//
//        beginListenForData();
//
//        status.setText("Bluetooth Opened");
//    }

//    private void close(Closeable aConnectedObject) {
//        if (aConnectedObject == null) return;
//        try {
//            aConnectedObject.close();
//        } catch (IOException e) {
//        }
//        aConnectedObject = null;
//    }
//
//    private UUID getSerialPortUUID() {
//        return UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    }

//    Thread workerThread;
//    byte[] readBuffer;
//    int readBufferPosition;
//    volatile boolean stopWorker;
//
//    private void beginListenForData() {
//        final Handler handler = new Handler();
//        stopWorker = false;
//        readBufferPosition = 0;
//        readBuffer = new byte[1024];
//        mProgress.setVisibility(View.GONE);
//        workerThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int i;
//                int counter = 0;
//                try {
//                    while (!Thread.currentThread().isInterrupted() && (i = mmInputStream.read()) != -1) {
////                    while (!Thread.currentThread().isInterrupted() && (i = mmDataInputStream.read()) != -1) {
//                        final byte b = ((byte) i);
//                        if (counter == 59) {
////                            LogUtils.LOGE("counter " + counter, "reset + send");
//                            sendBuffer(readBuffer, handler);
//                            readBuffer = new byte[1024];
//                            counter = 0;
//                        } else {
////                            byte[] intData = new byte[2];
////
////                            for (int j = 0; j < 2; j++) {
////                                mmDataInputStream.readFully(intData);
//////                                LogUtils.LOGE("read:", " " + ByteBuffer.wrap(intData)
//////                                        .order(ByteOrder.LITTLE_ENDIAN).getInt());
////                                if (counter == 50)
////                                    LogUtils.LOGE("read: ", " " + (ByteBuffer.wrap(intData)
////                                            .order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xffff));
////                            }
//                            LogUtils.LOGE("counter " + counter, "" + i);
//                            readBuffer[counter] = b;
//                            counter++;
//                        }
////                        } else if (counter == 12) {
////                            handler.post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    heartRateValue.setText(b + "");
////                                }
////                            });
////                        } else if (counter == 50) {
////                            final float distance = (float) b * 6.25f;
////                            LogUtils.LOGE("distance", b + " initial distance:" + initialDistance + " actual distance in cm:" + distance);
////                            handler.post(new Runnable() {
////                                             @Override
////                                             public void run() {
////                                                 if (initialDistance == -1) {
////                                                     distanceValue.setText("0.0");
////                                                 } else if (b > 0) {
////                                                     distanceValue.setText((distance - initialDistance) + "");
////                                                 } else if (b < 0) {
////                                                     distanceValue.setText((-initialDistance + distance) + "");
////                                                 }
////                                                 initialDistance = distance;
////                                             }
////                                         }
////
////                            );
////                        } else if (counter == 52) {
////                            LogUtils.LOGE("speed", b + "");
////                            handler.post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    speedValue.setText(b + "");
////                                }
////                            });
////                        }
////                        counter++;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        );
//
//        workerThread.start();
//    }

//    private void sendBuffer(final byte[] readBuffer, Handler handler) {
//        int heartRate = (int) readBuffer[12];
//        if (heartRate < 0) {
//            heartRate *= -1;
//        }
//        final int finalHeartRate = heartRate;
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
////                LogUtils.LOGE("posting heart rate", readBuffer[11] + "");
//                heartRateValue.setText(finalHeartRate + "");
//
//
//                byte[] arr = {readBuffer[50], readBuffer[51]};
//                ByteBuffer wrapped = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN);
//                short value = wrapped.getShort();
//                if (initialValue == -1) {
//                    initialValue = value;
//                }
////                LogUtils.LOGE("result distance before", value + " - " + initialValue + "(previous)");
//
//                if (value < previousValue) {
//                    previousValue = value;
//                    initialValue = 0;
//                    valueTravelledBefore += intermediateValue;
////                    LogUtils.LOGE("distance", "value before:" + valueTravelledBefore);
//                } else {
//                    previousValue = value;
//                    intermediateValue = valueTravelledBefore + (((value - initialValue) * 6.25f) / 100.0f);
////                    distanceValue.setText(intermediateValue + "");
//                    distanceValue.setText(new DecimalFormat("##.##").format(intermediateValue));
////                    LogUtils.LOGE("result for distance", intermediateValue + "");
//                }
//
//
//                short speedValueShort;
//                byte[] speedArr = {readBuffer[52], readBuffer[53]};
//                speedValueShort = ByteBuffer.wrap(speedArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
//                LogUtils.LOGE("speed in steps", "" + (speedValueShort / 256));
//
//                float speedValueFloat = speedValueShort / 256.0f;
//
//                if (speedValueFloat > maxSpeed)
//                    maxSpeed = speedValueFloat;
////                status.setText("maximum speed:" + maxSpeed);
//                status.setText("maximumu speed " + new DecimalFormat("##.##").format(maxSpeed));
//
////                speedValue.setText(speedValueFloat + "");
//                speedValue.setText(new DecimalFormat("##.##").format(speedValueFloat));
//
////                float distance = (float) readBuffer[50] * 6.25f;
//                if (previousDistance == -1) {
//                    totalDistance = 0.0f;
//                } else if (distance > 0) {
//                    totalDistance += (distance - previousDistance);
//                } else if (distance < 0) {
//                    totalDistance += (-previousDistance + distance);
//                }
//                if (totalDistance < 0) {
//                    LogUtils.LOGE("total distance is minus", "previousDistance:" + previousDistance + " new distance:" + distance);
//                }
//                previousDistance = distance;
//
//                distanceValue.setText(totalDistance + "");

//                speedValue.setText(readBuffer[52] + "");
//
//                LogUtils.LOGE("posting distance", readBuffer[50] + "");
////                LogUtils.LOGE("posting speed", readBuffer[52] + "");
//            }
//        });
//    }

//    void closeBT() throws IOException {
//        stopWorker = true;
//        mmOutputStream.close();
//        mmInputStream.close();
//        mmSocket.close();
//        status.setText("Bluetooth Closed");
//        heartRateValue.setText("?");
//        distanceValue.setText("?");
//        speedValue.setText("?");
//    }

}
