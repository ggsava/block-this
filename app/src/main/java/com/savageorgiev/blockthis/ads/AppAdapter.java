package com.savageorgiev.blockthis.ads;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.loopj.android.image.SmartImageView;
import com.savageorgiev.blockthis.R;


import java.util.ArrayList;

public class AppAdapter extends ArrayAdapter<App> {
    private final Context context;
    private final ArrayList<App> appList;
    private final int resourceId;
    LayoutInflater inflater;
    private Dialog dialog;

    public AppAdapter(Context context, int textViewResourceId, ArrayList<App> apps) {
        super(context, textViewResourceId, apps);
        this.context = context;
        this.appList = apps;
        this.resourceId = textViewResourceId;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.game_list_element, parent, false);

            // Create a ViewHolder and store references to the two children views
            holder = new ViewHolder();
            holder.image = (SmartImageView) convertView.findViewById(R.id.appIcon);
            holder.title = (TextView) convertView.findViewById(R.id.appName);

            // The tag can be any Object, this just happens to be the ViewHolder
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog = ProgressDialog.show(context, null, "Loading, please wait ..");
                WebView redirectWebView = (WebView) v.findViewById(R.id.webview);
                WebSettings webSettings = redirectWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                redirectWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Uri uri = Uri.parse(url);
                        if (uri.getScheme().equals("market")){
                            try {
                                Log.d("URLFINAL", url);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                Activity host = (Activity) view.getContext();
                                host.startActivity(intent);
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        };
                        return false;
                    }
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        if (dialog != null){
                            dialog.dismiss();
                        }
                        super.onPageFinished(view, url);
                    }
                });
                Answers.getInstance().logCustom(new CustomEvent("3.0 Game Clicked")
                        .putCustomAttribute("Name", appList.get(position).getName())
                        .putCustomAttribute("Revenue", appList.get(position).getRevenueAmount())
                        .putCustomAttribute("Country", appList.get(position).getCountry())
                );
                redirectWebView.loadUrl(appList.get(position).getInstallUrl());
            }
        });

        holder.image.setImageUrl(appList.get(position).getImageUrl());
        holder.title.setText(appList.get(position).getName());

        return convertView;
    }

    static class ViewHolder {
        private SmartImageView image;
        private TextView title;
    }
}