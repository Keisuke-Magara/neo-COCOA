package com.example.neo_cocoa;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class appSettings {
    private boolean backgroundHazardNotification;
    private int getTime;
    private SharedPreferences sharedPref;
    private enum pref_keys{
        backgroundHazardNotification
    };

    public appSettings(Context context) {
        sharedPref = this.context.getActivity().getSharedPreference("settings", MODE_PRIVATE);
    }

    public void refleshData() {
    }
}
