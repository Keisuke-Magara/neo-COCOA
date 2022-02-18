package com.example.neo_cocoa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
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
    private static final int gpsInterval = 120*1000; // ms
    private static final int gpsFastestInterval = 5*1000; // ms
    private static final int gpsPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private static final int REQUEST_PERMISSION = 2000;
    private static final String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    /**
     * AppLocationProviderのインスタンスは1個 -> create()メソッド?
     * データは全てstaticにする -> 呼び出し時のインスタンス化不要
     * TODO: UpdateLocation のコールバックメソッドを設定できるようにする -> startUpdateLocation()
     * TODO: Update購読を削除する関数を作る
     * TODO: 単発で位置情報を取る関数を作る -> getCurrentLocation()
     */

    /**
     * 最新の位置情報をFLCが取得し、取得が終わったらコールバックメソッドが実行される。
     * @param activity : 呼び出し元のactivityを指定
     * @param cancellationToken : インスタンスを指定
     * @param callback : 匿名関数or処理を描いたインスタンスを指定
     */
    public static void getCurrentLocation(Activity activity, CancellationToken cancellationToken,
                                          OnCompleteListener<Location> callback) {
        // 権限確認
        checkPermission();
        System.out.println("AppLocationProvider.getCurrentLocation() was called.");
        fusedLocationClient.getCurrentLocation(gpsPriority, cancellationToken)
                .addOnCompleteListener(activity, callback);
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

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
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
    public boolean startUpdateLocation(LocationCallback lc) {
        System.out.println("function startUpdateLocation called");
        // 権限確認
        if (checkPermission()) {
            setUpdateConfig(lc);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 位置情報許可の確認
     * @return Granted: true, Dined: false
     */
    private static boolean checkPermission() {
        System.out.println("function checkPermission called");
        // 既に許可している
        if (ContextCompat.checkSelfPermission(mainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION)
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
        System.out.println("function requestLocationPermission called");
        if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) || GlobalField.appSettings.isFirstLaunch()) {
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION);
            return true;
        } else {
            System.out.println("Cannot run the app without permission");
            return false;
        }
    }

    /*private class MyLocationCallback extends LocationCallback {

        @Override
        public void onLocationResult(LocationResult lr) {
            locationResult = lr;
            System.out.println("function onLocationResult called");
            if (locationResult == null) {
                System.out.println("locationResult == null.");
                return;
            }
            ready = true;
            System.out.println("===============================================\n ready.\n=================================================================");
        }
        *//*public void printLocation() {
            Location location = lr.getLastLocation();
            System.out.println("====================================================================");
            longitude
            String msg = "緯度:"+location.getLatitude() + "\n経度:"+location.getLongitude();
            Toast.makeText(mainActivity, msg, Toast.LENGTH_LONG).show();

        }*//*
    }*/

    @SuppressLint("MissingPermission")
    private static void setUpdateConfig(LocationCallback lc) {
        // 位置情報の取得方法を設定
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(gpsInterval);
        locationRequest.setFastestInterval(gpsFastestInterval);
        locationRequest.setPriority(gpsPriority);
        fusedLocationClient.requestLocationUpdates(locationRequest, lc, null);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("function onRequestPermissionsResult called");
        System.out.println("entered onRequestPermissionsResult()");
        if (requestCode == REQUEST_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 位置情報取得開始
            //getCurrentLocation();
        }
    }
}
