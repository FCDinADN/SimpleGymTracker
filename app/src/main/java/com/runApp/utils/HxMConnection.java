package com.runApp.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.runApp.models.HxMMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Class used to find the bluetooth connection and pair the device with the HxM sensor.
 * Created by Rares on 18/02/15.
 */
public class HxMConnection {
    private final String TAG = HxMConnection.class.getSimpleName();

    private BluetoothAdapter bluetoothAdapter;
    private Fragment mFragment;
    private HxMListener mHxMListener;
    private ConnectThread mConnectThread;

    public HxMConnection(Fragment fragment) {
        mFragment = fragment;
    }

    public void findBT() {
        BluetoothDevice mmDevice = null;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
//            mHxMListener.setStatusMessage("No bluetooth adapter available");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mFragment.startActivityForResult(enableBluetooth, 0);
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().startsWith("HXM")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        if (mmDevice != null) {
            mConnectThread = new ConnectThread(mmDevice);
            mConnectThread.start();
//            mHxMListener.setStatusMessage("Bluetooth Device Found");
        } else {
            mHxMListener.socketClosed();
        }
//        return true;
    }

    public void closeBT() throws IOException {
        if (mConnectThread != null) {
            mConnectThread.cancel();
        }
//        mHxMListener.resetValues();
    }

    public void setHxMListener(HxMListener hxMListener) {
        this.mHxMListener = hxMListener;
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSockect;
        private final BluetoothDevice mmDevice;
        private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        private ConnectedThread mConnectedThread;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmpSocket = null;
            try {
                tmpSocket = mmDevice.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException ignored) {
            }
            mmSockect = tmpSocket;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSockect.connect();
            } catch (IOException e) {
                try {
                    mmSockect.close();
                } catch (IOException ignored) {
                }
            }
            mConnectedThread = new ConnectedThread(mmSockect);
            mConnectedThread.start();
        }

        public void cancel() {
            try {
                if (mConnectedThread != null) {
                    mConnectedThread.cancel();
                }
                mmSockect.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInputStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpInputStream = null;
            try {
                tmpInputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInputStream = tmpInputStream;
        }

        public void run() {
            byte[] readBuffer = new byte[1024];
            int counter = 0;
            int i = -1;
            mHxMListener.setHandler(mHandler);
            try {
                while (!Thread.currentThread().isInterrupted() && (i = mmInputStream.read()) != -1) {
                    if (counter == 59) {
                        mHxMListener.receiveValues(new HxMMessage(readBuffer));
                        readBuffer = new byte[1024];
                        counter = 0;
                    } else {
                        readBuffer[counter] = (byte) i;
                        counter++;
                    }
                }
            } catch (IOException e) {
                mHxMListener.socketClosed();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private Handler mHandler = new Handler();
}
