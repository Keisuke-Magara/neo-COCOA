package com.example.neo_cocoa.ui.information;

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
import com.example.neo_cocoa.databinding.FragmentInformationBinding;

public class InformationFragment extends Fragment {

    private FragmentInformationBinding binding;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInformationBinding.inflate(inflater, container, false);

        view = inflater.inflate(R.layout.fragment_information, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}