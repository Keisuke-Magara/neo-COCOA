package com.example.neo_cocoa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.widget.Toast;

import com.example.neo_cocoa.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private int gpsInterval = 120*1000; // ms
    private int gpsFastestInterval = 5*1000; // ms
    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_PERMISSION = 2000;
    private static String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_infomation, R.id.navigation_settings, R.id.navigation_hazard, R.id.navigation_proof)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        // 位置情報取得開始
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startUpdateLocation();
    }
    
    private void startUpdateLocation() {
        System.out.println("function startUpdateLocation called");
        // 権限確認
        if (ActivityCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED) {
            // 権限不足
            System.out.println("lack of permission");
            if(Build.VERSION.SDK_INT >= 23){
                checkPermission();
            }
            else{
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
                new MyLocationCallback();
            }
            System.out.println("after dialog");
            return;
        }
        getLocation();
    }

    // 位置情報許可の確認
    public void checkPermission() {
        System.out.println("function checkPermission called");
        // 既に許可している
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
        // 拒否していた場合
        else{
            requestLocationPermission();
        }
    }

    // 許可を求める
    private void requestLocationPermission() {
        System.out.println("function requestLocationPermission called");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION);
            getLocation();
        } else {
            System.out.println("Cannot run the app without permission");
        }
    }

    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
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
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("function onRequestPermissionsResult called");
        System.out.println("entered onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 位置情報取得開始
            startUpdateLocation();
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        // 位置情報の取得方法を設定
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(gpsInterval);
        locationRequest.setFastestInterval(gpsFastestInterval);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        System.out.println("before fusedL");
        fusedLocationClient.requestLocationUpdates(locationRequest, new MyLocationCallback(), null);
    }
}