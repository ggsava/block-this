package com.savageorgiev.blockthis.whitelist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.savageorgiev.blockthis.R;
import com.savageorgiev.blockthis.ads.App;
import com.savageorgiev.blockthis.vpn.VpnFragment;

import java.util.ArrayList;
import java.util.List;


public class WhitelistAppsFragment extends ListFragment {

    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist=null;
    private List<ApplicationInfo> whitelistedAppList = null;
    private AppAdapter listAdapter=null;
    private int pos;
    private TextView no_whitelist;
    private boolean textVisible = false;

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.whitelist, container, false);
        FloatingActionButton b1 = (FloatingActionButton) v.findViewById(R.id.button_add);
        no_whitelist = (TextView) v.findViewById(R.id.no_whitelist);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new WhitelistAddFragment();
                //replacing the fragment
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });
        new LoadApplications().execute();
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences("com.savageorgiev.blockthis", Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();

        packageManager=getActivity().getPackageManager();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Whitelisted Apps");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        pos = position;
        deleteAppFromWhiteListDialog();
    }

    public void deleteAppFromWhitelist(int pos){
        ApplicationInfo app = whitelistedAppList.get(pos);

        String whitelisted = prefs.getString("whitelisted_apps", null);
        ArrayList<String> packages = new ArrayList<String>();

        if (whitelisted != null){
            packages = gson.fromJson(whitelisted, new TypeToken<ArrayList<String>>(){}.getType());
        }

        if (packages.contains(app.packageName)){
            packages.remove(packages.indexOf(app.packageName));

            whitelisted = gson.toJson(packages);
            editor.putString("whitelisted_apps", whitelisted).apply();

            Toast.makeText(getActivity(), app.loadLabel(packageManager) + " removed from white list. Please restart Block-This VPN.", Toast.LENGTH_LONG).show();
            new LoadApplications().execute();
        }
    }

    public void deleteAppFromWhiteListDialog(){

        ApplicationInfo app = whitelistedAppList.get(pos);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Are you sure?");
        alertDialog.setMessage("Are you sure you want to remove " + (String) app.loadLabel(packageManager) + "  from your whitelist?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAppFromWhitelist(pos);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void updateTextView(){
        if (textVisible) {
            no_whitelist.setVisibility(View.VISIBLE);
        } else {
            no_whitelist.setVisibility(View.GONE);
        }
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list){

        ArrayList<ApplicationInfo> applist=new ArrayList<ApplicationInfo>();

        for(ApplicationInfo info : list){
            try{
                if(packageManager.getLaunchIntentForPackage(info.packageName) != null){
                    applist.add(info);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return  applist;
    }


    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress=null;

        @Override
        protected Void doInBackground(Void... params) {

            String whitelisted = prefs.getString("whitelisted_apps", null);
            ArrayList<String> packages = new ArrayList<String>();

            whitelistedAppList = new ArrayList<ApplicationInfo>();

            if (whitelisted != null){
                packages = gson.fromJson(whitelisted, new TypeToken<ArrayList<String>>(){}.getType());
            }

            applist = checkForLaunchIntent(packageManager.getInstalledApplications(packageManager.GET_META_DATA));

            for(ApplicationInfo info : applist){
                if (packages.contains(info.packageName)){
                    whitelistedAppList.add(info);
;                }
            }

            if (whitelistedAppList.size() == 0){
                textVisible = true;
            } else {
                textVisible = false;
            }

            listAdapter=new AppAdapter(getActivity(),R.layout.listview_whitelist, whitelistedAppList);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }

            setListAdapter(listAdapter);
            updateTextView();
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress=ProgressDialog.show(getActivity(), null, "Loading apps ...");
            super.onPreExecute();
        }
    }
}