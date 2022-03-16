package com.example.neo_cocoa.hazard_models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import com.example.neo_cocoa.GlobalField;
import com.example.neo_cocoa.HazardData;
import com.ibm.icu.text.Transliterator;


public class HazardModel {
    private static final String TAG = "HazardModel";
    private static HazardModel instance = null;
    private static HazardData hazardData;
    private static Context context;
    private static final int t1 =  1; // 人以上でレベル1
    private static final int t2 = 10; // 人以上でレベル2
    private static final int t3 = 20; // 人以上でレベル3
    private static final int t4 = 30; // 人以上でレベル4
    private static final int t5 = 40; // 人以上でレベル5

    private static int last_contact = 0;
    private static long last_key = 0;
    private static int num_of_contact = 0;

    public static void init(Context c) {
        if(instance==null) {
            context = c;
            instance = new HazardModel(true);
        } else {
            Log.w(TAG, "new instance was not created because instance has already existed.");
        }
    }

    private HazardModel (boolean bgState) {
        hazardData = new HazardData(context);
        if (bgState) {
            if (!GlobalField.mock_ens.isAlive()) {
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
                String str = "";
                str = address.getAddressLine(0);
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

    public static int getNumOfContact () {
        int num_at_key = GlobalField.mock_ens.get_num_at_key();
        long key = GlobalField.mock_ens.getKey2Value();
        int num_at_last_key = GlobalField.mock_ens.get_num_at_prev_key();

        Log.d(TAG, "Key: "+String.valueOf(key));
        Log.d(TAG, "Num: "+String.valueOf(num_at_key));

        if (key == last_key) {
            num_of_contact += num_at_key - last_contact;
            last_contact = num_at_key;
        } else {
            Log.d(TAG, "======= key was changed. ========");
            num_of_contact += num_at_last_key - last_contact;
            num_of_contact += num_at_key;
            last_contact = num_at_key;
            last_key = key;
            //Log.d(TAG, "num_of_contact = "+ num_at_last_key+" - "+last_contact+" + "+num_at_key+" = "+num_of_contact);
        }
        return num_of_contact;
    }

    public static void reset() {
        num_of_contact = 0;
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
