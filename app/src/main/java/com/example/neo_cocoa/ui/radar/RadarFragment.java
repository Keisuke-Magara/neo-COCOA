package com.example.neo_cocoa.ui.radar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.neo_cocoa.R;
import com.example.neo_cocoa.databinding.FragmentRadarBinding;

public class RadarFragment extends Fragment {

    private com.example.neo_cocoa.ui.radar.RadarViewModel RadarViewModel;
    private FragmentRadarBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RadarViewModel =
                new ViewModelProvider(this).get(com.example.neo_cocoa.ui.radar.RadarViewModel.class);

        binding = FragmentRadarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textRadar;
        RadarViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}