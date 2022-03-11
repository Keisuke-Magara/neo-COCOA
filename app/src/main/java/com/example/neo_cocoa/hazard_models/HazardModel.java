package com.example.neo_cocoa.hazard_models;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import com.ibm.icu.text.Transliterator;


public class HazardModel {
    private static final String TAG = "HazardModel";
    private static final int graphRange = 6; // 時間前までの接触人数の推移
    private static final int t1 =  1; // 人以上でレベル1
    private static final int t2 = 10; // 人以上でレベル2
    private static final int t3 = 20; // 人以上でレベル3
    private static final int t4 = 30; // 人以上でレベル4
    private static final int t5 = 40; // 人以上でレベル5
    private final mock_ENS ens;
    private int sumOfKey = 0; // 一つのENS Keyで取得された接触者数の累計

    private int last_contact;
    private int danger_level;

    public HazardModel (boolean bgState) {
        ens = new mock_ENS();
        if (bgState) {
            ens.start();
        }
    }

    public int get_danger_level () {
        int num_of_contact = ens.get_num_at_key();
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

    public String getCurrentAddress(Location location, Geocoder coder, String addressText) {
        try {
            System.out.println(location.getLatitude());
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


}
