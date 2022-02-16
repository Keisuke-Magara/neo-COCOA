package com.example.neo_cocoa.ui.hazard;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.neo_cocoa.R;
import com.example.neo_cocoa.databinding.FragmentHazardBinding;

public class HazardFragment extends Fragment {

    private FragmentHazardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHazardBinding.inflate(inflater, container, false);

        View view = inflater.inflate(R.layout.fragment_hazard, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}