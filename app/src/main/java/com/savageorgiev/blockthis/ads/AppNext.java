package com.savageorgiev.blockthis.ads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.savageorgiev.blockthis.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Async task class to get json by making HTTP call
 * */
public class AppNext extends AsyncTask<Void, Void, Void> {

    public AsyncResponse delegate=null;
    private Context mContext;

    private ProgressDialog progress;

    Properties config;

    private static final int cacheTTL = 15*1000*60; //15 minutes

    //json config
    private String url = null;
    private static final String endPoint = "https://admin.appnext.com/offerWallApi.aspx";
    private static final String limit = "25";
    private static final String type = "json";
    private static final String root = "apps";

    //json node names
    private static final String name = "title";
    private static final String description = "desc";
    private static final String imageUrl = "urlImg";
    private static final String imageWideUrl = "urlImgWide";
    private static final String installUrl = "urlApp";
    private static final String androidPackage = "androidPackage";
    private static final String revenueType = "revenueType";
    private static final String revenueAmount = "revenueRate";
    private static final String categories = "categories";
    private static final String videoUrl = "urlVideo";
    private static final String hdVideoUrl = "urlVideoHigh";
    private static final String video30secUrl = "urlVideo30Sec";
    private static final String hdVideo30secUrl = "urlVideo30SecHigh";
    private static final String rating = "storeRating";
    private static final String downloadCount = "storeDownloads";
    private static final String size = "appSize";
    private static final String country = "country";

    // Temporary vars for list of ads
    ArrayList<App> adsList;
    JSONArray ads = null;

    public AppNext(Context context){
        mContext = context;

        config = new Properties();

        try {
            //load a properties file
            config.load(mContext.getAssets().open("config.properties"));
            url = endPoint+"?id="+config.getProperty("appNextApiKey")+"&cnt="+limit+"&type="+type;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        adsList = new ArrayList<App>();
        progress = ProgressDialog.show(mContext, "", "Loading games ..");
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void result) {
        if (progress != null){
            progress.dismiss();
        }

        delegate.processFinish(adsList);
        super.onPostExecute(result);
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        SharedPreferences prefs = mContext.getSharedPreferences(
                "com.savageorgiev.blockthis", Context.MODE_PRIVATE);

        long ads_cache_timestamp = prefs.getLong("ads_cache_timestamp", 0);
        String jsonStr = prefs.getString("ads_string_cached", null);
        //jsonStr = null; //disable cache
        if (ads_cache_timestamp == 0 || ads_cache_timestamp+(cacheTTL) < System.currentTimeMillis() || jsonStr == null){

            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                String responseBody = response.body().string();

                prefs.edit().putString("ads_string_cached", responseBody).apply();
                prefs.edit().putLong("ads_cache_timestamp", System.currentTimeMillis()).apply();
            } catch (IOException e){
                e.printStackTrace();
            }

        }

        jsonStr = prefs.getString("ads_string_cached", null);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                try {
                    ads = jsonObj.getJSONArray(root);
                } catch (JSONException e){
                    return null;
                }
                // looping through All Ads
                for (int i = 0; i < ads.length(); i++) {
                    JSONObject data = ads.getJSONObject(i);

                    App application = new App();

                    application.setAndroidPackage(data.getString(androidPackage));
                    application.setCategories(data.getString(categories).split(","));
                    application.setDescription(data.getString(description));
                    application.setDownloadCount(data.getString(downloadCount));
                    application.setHdVideo30secUrl(data.getString(hdVideo30secUrl));
                    application.setHdVideoUrl(data.getString(hdVideoUrl));
                    application.setImageUrl(data.getString(imageUrl));
                    application.setImageWideUrl(data.getString(imageWideUrl));
                    application.setInstallUrl(data.getString(installUrl));
                    application.setName(data.getString(name));
                    application.setSize(data.getString(size));
                    application.setRating(data.getString(rating));
                    application.setRevenueType(data.getString(revenueType));
                    application.setRevenueAmount(Float.valueOf(data.getString(revenueAmount)));
                    application.setVideo30secUrl(data.getString(video30secUrl));
                    application.setVideoUlr(data.getString(videoUrl));
                    application.setCountry(data.getString(country));

                    adsList.add(application);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

        return null;
    }
}