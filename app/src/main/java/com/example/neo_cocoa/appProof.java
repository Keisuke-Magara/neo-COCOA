package com.example.neo_cocoa;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class appProof {
    private boolean backgroundHazardNotification;
    private int getTime;
    private SharedPreferences sharedPref;
    private enum pref_keys{
        backgroundHazardNotification
    };

    public appProof(Context context) {
        this.sharedPref = context.getSharedPreferences("proof", MODE_PRIVATE);
    }


    public void refleshData() {
    }
}