package com.example.neo_cocoa;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class AppInformation {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
//    private enum pref_keys{
//    }

    public AppInformation(Context context) {
        this.sharedPref = context.getSharedPreferences("information", MODE_PRIVATE);
        editor = sharedPref.edit();
        this.refreshData();
    }

    public void refreshData() {

    }
}