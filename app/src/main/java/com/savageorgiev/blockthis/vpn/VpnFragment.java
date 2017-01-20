package com.savageorgiev.blockthis.vpn;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.VpnService;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.savageorgiev.blockthis.R;
import com.savageorgiev.blockthis.donate.DonateFragment;


public class VpnFragment extends Fragment implements View.OnClickListener {

    private VpnStatusReceiver vpnStatusReceiver;
    private IntentFilter filter;
    public MainVpnService mainService;
    boolean isBound = false;


    View connectBtn;

    View disconnectBtn;

    TextView textBtnConnected;

    TextView textBtnDisconnected;

    TextView textDonate;

    @Override
    public void onClick(View v) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vpn, container, false);

        connectBtn = (View) v.findViewById(R.id.button_connect);
        disconnectBtn = (View) v.findViewById(R.id.button_disconnect);
        textBtnConnected = (TextView) v.findViewById(R.id.text_button_connected);
        textBtnDisconnected = (TextView) v.findViewById(R.id.text_button_disconnected);
        textDonate = (TextView) v.findViewById(R.id.text_top_donation);

        SpannableStringBuilder spannableString = new SpannableStringBuilder("If you find this app useful, please");
                spannableString.append("\n");
                spannableString.append("consider supporting it (free).");

        ClickableSpan installSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Answers.getInstance().logCustom(new CustomEvent("3.0 Homepage Support"));

                Fragment fragment = new DonateFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        spannableString.setSpan(installSpan, 45, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textDonate.setText(spannableString);
        textDonate.setMovementMethod(LinkMovementMethod.getInstance());
        textDonate.setHighlightColor(Color.parseColor("#c55b5b"));


        vpnStatusReceiver = new VpnStatusReceiver() {
            @Override
            public void onVpnStartReceived() {
                updateConnectedUI();
            }
        };

        filter = new IntentFilter("vpn.start");
        getActivity().registerReceiver(vpnStatusReceiver, filter);

        if (MainVpnService.isRunning) {
            this.getActivity().bindService(new Intent(this.getActivity(), MainVpnService.class), mainConnection, Context.BIND_AUTO_CREATE);
            updateConnectedUI();
        }

        connectBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    connect(v);
                }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                disconnect(v);
            }
        });

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home - VPN");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void updateConnectedUI() {
        connectBtn.setVisibility(View.GONE);
        disconnectBtn.setVisibility(View.VISIBLE);
        textBtnConnected.setVisibility(View.GONE);
        textBtnDisconnected.setVisibility(View.VISIBLE);
    }

    private void updateDisconnectedUI() {
        connectBtn.setVisibility(View.VISIBLE);
        disconnectBtn.setVisibility(View.GONE);
        textBtnConnected.setVisibility(View.VISIBLE);
        textBtnDisconnected.setVisibility(View.GONE);
    }

    public void connect(View view) {
        startVpn();
    }

    public void startVpn(){
        Intent intent = VpnService.prepare(getActivity());
        if (intent != null) {
            startActivityForResult(intent, 0);
        } else {
            onActivityResult(0, Activity.RESULT_OK, null);
        }
    }

    public void disconnect(View view) {
        if (mainService != null){
            mainService.kill();
        }
        updateDisconnectedUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Intent i = new Intent(this.getActivity(), MainVpnService.class);
            this.getActivity().startService(i);
            this.getActivity().bindService(
                    new Intent(this.getActivity(),
                            MainVpnService.class),
                    mainConnection, Context.BIND_AUTO_CREATE);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private ServiceConnection mainConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainVpnService.MyLocalBinder binder = (MainVpnService.MyLocalBinder) service;
            mainService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (isBound) {
            getActivity().unbindService(mainConnection);
            isBound = false;
        }
        getActivity().unregisterReceiver(vpnStatusReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainVpnService.isRunning && !isBound) {
            getActivity().bindService(new Intent(getActivity(), MainVpnService.class), mainConnection, Context.BIND_AUTO_CREATE);
            updateConnectedUI();
        } else {
            updateDisconnectedUI();
        }
        getActivity().registerReceiver(vpnStatusReceiver, filter);
    }
}