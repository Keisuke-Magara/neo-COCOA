package com.example.neo_cocoa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
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
    private static LocationResult locationResult=null;
    private static boolean ready = false;
    private static double latitude;
    private static double longitude;
    private static final int gpsInterval = 5*1000; // ms
    private static final int gpsFastestInterval = 5*1000; // ms
    private static final int gpsPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final int REQUEST_ONESHOT = 1;
    private static final int REQUEST_UPDATE = 2;
    private static final String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
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
    public static void stopUpdateLocation(LocationCallback lc) {
        fusedLocationClient.removeLocationUpdates(lc);
    }

    /**
     * フォアグラウンド位置情報アクセス許可の確認
     * @return Granted: true, Dined: false
     */
    public static boolean checkPermission() {
        Log.d(TAG, "function checkPermission called");
        // 既に許可している
        if (ContextCompat.checkSelfPermission(mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        // 拒否していた場合
        else{
            return requestLocationPermission(permissions[0]);
        }
    }

    /**
     * バックグラウンド位置情報アクセス許可の確認
     * @return Granted: true, Dined: false
     */
    public static boolean checkBGPermission() {
        Log.d(TAG, "function checkBGPermission called");
        // 既に許可している
        if (ContextCompat.checkSelfPermission(mainActivity,
                permissions[1])
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        // 拒否していた場合
        else{
            return false;
        }
    }

    public static boolean requestBGPermission() {
        return requestLocationPermission(permissions[1]);
    }

    /**
     * 許可を求めるダイアログ表示
     * @param requestPermission
     * @return Success : true, Dined: false
     */
    private static boolean requestLocationPermission(String requestPermission) {
        Log.d(TAG, "function requestLocationPermission called");
        if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,
                requestPermission) || GlobalField.appSettings.isFirstLaunch()) {
            ActivityCompat.requestPermissions(mainActivity, new String[] {requestPermission}, REQUEST_ONESHOT);
            return true;
        } else {
            Log.e(TAG, requestPermission + " was not granted.");
            goToSettings();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        /*builder.setTitle(R.string.appLocationProvider_goToSettings_dialog_title);
        builder.setMessage(R.string.appLocationProvider_goToSettings_dialog_message);
        builder.setPositiveButton(R.string.appLocationProvider_positiveButton_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {*/
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+mainActivity.getPackageName()));
                mainActivity.startActivity(intent);
            /*}
        });
        builder.show();*/
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
