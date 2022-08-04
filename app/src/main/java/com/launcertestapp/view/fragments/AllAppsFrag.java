package com.launcertestapp.view.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.launcertestapp.R;
import com.launcertestapp.view.MainActivity;
import com.launcertestapp.view.pojo.AppData;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Umesh Kumar on 03/08/2022
 */
public class AllAppsFrag extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static ProgressBar progressBar;
    private Unbinder unbinder;
    private static final String TAG = "AllAppsFrag";
    public static AppListAdapter appListAdapter;

    private PackageManager packageManager;

    public AllAppsFrag() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_all_apps, container, false);
        unbinder = ButterKnife.bind(this, view);

        progressBar = view.findViewById(R.id.progressBar);
        appListAdapter = new AppListAdapter(MainActivity.appDataList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(appListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Get a list of all the apps installed
        packageManager = getActivity().getPackageManager();

        fetchAppList();

        return view;
    }

    private void fetchAppList() {
        // clear the list before adding items to avoid duplicate data
        MainActivity.appDataList.clear();

        // Query the package manager for all apps
        List<ResolveInfo> activities = packageManager.queryIntentActivities(
                new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0);

        // Sort the applications by alphabetical order and add them to the list
        Collections.sort(activities, new ResolveInfo.DisplayNameComparator(packageManager));
        for (ResolveInfo resolver : activities) {

            // Exclude the settings app and this launcher from the list of apps shown
            String appName = (String) resolver.activityInfo.loadLabel(packageManager);
            if (appName.equals("Settings") || appName.equals("Minimalist Launcher"))
                continue;

            AppData data = new AppData();
            data.setName(appName);
            data.setActivityName(resolver.activityInfo.name);
            data.setPackageName(resolver.activityInfo.packageName);
            data.setFlags(resolver.activityInfo.flags);
            data.setIcon(resolver.activityInfo.loadIcon(packageManager));
            MainActivity.appDataList.add(data);
        }
        if (appListAdapter != null) appListAdapter.notifyDataSetChanged();
    }

    public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

        private List<AppData> appList;

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView appName, packageName;
            ImageView appIcon;

            ViewHolder(View itemView) {
                super(itemView);
                appName = itemView.findViewById(R.id.itemName);
                packageName = itemView.findViewById(R.id.itemPackageName);
                appIcon = itemView.findViewById(R.id.itemLogo);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                ComponentName name = new ComponentName(appList.get(getAdapterPosition()).getPackageName(),
                        appList.get(getAdapterPosition()).getActivityName());
                Intent i = new Intent(Intent.ACTION_MAIN);

                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                i.setComponent(name);

                startActivity(i);
            }
        }

        AppListAdapter(List<AppData> appDataList) {
            appList = appDataList;
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int integer) {
            AppData app = appList.get(integer);
            String appName = app.getName();
            String appPackageName = app.getPackageName();
            Drawable appIcon = app.getIcon();

            viewHolder.appName.setText(appName);
            viewHolder.packageName.setText(appPackageName);
            viewHolder.appIcon.setImageDrawable(appIcon);
            viewHolder.appIcon.setContentDescription(appName);
        }

        @Override
        public int getItemCount() {
            return appList.size();
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.app_item_view, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // release views
        unbinder.unbind();
    }
}
