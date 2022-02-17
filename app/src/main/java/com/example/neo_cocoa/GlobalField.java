package com.example.neo_cocoa;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Use as container for Global values and functions.
 * Do not make instance of this class.
 * Every elements must be static.
 */
public class GlobalField {
    // address directing to instance
    public static AppSettings appSettings;
    public static HazardData hazardData;

    // Global values


    // Global functions
    public static String getNowDate(){
        final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }
}
