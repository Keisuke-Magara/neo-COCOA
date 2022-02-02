package com.example.neo_cocoa.ui.proof;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.example.neo_cocoa.R;
import com.example.neo_cocoa.appProof;
import com.example.neo_cocoa.databinding.FragmentProofBinding;

public class ProofFragment extends Fragment {
    private FragmentProofBinding binding;
    private appProof appproof;
    private Button button_bt;
    private Button button_vac;
    private Button button_share;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        appproof = new appProof(this.getActivity());
        binding = FragmentProofBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View view = inflater.inflate(R.layout.fragment_proof, container, false);
        //体温入力ボタンのリスナを設定
        button_bt = (Button)view.findViewById(R.id.proof_button_body_temperature);
        button_bt.setOnClickListener(button_bt_listener);
        //ワクチン接種回数の入力
        button_vac = (Button)view.findViewById(R.id.proof_button_vaccine);
        button_vac.setOnClickListener(button_vac_listener);

        return view;
    }

    //体温入力ボタンのリスナ
    Button.OnClickListener button_bt_listener = new Button.OnClickListener() {
        public void onClick(View view) {
            BodyTemperatureDialogFragment dialogFragment = new BodyTemperatureDialogFragment();
            dialogFragment.show(getActivity().getSupportFragmentManager(), "BodyTemperatureDialogFragment");

        }
    };
    //ワクチン接種回数のリスナ
    Button.OnClickListener button_vac_listener = new Button.OnClickListener() {
        public void onClick(View view) {
            VaccineDialogFragment dialogFragment = new VaccineDialogFragment();
            dialogFragment.show(getActivity().getSupportFragmentManager(), "VaccineDialogFragment");
        }
    };
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}