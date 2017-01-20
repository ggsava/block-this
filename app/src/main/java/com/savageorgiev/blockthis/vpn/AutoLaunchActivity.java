package com.savageorgiev.blockthis.vpn;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;

import com.savageorgiev.blockthis.R;


public class AutoLaunchActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    private VpnStatusReceiver vpnStatusReceiver;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filter = new IntentFilter("vpn.start");

        vpnStatusReceiver = new VpnStatusReceiver() {
            // this code is call asyncrously from the receiver
            @Override
            public void onVpnStartReceived() {
               closeThis();
            }
        };

        registerReceiver(vpnStatusReceiver, filter);
        setContentView(R.layout.activity_auto_launch);
        connect();
    }

    protected void closeThis(){
        finish();
        onDestroy();

        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(vpnStatusReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(vpnStatusReceiver);
    }

    protected void connect(){
            Intent intent = VpnService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, 0);
            } else {
                onActivityResult(0, RESULT_OK, null);
            }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (result == RESULT_OK) {
            Intent i = new Intent(this, MainVpnService.class);
            startService(i);
        } else {
            super.onActivityResult(request, result, data);
        }
    }
}