package com.example.neo_cocoa;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class appProof {
    private String bodyTemperature;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private enum pref_keys{
        bodyTemperature;
    }

    public appProof(Context context) {
        this.sharedPref = context.getSharedPreferences("proof", MODE_PRIVATE);
        editor = sharedPref.edit();
        this.refreshData();
    }

    public void setBodyTemperature(String bt) {
        this.bodyTemperature = bt;
        editor.putString(String.valueOf(appProof.pref_keys.bodyTemperature), bt);
        editor.apply();
        this.refreshData();
    }

    public String getBodyTemperature() {
        this.refreshData();
        return this.bodyTemperature;
    }

    public void refreshData() {
        this.bodyTemperature = this.sharedPref.getString(String.valueOf(pref_keys.bodyTemperature), "取得中...");
    }
}