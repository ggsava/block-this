package com.savageorgiev.blockthis.whitelist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.savageorgiev.blockthis.R;
import com.savageorgiev.blockthis.vpn.VpnFragment;

import java.util.ArrayList;
import java.util.List;


public class WhitelistAddFragment extends ListFragment {

    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist=null;
    private List<ApplicationInfo> notWhitelistedAppList = null;
    private AppAdapter listAdapter=null;
    private Gson gson;

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.whitelist_add, container, false);
        FloatingActionButton b1 = (FloatingActionButton) v.findViewById(R.id.button_add);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        prefs = getActivity().getSharedPreferences("com.savageorgiev.blockthis", Context.MODE_PRIVATE);
        editor = prefs.edit();
        packageManager=getActivity().getPackageManager();
        new LoadApplications().execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ApplicationInfo app = notWhitelistedAppList.get(position);

        String whitelisted = prefs.getString("whitelisted_apps", null);
        ArrayList<String> packages = new ArrayList<String>();

        if (whitelisted != null){
            packages = gson.fromJson(whitelisted, new TypeToken<ArrayList<String>>(){}.getType());
        }
        packages.add(app.packageName);
        whitelisted = gson.toJson(packages);

        editor.putString("whitelisted_apps", whitelisted).apply();

        Toast.makeText(getActivity(), app.loadLabel(packageManager) + " added to your white list. Please restart Block-This VPN.", Toast.LENGTH_LONG).show();

        Fragment fragment = new WhitelistAppsFragment();
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Add apps to Whitelist");
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

            notWhitelistedAppList = new ArrayList<ApplicationInfo>();

            if (whitelisted != null){
                packages = gson.fromJson(whitelisted, new TypeToken<ArrayList<String>>(){}.getType());
            }

            applist = checkForLaunchIntent(packageManager.getInstalledApplications(packageManager.GET_META_DATA));

            for(ApplicationInfo info : applist){
                if (!packages.contains(info.packageName)){
                    notWhitelistedAppList.add(info);
                }
            }

            listAdapter=new AppAdapter(getActivity(),R.layout.listview_whitelist, notWhitelistedAppList);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }

            setListAdapter(listAdapter);

            if (progress != null){
                progress.dismiss();
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=ProgressDialog.show(getActivity(), null, "Loading apps ...");
        }
    }
}