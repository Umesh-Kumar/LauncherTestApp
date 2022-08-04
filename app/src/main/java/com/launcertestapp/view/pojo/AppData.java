package com.launcertestapp.view.pojo;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * Created by Umesh Kumar on 04/08/2022
 */
public class AppData {
    @androidx.annotation.NonNull
    private java.lang.String packageName;
    private java.lang.String name;
    private android.graphics.drawable.Drawable icon;
    private java.lang.Integer flags;
    private java.lang.String activityName;

    public AppData() {}

    @NonNull
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(@NonNull String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
