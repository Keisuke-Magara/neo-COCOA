package com.example.neo_cocoa;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationRequest;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.neo_cocoa.hazard_models.HazardModel;
import com.example.neo_cocoa.hazard_models.mock_ENS;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class BackgroundHazardNotificationService extends Service {
    private static final String TAG = "BackgroundHazardNotificationService";
    private static final String REQUEST_ID = "BGHazardNotification";
    private static final float ACTIVE_RADIUS = 100; // [m]
    private static final long EXPIRATION_DURATION = 6 * 60 * 60 * 1000; // [ms]
    private mock_ENS mock_ens;
    private GeofencingClient geofencingClient;


    @Override
    public void onCreate() {
        Log.d(TAG, "Service was created.");
        super.onCreate();
        geofencingClient = LocationServices.getGeofencingClient(this);
        mock_ens = mock_ENS.create();
        GlobalField.mock_ens = mock_ens;
        HazardModel.init();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service was started.");
        List<Geofence> geofenceList = new LinkedList<>();
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        FusedLocationProviderClient flpc = LocationServices.getFusedLocationProviderClient(this);
        flpc.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        // add geofence at here in 100m.
                        geofenceList.add(new Geofence.Builder()
                                .setRequestId(REQUEST_ID)
                                .setCircularRegion(task.getResult().getLatitude(),
                                        task.getResult().getLongitude(),
                                        ACTIVE_RADIUS)
                                .setExpirationDuration(EXPIRATION_DURATION)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                                .build());
                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service was bound.");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service was destroyed.");
    }
}