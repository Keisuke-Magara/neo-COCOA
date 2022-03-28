package com.example.neo_cocoa.hazard_models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import com.example.neo_cocoa.AppLocationProvider;
import com.example.neo_cocoa.GlobalField;
import com.example.neo_cocoa.HazardData;
import com.ibm.icu.text.Transliterator;


public class HazardModel {
    private static final String TAG = "HazardModel";
    private static HazardModel instance = null;
    private static HazardData hazardData;
    private static final int radius = 15; // [m] 接触人数がリセットされる半径
    private static final int t1 =  10; // 人以上でレベル1 (家の中想定)
    private static final int t2 =  75; // 人以上でレベル2 (4m間隔で人がいる場合)
    private static final int t3 = 150; // 人以上でレベル3 (2m間隔で人がいる場合)
    private static final int t4 = 250; // 人以上でレベル4 (大学の学食想定)
    private static final int t5 = 350; // 人以上でレベル5 (イベント会場想定)

    private int last_contact = 0;
    private long last_key = 0;
    private int num_of_contact = 0;
    private double lastLatitude = 0;
    private double lastLongitude = 0;
    private long startTimeOfStay = 0;

    public static void init(Context context) {
        if(instance==null) {
            instance = new HazardModel(true, context);
            reset();
        } else {
            Log.w(TAG, "new instance was not created because instance has already existed.");
        }
    }

    private HazardModel (boolean bgState, Context context) {
        hazardData = GlobalField.hazardData;
        if (bgState) {
            if (!GlobalField.mock_ens.isAlive() && hazardData.getDemoModeState()) {
                GlobalField.mock_ens.start();
            }
        }
    }


    public static String getCurrentAddress(Location location, Geocoder coder, String addressText) {
        try {
            List<Address> addresses = coder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String str = address.getAddressLine(0);
                str = str.split(" ")[1];
                str = str.replace("丁目", "-");
                Transliterator tl = Transliterator.getInstance("Fullwidth-Halfwidth");
                str = tl.transliterate(str);
                addressText = addressText.replace("XXX", str);
                return addressText;
            } else {
                return "ERROR";
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            return "ERROR";
        }
    }

    private static int getNumOfContact () {
        int num_at_key = GlobalField.mock_ens.get_num_at_key();
        long key = GlobalField.mock_ens.getKey2Value();
        int num_at_last_key = GlobalField.mock_ens.get_num_at_prev_key();

        Log.d(TAG, "Key: "+ key);
        Log.d(TAG, "Num: "+ num_at_key);

        if (key == instance.last_key) {
            instance.num_of_contact += num_at_key - instance.last_contact;
            instance.last_contact = num_at_key;
        } else {
            Log.d(TAG, "======= key was changed. ========");
            instance.num_of_contact += num_at_last_key - instance.last_contact;
            instance.num_of_contact += num_at_key;
            instance.last_contact = num_at_key;
            instance.last_key = key;
        }
        return instance.num_of_contact;
    }

    public static int getCurrentContact (double latitude, double longitude) {
        if (AppLocationProvider.getDistance(latitude, longitude, instance.lastLatitude, instance.lastLongitude) >= radius) {
            instance.lastLatitude = latitude;
            instance.lastLongitude = longitude;
            if (!(instance.lastLatitude==0 && instance.lastLongitude==0)) {
                reset();
            }
        }
        return getNumOfContact();
    }

    public static void reset() {
        instance.num_of_contact = 0;
        instance.startTimeOfStay = System.currentTimeMillis();
    }

    public static long getStartTimeOfStay() {
        return instance.startTimeOfStay;
    }

    public static int getDangerLevel (int num_of_contact) {
        if (num_of_contact < t1) {
            return 0;
        } else if (num_of_contact < t2) {
            return 1;
        } else if (num_of_contact < t3) {
            return 2;
        } else if (num_of_contact < t4) {
            return 3;
        } else if (num_of_contact < t5) {
            return 4;
        } else {
            return 5;
        }
    }
}
