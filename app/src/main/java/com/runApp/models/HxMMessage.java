package com.runApp.models;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Model class created to represent the packet sent from sensor to device.
 * Created by Rares on 18/02/15.
 */
public class HxMMessage {

    private byte[] buffer;

    public HxMMessage(byte[] buffer) {
        this.buffer = buffer;
    }

    public int getHeartRate() {
        if ((int) buffer[12] < 0) {
            return -(int) buffer[12];
        } else {
            return (int) buffer[12];
        }
    }

    public short getDistance() {
        byte[] arr = {buffer[50], buffer[51]};
        ByteBuffer wrapped = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getShort();
    }

    public float getSpeed() {
        byte[] arr = {buffer[52], buffer[53]};
        ByteBuffer wrapped = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN);
        return wrapped.getShort() / 256.0f;
    }

    public int getBattery() {
        return (int) buffer[11];
    }

}
