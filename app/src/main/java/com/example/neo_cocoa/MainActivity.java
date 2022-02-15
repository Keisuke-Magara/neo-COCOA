package com.example.neo_cocoa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.neo_cocoa.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_PERMISSION = 1000;
    private static String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };
    private Task task;

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
//位置情報の許可
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);

            return;
        }else{

        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        task =fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                        }
                    }
                });
//        System.out.println("---------------------------------------");
//        System.out.println(task.toString());

    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "onRequestPermissionsResult() PERMISSION_GRANTED");

//                this.getLastLocation();

            } else {
                Log.d("debug", "onRequestPermissionsResult() NOT PERMISSION_GRANTED");

                // 拒否された時の対応
                Snackbar sbar = Snackbar.make(findViewById(android.R.id.content),
                        R.string.message2, Snackbar.LENGTH_SHORT);
                sbar.setDuration(10000);
                sbar.getView().setBackgroundColor(Color.rgb(255, 64, 0));

                sbar.setAction(R.string.sbar_button2, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Intent でアプリ権限の設定画面に移行
                        Intent intent = new Intent();
                        intent.setAction(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        // BuildConfigは反映するのに時間がかかってエラーにることもある。待つ
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                sbar.setActionTextColor(Color.YELLOW);
                sbar.show();


            }
        }
    }

//    private void getLastLocation() {
//        fusedLocationClient.getLastLocation()
//                .addOnCompleteListener(
//                        this,
//                        new OnCompleteListener<Location>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Location> task) {
//                                if (task.isSuccessful() && task.getResult() != null) {
//                                    location = task.getResult();
//
//                                    strBuf.append((String.format(Locale.ENGLISH, "%s: %f,  ",
//                                            "緯度", location.getLatitude())));
//                                    strBuf.append((String.format(Locale.ENGLISH, "%s: %f\n",
//                                            "経度", location.getLongitude())));
//                                    textView.setText(strBuf);
//                                } else {
//                                    Log.d("debug","計測不能");
//                                    textView.setText("計測不能");
//                                }
//                            }
//                        });
    }


}