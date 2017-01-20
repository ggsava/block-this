package com.savageorgiev.blockthis.vpn;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.VpnService;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class MainVpnService extends VpnService {

    private final IBinder mainBinder = new MyLocalBinder();
    private final String TAG = "MainVpnService";
    private Thread vpnThread;
    private PendingIntent pendingIntent;
    private ParcelFileDescriptor vpnInterface;
    Builder builder = new Builder();
    public static boolean isRunning;
    SharedPreferences prefs;
    Gson gson;
    ArrayList<String> packages = new ArrayList<String>();

    Properties config;
    private String dns1 = null;
    private String dns2 = null;

    @Override
    public void onCreate() {

        config = new Properties();

        try {
            config.load(getApplicationContext().getAssets().open("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //replace in config with your own dns servers
        dns1 = config.getProperty("dns1");
        dns2 = config.getProperty("dns2");

        prefs = this.getApplicationContext().getSharedPreferences("com.savageorgiev.blockthis", Context.MODE_PRIVATE);
        gson = new Gson();
        String whitelisted = prefs.getString("whitelisted_apps", null);

        if (whitelisted != null) {
            packages = gson.fromJson(whitelisted, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start a new session by creating a new thread.
        vpnThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //a. Configure the TUN and get the interface.
                    builder.setSession("BlockThisLocalVpn")
                            .setMtu(1500)
                            .addAddress("10.0.2.15", 24)
                            .addAddress("10.0.2.16", 24)
                            .addAddress("10.0.2.17", 24)
                            .addAddress("10.0.2.18", 24)
                            .addDnsServer(dns1)
                            .addDnsServer(dns2);

                    if (Build.VERSION.SDK_INT>=21) {
                        for(String pn : packages){
                            builder.addDisallowedApplication(pn);
                        }
                        //must have those in whitelist
                        if (!packages.contains("com.android.vending")){
                            builder.addDisallowedApplication("com.android.vending");
                        }

                        if (!packages.contains("com.android.vending")) {
                            builder.addDisallowedApplication("com.savageorgiev.blockthis");
                        }
                    }

                    vpnInterface = builder.setConfigureIntent(pendingIntent).establish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, "BlockThisVpnRunnable");

        //start the service in a separate thread
        vpnThread.start();
        isRunning = true;

        Intent in= new Intent();
        in.setAction("vpn.start");
        sendBroadcast(in);
        
        return START_STICKY;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "unbind");
        return super.onUnbind(intent);
    }

    public void kill() {
        try {
            if (vpnInterface != null) {
                vpnInterface.close();
                vpnInterface = null;
            }
            isRunning = false;
        } catch (Exception e) {

        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "destroyed");
        if (vpnThread != null) {
            Log.i(TAG, "interrupted");
            vpnThread.interrupt();
        }
        isRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mainBinder;
    }

    public class MyLocalBinder extends Binder {
        MainVpnService getService(){
            return MainVpnService.this;
        }
    }
}
