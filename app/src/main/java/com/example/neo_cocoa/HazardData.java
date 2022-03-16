package com.example.neo_cocoa;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class HazardData {
    private final SharedPreferences sharedPref;
    private final SharedPreferences.Editor editor;
    private long refreshTime;
    private boolean hazardEnable = false;
    private int[] numOfContact = {0, 0, 0, 0, 0, 0, 0, 0};
    private enum pref_keys {
        hazardEnable,
        numOfContact
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

    public void addHistory(int num_of_contact) {
        for (int i=numOfContact.length-2; i>=0; i--) {
            numOfContact[i+1] = numOfContact[i];
        }
        numOfContact[0] = num_of_contact;
    }

    public int[] getNumOfContact() {
        return numOfContact;
    }

    public void syncData() {
        this.hazardEnable = this.sharedPref.getBoolean(String.valueOf(pref_keys.hazardEnable), false);
        this.numOfContact = GlobalField.stringToArray(this.sharedPref.getString(String.valueOf(pref_keys.hazardEnable), "0 0 0 0 0 0 0 0"), this.numOfContact.length);
        editor.putBoolean(String.valueOf(pref_keys.hazardEnable), this.hazardEnable);
        editor.putString(String.valueOf(pref_keys.numOfContact), GlobalField.arrayToString(this.numOfContact));
        editor.apply();
    }
}
