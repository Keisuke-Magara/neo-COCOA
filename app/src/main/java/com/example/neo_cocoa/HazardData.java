package com.example.neo_cocoa;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class HazardData {
    private final SharedPreferences sharedPref;
    private final SharedPreferences.Editor editor;
    private boolean demoModeEnable = false;
    private int[] numOfContactHistory = {0, 0, 0, 0, 0, 0, 0, 0};
    private long lastAdd_nOCH;
    private int[] dangerLevelHistory = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private long lastAdd_dLH;
    private enum pref_keys {
        demoModeEnable,
        numOfContact,
        lastAdd_nOCH,
        lastAdd_dLH,
        dangerLevelHistory
    }

    public HazardData(@NonNull Context context) {
        this.sharedPref = context.getSharedPreferences("hazard", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        this.fetchData();
        setNumOfContactHistory(new int[]{0, 0, 0, 0, 0, 0, 0, 0});
    }

    public void setDemoModeState (boolean state) {
        this.demoModeEnable = state;
        editor.putBoolean(String.valueOf(pref_keys.demoModeEnable), state);
        editor.apply();
    }

    public boolean getDemoModeState () {
        this.fetchData();
        return this.demoModeEnable;
    }

    public void addNumOfContactHistory(int num_of_contact) {
        fetchData();
        int passiveTime = numOfContactHistory.length;
        if (lastAdd_nOCH !=-1) {
            long currentTime = System.currentTimeMillis();
            double diffTime = (currentTime - lastAdd_nOCH) / 3600000.0; // 何秒前か
            String tmp = String.valueOf(diffTime);
            tmp = tmp.split(".")[0];
            passiveTime = Integer.parseInt(tmp); // 小数点切り捨て
        }
        if (passiveTime == 0) {
            // do nothing.
        } else {
            numOfContactHistory = slideArrayValues(numOfContactHistory, passiveTime, 0);
            numOfContactHistory[0] = num_of_contact;
            lastAdd_nOCH = System.currentTimeMillis();
            setLastAdd_nOCH(this.lastAdd_nOCH);
        }
        setNumOfContactHistory(this.numOfContactHistory);
    }

    private void setNumOfContactHistory(int[] array) {
        editor.putString(String.valueOf(pref_keys.numOfContact), GlobalField.arrayToString(array));
        editor.apply();
    }

    private void setLastAdd_nOCH(long lastAdd_nOCH) {
        editor.putLong(String.valueOf(pref_keys.lastAdd_nOCH), lastAdd_nOCH);
        editor.apply();
    }

    private int[] slideArrayValues (int[] array, int offset, int nullValue) {
        int size = array.length;
        for (int i=size-1; i>=offset; i--) {
            array[i] = array[i-offset];
        }
        for (int i=0; i<offset; i++) {
            if (i>size-1) {
                break;
            }
            array[i] = nullValue;
        }
        return array;
    }

    public int[] getNumOfContactHistory() {
        fetchData();
        System.out.print("numOfContactHistory: { ");
        for (int i=0; i<numOfContactHistory.length; i++) {
            System.out.print(numOfContactHistory[i]);
            System.out.print(", ");
        }
        System.out.println("}");
        return numOfContactHistory;
    }

    public void addDangerLevelHistory(int currentDangerLevel) {
        fetchData();
        int passiveTime = dangerLevelHistory.length;
        if (lastAdd_dLH !=-1) {
            long currentTime = System.currentTimeMillis();
            double diffTime = (currentTime - lastAdd_dLH) / 86400000.0; // 何日前か
            String tmp = String.valueOf(diffTime);
            tmp = tmp.split(".")[0];
            passiveTime = Integer.parseInt(tmp); // 小数点切り捨て
        }
        if (passiveTime == 0) {
            // do nothing.
        } else {
            dangerLevelHistory = slideArrayValues(dangerLevelHistory, passiveTime, -1);
            dangerLevelHistory[0] = currentDangerLevel;
            lastAdd_dLH = System.currentTimeMillis();
            setLastAdd_dLH(this.lastAdd_dLH);
        }
        setDangerLevelHistory(this.dangerLevelHistory);
    }

    public int getMaxDangerLevel() {
        fetchData();
        int max = -1;
        for (int i=0; i<dangerLevelHistory.length; i++) {
            if (dangerLevelHistory[i] > max) {
                max = dangerLevelHistory[i];
            }
        }
        return max;
    }

    private void setDangerLevelHistory(int[] array) {
        editor.putString(String.valueOf(pref_keys.dangerLevelHistory), GlobalField.arrayToString(array));
        editor.apply();
    }

    private void setLastAdd_dLH(long lastAddDLH) {
        editor.putLong(String.valueOf(pref_keys.lastAdd_dLH), lastAddDLH);
        editor.apply();
    }


    public void fetchData() {
        this.demoModeEnable = this.sharedPref.getBoolean(String.valueOf(pref_keys.demoModeEnable), true);
        this.numOfContactHistory = GlobalField.stringToArray(this.sharedPref.getString(String.valueOf(pref_keys.numOfContact), "0 0 0 0 0 0 0 0"), this.numOfContactHistory.length);
        this.lastAdd_nOCH = this.sharedPref.getLong(String.valueOf(pref_keys.lastAdd_nOCH), -1);
        this.dangerLevelHistory = GlobalField.stringToArray(this.sharedPref.getString(String.valueOf(pref_keys.dangerLevelHistory), "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1"), this.dangerLevelHistory.length);
        this.lastAdd_dLH = this.sharedPref.getLong(String.valueOf(pref_keys.lastAdd_dLH), -1);
        setDemoModeState(this.demoModeEnable);
        setNumOfContactHistory(this.numOfContactHistory);
        setLastAdd_nOCH(this.lastAdd_nOCH);
        setDangerLevelHistory(this.dangerLevelHistory);
        setLastAdd_dLH(this.lastAdd_dLH);
        editor.apply();
    }
}
