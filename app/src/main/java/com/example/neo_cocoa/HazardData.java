package com.example.neo_cocoa;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class HazardData {
    private final SharedPreferences sharedPref;
    private final SharedPreferences.Editor editor;
    private long refreshTime;
    private boolean hazardEnable = false;
    private enum pref_keys {
        hazardEnable
    }

    public HazardData(@NonNull Context context) {
        this.sharedPref = context.getSharedPreferences("hazard", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        this.syncData();
    }

    public void setHazardEnable (boolean state) {
        this.hazardEnable = state;
        editor.putBoolean(String.valueOf(pref_keys.hazardEnable), state);
        editor.apply();
    }

    public boolean getHazardEnable () {
        this.syncData();
        //return this.hazardEnable;
        return true;
    }

    public void syncData() {
        this.hazardEnable = this.sharedPref.getBoolean(String.valueOf(pref_keys.hazardEnable), false);
        editor.putBoolean(String.valueOf(pref_keys.hazardEnable), this.hazardEnable);
        editor.apply();
    }
}
