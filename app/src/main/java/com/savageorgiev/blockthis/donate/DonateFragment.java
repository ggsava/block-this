package com.savageorgiev.blockthis.donate;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.savageorgiev.blockthis.R;
import com.savageorgiev.blockthis.games.GameFragment;
public class DonateFragment extends Fragment {

    private View patronBtn;
    private View paypalBtn;
    private View gamesBtn;
    private View v;

    public String TAG = "donateFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_donate, container, false);

        patronBtn = (View) v.findViewById(R.id.support_patreon_btn);
        paypalBtn = (View) v.findViewById(R.id.support_paypal_btn);
        gamesBtn = (View) v.findViewById(R.id.download_games_btn);

        patronBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                donatePatron(v);
            }
        });
        paypalBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                donatePayPal(v);
            }
        });

        gamesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getGames(v);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Support Page");
    }

    private boolean MyStartActivity(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public void donatePatron(View v){
        Answers.getInstance().logCustom(new CustomEvent("3.0 Donate").putCustomAttribute("Type", "Patreon"));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.patreon.com/blockthis"));
        MyStartActivity(intent);
    }

    public void donatePayPal(View v){
        Answers.getInstance().logCustom(new CustomEvent("3.0 Donate").putCustomAttribute("Type", "PayPal"));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.paypal.me/SavaG/5"));
        MyStartActivity(intent);
    }

    public void getGames(View v){
        Answers.getInstance().logCustom(new CustomEvent("3.0 Donate").putCustomAttribute("Type", "Game"));
        Fragment fragment = new GameFragment();
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    }

}