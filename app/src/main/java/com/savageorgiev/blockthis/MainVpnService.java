package com.savageorgiev.blockthis;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.IBinder;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class MainVpnService extends VpnService {

    private final IBinder mainBinder = new MyLocalBinder();
    private final String TAG = "MainVpnService";
    private Thread vpnThread;
    private PendingIntent pendingIntent;
    private ParcelFileDescriptor vpnInterface;
    Builder builder = new Builder();
    public static boolean isRunning;

    public MainVpnService() {

    }

    @Override
    public void onCreate() {
        Log.i(TAG, "created");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "started");

        final String dns1 = "92.222.28.123";
        final String dns2 = "151.80.148.242";


        // Start a new session by creating a new thread.
        vpnThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //a. Configure the TUN and get the interface.
                    vpnInterface = builder.setSession("BlockThisLocalVpn")
                            .setMtu(1500)
                            .addAddress("10.0.2.15", 24)
                            .addAddress("10.0.2.16", 24)
                            .addAddress("10.0.2.17", 24)
                            .addAddress("10.0.2.18", 24)
                            .addDnsServer(dns1)
                            .addDnsServer(dns2)
                            .setConfigureIntent(pendingIntent).establish();

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
        Log.d("starting", "sending broadcast");
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

    public class MyLocalBinder extends Binder{
        MainVpnService getService(){
            return MainVpnService.this;
        }
    }

}
