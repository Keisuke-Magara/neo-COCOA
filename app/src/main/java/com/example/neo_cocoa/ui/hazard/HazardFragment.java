package com.example.neo_cocoa.ui.hazard;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.neo_cocoa.AppLocationProvider;
import com.example.neo_cocoa.R;
import com.example.neo_cocoa.databinding.FragmentHazardBinding;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HazardFragment extends Fragment {

    private FragmentHazardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHazardBinding.inflate(inflater, container, false);

        View view = inflater.inflate(R.layout.fragment_hazard, container, false);
        TextView locationView = view.findViewById(R.id.hazard_location_address);
        AppLocationProvider.startUpdateLocation(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                Geocoder coder = new Geocoder(getContext(), Locale.JAPANESE);
                try {
                    List<Address> addresses = coder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (addresses!=null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String metro = address.getAdminArea();
                        String gun = address.getSubAdminArea();
                        String city = address.getLocality();
                        String chome = address.getThoroughfare();
                        String str = "";
                        if (metro!=null)
                            str += metro;
                        if (gun!=null)
                            str += gun;
                        if (city!=null)
                            str += city;
                        if (chome!=null)
                            str += chome;
                        String addressText = getResources().getString(R.string.hazard_location_address);
                        addressText = addressText.replace("XXX", str);
                        locationView.setText(addresses.get(0).getAddressLine(0));
                    } else {
                        locationView.setText(R.string.getting_error);
                    }
                } catch (IOException e) {
                    locationView.setText(R.string.getting_error);
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}