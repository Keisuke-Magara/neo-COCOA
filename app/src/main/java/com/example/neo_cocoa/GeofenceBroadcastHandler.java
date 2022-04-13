package com.example.neo_cocoa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.LinkedList;
import java.util.List;

public class GeofenceBroadcastHandler extends BroadcastReceiver  implements NotificationConfig{
    private static final String TAG = "GeofenceBroadcastHandler";
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            Location triggeringLocation = geofencingEvent.getTriggeringLocation();
            System.out.println(triggeringLocation);
        } else {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                List<Geofence> geofenceList = new LinkedList<>();
                // remove current geofence
                // todo: implements method.
                // add new geofence list.
                /*geofenceList.add(new Geofence.Builder()
                        .setRequestId(REQUEST_ID)
                        .setCircularRegion(task.getResult().getLatitude(),
                                task.getResult().getLongitude(),
                                ACTIVE_RADIUS)
                        .setExpirationDuration(EXPIRATION_DURATION)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());*/
            } else {
                // Log the error.
                Log.e(TAG, "geofenceTransition() returned abnormal value.");
            }
        }
    }
}