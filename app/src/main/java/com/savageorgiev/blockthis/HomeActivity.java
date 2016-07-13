package com.savageorgiev.blockthis;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.VpnService;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.savageorgiev.blockthis.MainVpnService.MyLocalBinder;
import com.savageorgiev.blockthis.receivers.VpnStatusReceiver;
import com.winsontan520.wversionmanager.library.WVersionManager;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    MainVpnService mainService;
    boolean isBound = false;

    private SharedPreferences prefs;

    @BindView(R.id.button3)
    FloatingActionButton connectBtn;
    @BindView(R.id.button4)
    View disconnectBtn;
    @BindView(R.id.textView5)
    TextView textUnderBtn;
    @BindView(R.id.textView6)
    TextView textConnectedBtn;
    @BindView(R.id.switch1)
    Switch mySwitch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Called when the activity is first created.
     */
    private VpnStatusReceiver vpnStatusReceiver;
    private IntentFilter filter;

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            this.unbindService(mainConnection);
            isBound = false;
        }
        unregisterReceiver(vpnStatusReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MainVpnService.isRunning && !isBound) {
            Log.i(TAG, "true");
            this.bindService(new Intent(this, MainVpnService.class), mainConnection, Context.BIND_AUTO_CREATE);
            updateConnectedUI();
        } else {
            updateDisconnectedUI();
        }
        registerReceiver(vpnStatusReceiver, filter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank_activiy2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Log.v(TAG, "old");

        WVersionManager versionManager = new WVersionManager(this);
        versionManager.setVersionContentUrl("https://block-this.com/version.txt");
        versionManager.setUpdateUrl("https://block-this.com/block-this-latest.apk");
        versionManager.setDialogCancelable(false);
        versionManager.setReminderTimer(360);
        versionManager.setIgnoreThisVersionLabel(""); //make button invisible
        versionManager.checkVersion();

        //Initiate shared preferences
        prefs = this.getSharedPreferences("com.savageorgiev.blockthis", Context.MODE_PRIVATE);

        vpnStatusReceiver = new VpnStatusReceiver() {
            // this code is call asyncrously from the receiver
            @Override
            public void onVpnStartReceived() {
                updateConnectedUI();
                Log.d(TAG, "received");
            }
        };

        filter = new IntentFilter("vpn.start");
        this.registerReceiver(vpnStatusReceiver, filter);

        //initialize switch button
        handleSwitch();

        if (MainVpnService.isRunning) {
            Log.i(TAG, "true");
            this.bindService(new Intent(this, MainVpnService.class), mainConnection, Context.BIND_AUTO_CREATE);
            updateConnectedUI();
        } else {
            updateDisconnectedUI();
        }
    }

    public void donate(View v) {
        Answers.getInstance().logCustom(new CustomEvent("Want to help"));

        Intent intent = new Intent(this, DonateActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void updateConnectedUI() {
        connectBtn.setVisibility(View.GONE);
        disconnectBtn.setVisibility(View.VISIBLE);
        textUnderBtn.setVisibility(View.GONE);
        textConnectedBtn.setVisibility(View.VISIBLE);
    }

    public void updateDisconnectedUI() {
        connectBtn.setVisibility(View.VISIBLE);
        disconnectBtn.setVisibility(View.GONE);
        textUnderBtn.setVisibility(View.VISIBLE);
        textConnectedBtn.setVisibility(View.GONE);
    }

    public void handleSwitch() {

        int autoload = prefs.getInt("autoload", 0);
        if (autoload == 1) {
            //set the switch to ON
            mySwitch.setChecked(true);
        }

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    prefs.edit().putInt("autoload", 1).apply();
                } else {
                    prefs.edit().putInt("autoload", 0).apply();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blank_activiy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about: btnHelp();
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void connect(View view) {
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            Log.i(TAG, "not null");
            startActivityForResult(intent, 0);
        } else {
            Log.i(TAG, "NULL");
            onActivityResult(0, RESULT_OK, null);
        }
    }

    public void disconnect(View view) {
        mainService.kill();
        textUnderBtn.setVisibility(View.VISIBLE);
        disconnectBtn.setVisibility(View.GONE);
        connectBtn.setVisibility(View.VISIBLE);
        textConnectedBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (result == RESULT_OK) {
            Intent i = new Intent(this, MainVpnService.class);
            startService(i);
            this.bindService(new Intent(this, MainVpnService.class), mainConnection, Context.BIND_AUTO_CREATE);
        } else {
            super.onActivityResult(request, result, data);
        }
    }


    private ServiceConnection mainConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLocalBinder binder = (MyLocalBinder) service;
            mainService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private boolean MyStartActivity(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    //On click event for opening website
    public void openSite(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://block-this.com"));
        MyStartActivity(intent);
    }


    public void btnHelp() {
        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
        alertDialog.setTitle("Tell us what you think!");
        alertDialog.setMessage("If you are having any issues or want to share an opinion, please email us at blockthisapp@gmail.com.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Help!")
                .putContentType("Dialog")
                .putContentId("HomeActivity"));
    }

    @Override
    public void onDestroy() {
        if (isBound) {
            this.unbindService(mainConnection);
        }
        super.onDestroy();
    }
}
