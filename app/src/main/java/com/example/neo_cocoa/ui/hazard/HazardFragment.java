package com.example.neo_cocoa.ui.hazard;


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
import com.example.neo_cocoa.GlobalField;
import com.example.neo_cocoa.R;
import com.example.neo_cocoa.databinding.FragmentHazardBinding;
import com.example.neo_cocoa.hazard_models.HazardModel;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.Locale;
import java.util.Objects;

public class HazardFragment extends Fragment {
    private FragmentHazardBinding binding;
    private HazardModel hazardModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        hazardModel = new HazardModel(true);
        binding = FragmentHazardBinding.inflate(inflater, container, false);

        View view = inflater.inflate(R.layout.fragment_hazard, container, false);
        TextView locationView = view.findViewById(R.id.hazard_location_address);
        AppLocationProvider.startUpdateLocation(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                try {
                    Location location = locationResult.getLastLocation();
                    String addressText = getResources().getString(R.string.hazard_location_address);
                    if (Geocoder.isPresent()) {
                        Geocoder coder = new Geocoder(getContext(), Locale.JAPANESE);
                        String address = hazardModel.getCurrentAddress(location, coder, addressText);
                        if (Objects.equals(address, "ERROR")) {
                            locationView.setText(R.string.getting_error);
                        } else {
                            locationView.setText(addressText);
                        }
                    } else {
                        addressText = addressText.replace("XXX", locationResult.getLastLocation().getLatitude() + ", " + locationResult.getLastLocation().getLongitude());
                        locationView.setText(addressText);
                    }
                } catch (IllegalStateException ie) {
                    // do nothing.
                }
                String
                locationView.setText(hazardModel.get_danger_level());
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        AppLocationProvider.stopUpdateLocation();
    }
}