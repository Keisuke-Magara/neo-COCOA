package com.example.neo_cocoa;

import com.example.neo_cocoa.hazard_models.mock_ENS;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Use as container for Global values and functions.
 * Do not make instance of this class.
 * Every elements must be static.
 */
public final class GlobalField {
    // address directing to instance
    public static AppSettings appSettings;
    public static HazardData hazardData;
    public static mock_ENS mock_ens;

    // Global values


    // Global functions
    public static String getNowDate(){
        final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    public static String arrayToString (int[] a) {
        StringBuilder ret = new StringBuilder();
        for (int i=0; i<a.length; i++) {
            ret.append(a[i]).append(" ");
        }
        return ret.toString();
    }
    public static int[] stringToArray (String str, int arraySize) {
        int[] ret = new int[arraySize];
        String[] strArray = str.split(" ");
        for (int i=0; i<ret.length; i++) {
            ret[i] = Integer.parseInt(strArray[i]);
        }
        return ret;
    }




    private GlobalField() {} // Don't use constructor.
}
