package com.example.neo_cocoa;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class appSettings {
    private boolean backgroundHazardNotification;
    private int getTime;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private enum pref_keys{
        backgroundHazardNotification
    }

    public appSettings(Context context) {
        this.sharedPref = context.getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPref.edit();
        this.refleshData();
    }

    public void setBgNotif(boolean state) {
        this.backgroundHazardNotification = state;
        editor.putBoolean(String.valueOf(pref_keys.backgroundHazardNotification), state);
        editor.apply();
    }

    public boolean getBgNotif() {
        this.refleshData();
        return this.backgroundHazardNotification;
    }

    public void refleshData() {
        this.backgroundHazardNotification = this.sharedPref.getBoolean(String.valueOf(pref_keys.backgroundHazardNotification), false);
    }
}
