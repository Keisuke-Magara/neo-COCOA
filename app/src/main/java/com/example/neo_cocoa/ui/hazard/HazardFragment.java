package com.example.neo_cocoa.ui.hazard;


import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.Objects;

public class HazardFragment extends Fragment {
    private static final String TAG = "HazardFragment";
    private FragmentHazardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HazardModel.init(getContext());
        binding = FragmentHazardBinding.inflate(inflater, container, false);

        View view = inflater.inflate(R.layout.fragment_hazard, container, false);
        TextView locationView = view.findViewById(R.id.hazard_location_address);
        TextView TimeOfStay = view.findViewById(R.id.hazard_time_of_stay);
        TextView numOfContact = view.findViewById(R.id.hazard_num_of_contact);
        TextView dangerLevel = view.findViewById(R.id.hazard_danger_level_body);
        TextView comment = view.findViewById(R.id.hazard_danger_level_comment);
        AppLocationProvider.startUpdateLocation(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                try {
                    Location location = locationResult.getLastLocation();
                    String addressText = getResources().getString(R.string.hazard_location_address);
                    if (Geocoder.isPresent()) {
                        Geocoder coder = new Geocoder(getContext(), Locale.JAPANESE);
                        String address = HazardModel.getCurrentAddress(location, coder, addressText);
                        if (Objects.equals(address, "ERROR")) {
                            locationView.setText(R.string.getting_error);
                        } else {
                            locationView.setText(HazardModel.getCurrentAddress(location, coder, addressText));
                        }
                    } else {
                        addressText = addressText.replace("XXX", locationResult.getLastLocation().getLatitude() + ", " + locationResult.getLastLocation().getLongitude());
                        locationView.setText(addressText);
                    }
                    String num_of_contact_text = getResources().getString(R.string.hazard_num_of_contact);
                    num_of_contact_text = num_of_contact_text.replace("XXX", String.valueOf(HazardModel.getNumOfContact()));
                    numOfContact.setText(num_of_contact_text);
                } catch (IllegalStateException ie) {
                    Log.d(TAG, ie.toString());
                    // do nothing.
                }

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