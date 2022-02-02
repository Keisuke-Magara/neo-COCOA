package com.example.neo_cocoa.ui.proof;

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

import com.example.neo_cocoa.databinding.FragmentProofBinding;

public class ProofFragment extends Fragment {

    private ProofViewModel ProofViewModel;
    private FragmentProofBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProofViewModel =
                new ViewModelProvider(this).get(ProofViewModel.class);

        binding = FragmentProofBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textProof;
        ProofViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
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