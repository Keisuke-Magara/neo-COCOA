package com.example.neo_cocoa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;


import java.util.List;
import java.util.Map;


public class AppLocationProvider {
    private static MainActivity mainActivity;
    public static FusedLocationProviderClient fusedLocationClient;
    private LocationCallback myLocationCallback;
    private static LocationResult locationResult=null;
    private static boolean ready = false;
    private static double latitude;
    private static double longitude;
    private static final int gpsInterval = 5*1000; // ms
    //private static final int gpsFastestInterval = 5*1000; // ms
    private static final int gpsPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final int REQUEST_ONESHOT = 1;
    private static final int REQUEST_UPDATE = 2;
    private static final String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final String TAG = "AppLocationProvider";


    /**
     * 最新の位置情報をFLCが取得し、取得が終わったらコールバックメソッドが実行される。
     * @param activity : 呼び出し元のactivityを指定
     * @param cancellationToken : インスタンスを指定
     * @param callback : 匿名関数or処理を描いたインスタンスを指定
     */
    public static void getCurrentLocation(Activity activity, CancellationToken cancellationToken,
                                          OnCompleteListener<Location> callback) {
        // 権限確認
        if (checkPermission()) {
            Log.d(TAG, "AppLocationProvider.getCurrentLocation() was called.");
            fusedLocationClient.getCurrentLocation(gpsPriority, cancellationToken)
                    .addOnCompleteListener(activity, callback);
        }
    }

    // TODO: コールバックメソッドの実装
    private static void getLastLocation() {
        // 現在地取得
        if (locationResult != null) {
            Location location = locationResult.getLastLocation();
            // ローカル変数に格納
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }


    /** アプリ起動時にMainActivityで1度だけ実行される */
    public AppLocationProvider (MainActivity MainActivityPointer, FusedLocationProviderClient FLPCPointer) {
        mainActivity = MainActivityPointer;
        fusedLocationClient = FLPCPointer;
    }

    /**
     * 位置情報のアップデートを購読する。
     * @param lc: 位置情報が更新されたときに行いたい処理を書いたクラスのインスタンス
     * @return 購読開始: true, 権限不足: false
     */
    public static boolean startUpdateLocation(LocationCallback lc) {
        Log.d(TAG, "function startUpdateLocation called");
        // 権限確認
        if (checkPermission()) {
            setUpdateConfigThenStart(lc);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 位置情報のアップデートを停止する。
     */
    public static void stopUpdateLocation() {
        fusedLocationClient.removeLocationUpdates(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.d(TAG, "removed Location Updates.\nLocation is not available.");
            }
        });
    }

    /**
     * 位置情報許可の確認
     * @return Granted: true, Dined: false
     */
    private static boolean checkPermission() {
        Log.d(TAG, "function checkPermission called");
        // 既に許可している
        if (ContextCompat.checkSelfPermission(mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        // 拒否していた場合
        else{
            return requestLocationPermission();
        }
    }

    /**
     * 許可を求めるダイアログ表示
     * @return Success: true, Dined: false
     */
    private static boolean requestLocationPermission() {
        Log.d(TAG, "function requestLocationPermission called");
        if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,
                permissions[0]) || GlobalField.appSettings.isFirstLaunch()) {
            ActivityCompat.requestPermissions(mainActivity, permissions, REQUEST_ONESHOT);
            return true;
        } else {
            Log.e(TAG, "Cannot run Hazard without permission");
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    private static void setUpdateConfigThenStart(LocationCallback lc) {
        // 位置情報の取得方法を設定
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(gpsInterval);
        //locationRequest.setFastestInterval(gpsFastestInterval);
        locationRequest.setPriority(gpsPriority);
        fusedLocationClient.requestLocationUpdates(locationRequest, lc, Looper.getMainLooper());
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "function onRequestPermissionsResult called");
        Log.d(TAG, "entered onRequestPermissionsResult()");
        if (requestCode == REQUEST_ONESHOT && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 位置情報取得開始
            //getCurrentLocation();
        }
    }

    public static void goToSettings() {
        Toast.makeText(mainActivity, "位置情報が許可されていません", Toast.LENGTH_LONG).show();
    }

    public static float getDistance(double startX, double startY, double endX, double endY) {
        // 結果を格納するための配列を生成
        float[] results = new float[3];
        // 距離計算
        Location.distanceBetween(startX, startY, endX, endY, results);
        return results[0];
    }
}
