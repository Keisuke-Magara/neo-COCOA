package com.example.neo_cocoa;

import android.location.Location;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class MyLocation extends LocationCallback {
    private MyLocation() {
        System.out.println("entered MyLocationCallback");
    }
    @Override
    public void onLocationResult(LocationResult locationResult) {
        System.out.println("function onLocationResult called");
        if (locationResult == null) {
            System.out.println("locationResult == null.");
            return;
        }
        // 現在地取得
        Location location = locationResult.getLastLocation();
        // TODO: ここに取得後の処理を書く
        // Logcatに表示
        System.out.println("====================================================================");
        System.out.println("緯度:"+location.getLatitude() + "\n経度:"+location.getLongitude());
    }
}
/***
 * TODO:
 * onLocationResultでprivate変数に代入させる
 * getメソッドでその値をreturnする
 *
 */