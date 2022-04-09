package com.example.neo_cocoa;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundHazardNotificationService extends Service {
    private static final String TAG = "BackgroundHazardNotificationService";
    private static final String REQUEST_ID = "BGHazardNotification";
    private static final float ACTIVE_RADIUS = 100; // [m]
    private static final long EXPIRATION_DURATION = 6 * 60 * 60 * 1000; // [ms]
    private static final String CHANNEL_ID = "neoCOCOA.BackgroundHazardNotification";
    private static int notificationId = 0;
    private mock_ENS mock_ens;
    private GeofencingClient geofencingClient;


    @Override
    public void onCreate() {
        Log.d(TAG, "Service was created.");
        super.onCreate();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "===== Service is runnning...=====");
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentText("TEST")
                        .setContentText("Service was still working...")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .build();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "ハザード通知";
                    String description = "ハザード通知のテストです。";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                    channel.setDescription(description);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                notificationManagerCompat.notify(TAG, notificationId, notification);
            }
        }, 1000, 15000);
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
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
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
        Intent intent = new Intent(getApplication(), BackgroundHazardNotificationService.class);
        startService(intent);
    }
}