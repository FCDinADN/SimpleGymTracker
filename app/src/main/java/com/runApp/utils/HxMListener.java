package com.runApp.utils;

import android.os.Handler;

import com.runApp.models.HxMMessage;

/**
 * Created by Rares on 18/02/15.
 */
public interface HxMListener {

    public void sendMessage(final HxMMessage hxMMessage);

    public void socketClosed();

    public void setHandler(Handler handler);

    public void setStatusMessage(String s);

    public void resetValues();

    public void setProgressVisibility(int visibility);
}
