package com.savageorgiev.blockthis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class TeaserActivity extends Activity {

    private static final String TAG = "TeaserActivity";
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        prefs = this.getSharedPreferences(
                "com.savageorgiev.blockthis", Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        int reload = getIntent().getIntExtra("reload", 0);

        Log.d(TAG, "a:" + reload);
        if (reload != 1){
            moveForward(0);
        }

        setContentView(R.layout.welcome);
    }

    public void moveForward(int clicked) {

        int seen_teaser = prefs.getInt("seen_teaser", 0);

        if (clicked == 1) {
            prefs.edit().putInt("seen_teaser", 1).apply();
            seen_teaser = 1;
        }

        if (seen_teaser == 1){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int request, int result, Intent data) {
            super.onActivityResult(request, result, data);
    }

    public void onLetMeTryItClicked(View view){
        moveForward(1);
    }

}