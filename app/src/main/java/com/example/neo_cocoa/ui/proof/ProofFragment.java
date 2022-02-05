package com.example.neo_cocoa.ui.proof;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;


import com.example.neo_cocoa.R;
import com.example.neo_cocoa.AppProof;
import com.example.neo_cocoa.databinding.FragmentProofBinding;

public class ProofFragment extends Fragment {
    private FragmentProofBinding binding;
    private AppProof appProof;
    private ProofFragment proofFragment;
    private Button buttonBt;
    private Button buttonVac;
    private Button buttonShare;

    private TextView btData;
    private TextView vacData;

    private ImageView ivContact;
    private ImageView ivMasculine;
    private ImageView ivBodyTemperature;
    private ImageView ivVaccine;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        proofFragment = this;
        appProof = new AppProof(this.getActivity());

        binding = FragmentProofBinding.inflate(inflater, container, false);

        view = inflater.inflate(R.layout.fragment_proof, container, false);

        //画面の表示データを更新
        refreshProofFragment();
        //体温入力ボタンのリスナを設定
        buttonBt = (Button)view.findViewById(R.id.proof_button_body_temperature);
        buttonBt.setOnClickListener(button_bt_listener);
        //ワクチン接種回数入力ボタンのリスナを設定
        buttonVac = (Button)view.findViewById(R.id.proof_button_vaccine);
        buttonVac.setOnClickListener(button_vac_listener);

        return view;
    }

    //体温入力ボタンのリスナ
    Button.OnClickListener button_bt_listener = new Button.OnClickListener() {
        public void onClick(View view) {
            BodyTemperatureDialogFragment dialogFragment = new BodyTemperatureDialogFragment(proofFragment);
            dialogFragment.show(getActivity().getSupportFragmentManager(), "BodyTemperatureDialogFragment");
        }
    };
    //ワクチン接種回数のリスナ
    Button.OnClickListener button_vac_listener = new Button.OnClickListener() {
        public void onClick(View view) {
            VaccineDialogFragment dialogFragment = new VaccineDialogFragment(proofFragment);
            dialogFragment.show(getActivity().getSupportFragmentManager(), "VaccineDialogFragment");
        }
    };
    public void setBodyTemperature(Float bodyTemperature) {
        appProof.setBodyTemperature(bodyTemperature);
    }
    public void setNumOfVaccine(Integer numOfVaccine) {
        appProof.setNumOfVaccine(numOfVaccine);
    }
    public Integer getNumOfVaccine() {
        return appProof.getNumOfVaccine();
    }
    public void setVaccineDate(Integer vaccineDate) {
        appProof.setVaccineDate(vaccineDate);
    }

    //Fragmentをの情報を更新
    public void refreshProofFragment() {
        //データの表示
        btData = (TextView)view.findViewById(R.id.proof_data_body_temperature);
        btData.setText(String.valueOf(appProof.getBodyTemperature()) + "度");

        vacData = (TextView)view.findViewById(R.id.proof_data_num_of_vaccine);
        vacData.setText(appProof.getNumOfVaccine() + "回(" +
                appProof.getVaccineDate()/10000 + "/" +
                (appProof.getVaccineDate()/100)%100 + "/" +
                appProof.getVaccineDate()%100 + ")");

        //アイコンの表示
        Resources res = getResources();
        Drawable okIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_proof_ok, null);
        Drawable ngIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_proof_ng, null);
        ivContact = (ImageView) view.findViewById(R.id.proof_icon_num_of_contacts);
        ivMasculine = (ImageView) view.findViewById(R.id.proof_icon_num_of_masculine);
        ivBodyTemperature = (ImageView) view.findViewById(R.id.proof_icon_body_temperature);
        ivVaccine = (ImageView) view.findViewById(R.id.proof_icon_num_of_vaccine);

        ivContact.setImageDrawable(ngIcon);
        ivMasculine.setImageDrawable(ngIcon);
        if(appProof.getBodyTemperature() < 37.0F) {
            ivBodyTemperature.setImageDrawable(okIcon);
        }else {
            ivBodyTemperature.setImageDrawable(ngIcon);
        }

        if(appProof.getNumOfVaccine() >= 2) {
            ivVaccine.setImageDrawable(okIcon);
        }else {
            ivVaccine.setImageDrawable(ngIcon);
        }


    }
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}