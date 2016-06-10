package com.savageorgiev.blockthis;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

public class DonateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
    }

    private boolean MyStartActivity(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public void donate_patron(View v){
        Answers.getInstance().logCustom(new CustomEvent("Donate Clickout").putCustomAttribute("Location", "Patrion"));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.patreon.com/blockthis"));
        MyStartActivity(intent);
    }

    public void donate_paypal(View v){
        Answers.getInstance().logCustom(new CustomEvent("Go to Paypal").putCustomAttribute("Location", "PayPal"));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.paypal.me/SavaG"));
        MyStartActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        return;
    }

}
