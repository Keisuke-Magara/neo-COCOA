package com.example.neo_cocoa;

public interface NotificationConfig {
    public static final String REQUEST_ID = "BGHazardNotification";
    public static final float ACTIVE_RADIUS = 100; // [m]
    public static final long EXPIRATION_DURATION = 6 * 60 * 60 * 1000; // [ms]
    public static final String CHANNEL_ID = "neoCOCOA.BackgroundHazardNotification";
}
