package com.savageorgiev.blockthis.games;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.savageorgiev.blockthis.R;
import com.savageorgiev.blockthis.ads.AppRevenueComparator;
import com.savageorgiev.blockthis.ads.AppNext;
import com.savageorgiev.blockthis.ads.AsyncResponse;

import java.util.ArrayList;
import java.util.Collections;

public class GameFragment extends Fragment implements AsyncResponse {

    private View v;

    public String TAG = "gameFragment";
    private SharedPreferences sharedPreferences;

    //Ads
    AppNext asyncTask;
    ArrayList adsList;

    TextView installGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("com.savageorgiev.blockthis", Context.MODE_PRIVATE);
        v = inflater.inflate(R.layout.fragment_game, container, false);
        installGame = (TextView) v.findViewById(R.id.text_top_game_install);
        getAds();

        return v;
    }

    public void getAds() {
        asyncTask = new AppNext(getActivity());
        asyncTask.delegate = this;
        asyncTask.execute();
    }

    public void processFinish(ArrayList output) {

        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        if (output.size() > 0) {
            Collections.sort(output, new AppRevenueComparator());
            adsList = output;
            ListView mGameList = (ListView) v.findViewById(R.id.games_list_view);
            if (getActivity() != null) {
                mGameList.setAdapter(new GameAdapter(getActivity(), mGameList.getId(), adsList));
            }

            boolean seenGamesDialog = sharedPreferences.getBoolean("seen_games_dialog", false);

            if (!seenGamesDialog){
               displayDialog();
            }
        } else {
            String text = getResources().getString(R.string.no_games_available);
            installGame.setText(text);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Games");
    }

    public void displayDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Thank you!");
        alertDialog.setMessage("By installing any of these games, you support this app's existence and further development." +
                " Without your help, all of this would not be possible. Thank you!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Okay, I understand",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences.edit().putBoolean("seen_games_dialog", true).apply();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
