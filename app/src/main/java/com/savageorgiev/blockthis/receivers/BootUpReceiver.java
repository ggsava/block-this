package com.savageorgiev.blockthis.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.util.Log;

import com.savageorgiev.blockthis.AutoLaunchActivity;
import com.savageorgiev.blockthis.HomeActivity;
import com.savageorgiev.blockthis.MainVpnService;

/**
 * Created by Y0yOy0 on 7/8/2015.
 */

public class BootUpReceiver extends BroadcastReceiver {

    private SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            prefs = context.getSharedPreferences("com.savageorgiev.blockthis", Context.MODE_PRIVATE);

            int autoload = prefs.getInt("autoload", 0);
            if (autoload == 1){
                //autoload is on - start service
                Intent i = new Intent(context, AutoLaunchActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        }
    }
}