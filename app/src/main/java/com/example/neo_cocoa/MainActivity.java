package com.example.neo_cocoa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.neo_cocoa.databinding.ActivityMainBinding;
import com.example.neo_cocoa.hazard_models.mock_ENS;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private AppSettings appSettings;
    private HazardData hazardData;
    private FusedLocationProviderClient fusedLocationClient;
    private AppLocationProvider appLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GlobalFieldへの書き込み
        appSettings = new AppSettings(this);
        GlobalField.appSettings = appSettings;
        hazardData = new HazardData(this);
        GlobalField.hazardData = hazardData;

        // 位置情報関係初期化
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        appLocationProvider = new AppLocationProvider(this, fusedLocationClient);

        // Exposure Notification Service API (mock) 起動
        mock_ENS ens = mock_ENS.create();
        GlobalField.mock_ens = ens;

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


        // 緯度経度をtoast通知する(サンプル)
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token1 = cts.getToken().onCanceledRequested(new OnTokenCanceledListener() {
            @Override
            public void onCanceled() {
                Log.i(TAG, "Canceled.");
            }
        });
        OnCompleteListener<Location> listener = new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
            }
        };
        AppLocationProvider.getCurrentLocation(this, token1, listener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("debug", "MainActivity.onRequestPermissionsResult was called.");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        appLocationProvider.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}