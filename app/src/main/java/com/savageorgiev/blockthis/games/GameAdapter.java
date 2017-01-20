package com.savageorgiev.blockthis.games;


import android.content.Context;

import com.savageorgiev.blockthis.ads.App;
import com.savageorgiev.blockthis.ads.AppAdapter;

import java.util.ArrayList;


public class GameAdapter extends AppAdapter {

    public GameAdapter(Context context, int textViewResourceId, ArrayList<App> apps) {
        super(context, textViewResourceId, apps);
    }

}