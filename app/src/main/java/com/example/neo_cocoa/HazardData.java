package com.example.neo_cocoa;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class HazardData {
    private final SharedPreferences sharedPref;
    private final SharedPreferences.Editor editor;
    private long refreshTime;
    private boolean demoModeEnable = false;
    private int[] numOfContact = {0, 0, 0, 0, 0, 0, 0, 0};
    private enum pref_keys {
        demoModeEnable,
        numOfContact
    }

    public HazardData(@NonNull Context context) {
        this.sharedPref = context.getSharedPreferences("hazard", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        this.syncData();
    }

    public void setDemoModeState (boolean state) {
        this.demoModeEnable = state;
        editor.putBoolean(String.valueOf(pref_keys.demoModeEnable), state);
        editor.apply();
    }

    public boolean getDemoModeState () {
        this.syncData();
        return this.demoModeEnable;
    }

    public void addHistory(int num_of_contact) {
        for (int i=numOfContact.length-2; i>=0; i--) {
            numOfContact[i+1] = numOfContact[i];
        }
        numOfContact[0] = num_of_contact;
    }

    public int[] getNumOfContact() {
        return numOfContact;
    }

    /*public static int getMaxDangerLevel() {

    }*/

    public void syncData() {
        this.demoModeEnable = this.sharedPref.getBoolean(String.valueOf(pref_keys.demoModeEnable), true);
        this.numOfContact = GlobalField.stringToArray(this.sharedPref.getString(String.valueOf(pref_keys.numOfContact), "0 0 0 0 0 0 0 0"), this.numOfContact.length);
        editor.putBoolean(String.valueOf(pref_keys.demoModeEnable), this.demoModeEnable);
        editor.putString(String.valueOf(pref_keys.numOfContact), GlobalField.arrayToString(this.numOfContact));
        editor.apply();
    }
}
