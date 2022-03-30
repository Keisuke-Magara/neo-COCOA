package com.example.neo_cocoa.ui.proof;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;


import com.example.neo_cocoa.GlobalField;
import com.example.neo_cocoa.R;
import com.example.neo_cocoa.AppProof;
import com.example.neo_cocoa.databinding.FragmentProofBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
    private ImageView ivPositivePerson;
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
        //健康記録共有ボタンのリスナを設定
        buttonShare = (Button)view.findViewById(R.id.proof_button_share);
        buttonShare.setOnClickListener(button_share_listener);

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
    //体温記録共有ボタンのリスナ
    View.OnClickListener button_share_listener = new View.OnClickListener() {
        public void onClick(View view) {
            //PDFファイル生成
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo.Builder builder = new PdfDocument.PageInfo.Builder(595, 842, 0);
            PdfDocument.PageInfo pageInfo = builder.create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint;
            paint = new Paint();
            canvas.drawText("neoCOCOA",10f,10f, paint);
            pdfDocument.finishPage(page);
            FileOutputStream out = null;
            try {
                out = getContext().openFileOutput("proof.pdf", getContext().MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                pdfDocument.writeTo(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //PDFファイル共有
            // ストレージの権限の確認
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                // ストレージの権限の許可を求めるダイアログを表示する
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
            }

            Uri uri = FileProvider.getUriForFile(getActivity(),
                    getActivity().getApplicationContext().getPackageName() + ".provider",
                    new File("proof.pdf"));

            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_EMAIL, "");
            it.putExtra(Intent.EXTRA_SUBJECT, "");
            it.putExtra(Intent.EXTRA_STREAM, uri);
            it.setType("application/pdf");

            try {
                startActivity(Intent.createChooser(it, "選択"));
            }catch (Exception e) {
                e.printStackTrace();
            }
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
        ivPositivePerson = (ImageView) view.findViewById(R.id.proof_icon_num_of_positive_person);
        ivBodyTemperature = (ImageView) view.findViewById(R.id.proof_icon_body_temperature);
        ivVaccine = (ImageView) view.findViewById(R.id.proof_icon_num_of_vaccine);

        ivContact.setImageDrawable(ngIcon);
        ivPositivePerson.setImageDrawable(ngIcon);
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