package com.savageorgiev.blockthis.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Y0yOy0 on 7/8/2015.
 */

public class VpnStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        onVpnStartReceived();
    }

    public void onVpnStartReceived(){

    }
}