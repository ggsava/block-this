package com.savageorgiev.blockthis.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.savageorgiev.blockthis.R;

public class SettingsFragment extends Fragment {

        private Switch switchAuto;
        private View v;

        SharedPreferences prefs;

        public String TAG = "donateFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            prefs = getActivity().getSharedPreferences(
                    "com.savageorgiev.blockthis", Context.MODE_PRIVATE);

            v = inflater.inflate(R.layout.fragment_settings, container, false);
            switchAuto = (Switch) v.findViewById(R.id.switch_auto_start);

            int autoload = prefs.getInt("autoload", 0);
            if (autoload == 1) {
                switchAuto.setChecked(true);
            }

            switchAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        prefs.edit().putInt("autoload", 1).apply();
                    } else {
                        prefs.edit().putInt("autoload", 0).apply();
                    }
                }
            });

            return v;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getActivity().setTitle("Settings");
        }
}

