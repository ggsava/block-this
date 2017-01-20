package com.savageorgiev.blockthis.whitelist;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.savageorgiev.blockthis.R;

import java.util.List;

public class AppAdapter extends ArrayAdapter<ApplicationInfo> {

    private List<ApplicationInfo> applist = null;
    private Context context;
    private PackageManager packageManager;
    String appNameV;
    LayoutInflater inflater;

    public AppAdapter(Context context, int resource, List<ApplicationInfo> objects) {
        super(context, resource,  objects);

        this.context=context;
        this.applist=objects;
        packageManager=context.getPackageManager();

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ((null != applist) ? applist.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != applist) ? applist.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_whitelist, parent, false);

            // Create a ViewHolder and store references to the two children views
            holder = new ViewHolder();
            holder.iconView = (SmartImageView) convertView.findViewById(R.id.appIcon);
            holder.appName = (TextView) convertView.findViewById(R.id.appName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ApplicationInfo data = applist.get(position);

        if (data != null){
            holder.appName.setText(data.loadLabel(packageManager));
            appNameV = (String) data.loadLabel(packageManager);
            holder.iconView.setImageDrawable(data.loadIcon(packageManager));
        }
        return convertView;
    }

    static class ViewHolder {
        private SmartImageView iconView;
        private TextView appName;
    }
}