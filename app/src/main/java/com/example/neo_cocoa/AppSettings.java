package com.example.neo_cocoa;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;


public class AppSettings {
    private boolean backgroundHazardNotification;
    private String syncTime;
    private final SharedPreferences sharedPref;
    private final SharedPreferences.Editor editor;
    private enum pref_keys{
        backgroundHazardNotification,
        firstLaunch
    }

    public AppSettings(Context context) {
        this.sharedPref = context.getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPref.edit();
        this.syncData();
    }

    public void setBgNotif(boolean state) {
        this.backgroundHazardNotification = state;
        editor.putBoolean(String.valueOf(pref_keys.backgroundHazardNotification), state);
        editor.apply();
    }

    public boolean getBgNotif() {
        this.syncData();
        return this.backgroundHazardNotification;
    }

    public boolean isFirstLaunch() {
        boolean tmp;
        tmp = this.sharedPref.getBoolean(String.valueOf(pref_keys.firstLaunch), true);
        if(tmp) {
            editor.putBoolean(String.valueOf(pref_keys.firstLaunch), false);
        }
        return tmp;
    }

    public void syncData() {
        this.backgroundHazardNotification = this.sharedPref.getBoolean(String.valueOf(pref_keys.backgroundHazardNotification), false);
        this.editor.putBoolean(String.valueOf(pref_keys.backgroundHazardNotification), this.backgroundHazardNotification);
        editor.apply();
        syncTime = GlobalField.getNowDate();
    }

    public String getSyncTime() {
        return syncTime;
    }
}
