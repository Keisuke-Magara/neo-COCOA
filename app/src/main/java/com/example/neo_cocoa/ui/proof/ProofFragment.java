package com.example.neo_cocoa.ui.proof;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import java.io.OutputStream;
import java.util.Calendar;

public class ProofFragment extends Fragment {
    private FragmentProofBinding binding;
    private AppProof appProof;
    private ProofFragment proofFragment;
    private View view;

    private Button buttonBodyTemp;
    private Button buttonVaccine;
    private Button buttonShare;

    private TextView dataUpdateDate;
    private TextView dataContact;
    private TextView dataPositive;
    private TextView dataBodyTemp;
    private TextView dataVaccine;

    private ImageView ivContact;
    private ImageView ivPositivePerson;
    private ImageView ivBodyTemperature;
    private ImageView ivVaccine;

    private Integer NUM_OF_POSITIVE = 0;//陽性者との接触人数

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        proofFragment = this;
        appProof = new AppProof(this.getActivity());

        binding = FragmentProofBinding.inflate(inflater, container, false);

        view = inflater.inflate(R.layout.fragment_proof, container, false);

        //画面の表示データを更新
        refreshProofFragment();
        //体温入力ボタンのリスナを設定
        buttonBodyTemp = (Button)view.findViewById(R.id.proof_button_body_temperature);
        buttonBodyTemp.setOnClickListener(button_bt_listener);
        //ワクチン接種回数入力ボタンのリスナを設定
        buttonVaccine = (Button)view.findViewById(R.id.proof_button_vaccine);
        buttonVaccine.setOnClickListener(button_vac_listener);
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
    //健康記録共有ボタンのリスナ
    View.OnClickListener button_share_listener = new View.OnClickListener() {
        public void onClick(View view) {
            try {
                generatePDF();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            sharePDF();
        }
    };

    //PDFを生成する
    private void generatePDF() throws FileNotFoundException {
        //PDFファイル生成
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo.Builder builder = new PdfDocument.PageInfo.Builder(595, 842, 0);
        PdfDocument.PageInfo pageInfo = builder.create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint;
        paint = new Paint();
        //viewからbitmapを生成してPDFに貼り付け
        CreateBitmap(view);
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dst = new Rect(0, 0, 595, bitmap.getHeight()*595/bitmap.getWidth());
        canvas.drawBitmap(bitmap, src, dst, paint);

        pdfDocument.finishPage(page);
        FileOutputStream out = getContext().openFileOutput("proof.pdf", getContext().MODE_PRIVATE);
        try {
            pdfDocument.writeTo(out);
            System.out.println("PDF generate");
            System.out.println(getContext().getFilesDir().toURI());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("PDF generate failed");
        }
        pdfDocument.close();
    }

    //PDFを共有する
    private void sharePDF() {
        // ストレージの権限の確認
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            // ストレージの権限の許可を求めるダイアログを表示する
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }
        //PDFファイル表示or共有
        Uri uri = FileProvider.getUriForFile(getContext(),
                getContext().getApplicationContext().getPackageName() + ".provider",
                new File(getContext().getFilesDir() + "/proof.pdf"));
        //表示
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri,"application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        }catch (Exception e) {
            //PDFが表示できない場合は共有
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_STREAM, uri);
            it.setType("application/pdf");
            try {
                startActivity(Intent.createChooser(it, "選択"));
            }catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    //viewからbitmapを生成
    private Bitmap bitmap =null;
    private final Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
    public void CreateBitmap(View v) {
        bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), bitmapConfig);
        Canvas c = new Canvas();
        c.setBitmap(bitmap);
        //不要ボタン等の非表示
        Button buttonShare = v.findViewById(R.id.proof_button_share);
        buttonShare.setVisibility(View.INVISIBLE);
        Button buttonBodyTemp = v.findViewById(R.id.proof_button_body_temperature);
        buttonBodyTemp.setVisibility(View.INVISIBLE);
        Button buttonVaccine = v.findViewById(R.id.proof_button_vaccine);
        buttonVaccine.setVisibility(View.INVISIBLE);
        ScrollView scrollView = view.findViewById(R.id.proof_scrollView);
        scrollView.scrollTo(0,0);
        scrollView.setVerticalScrollBarEnabled(false);
        //描画
        v.draw(c);
        //ボタン等の再表示
        buttonShare.setVisibility(View.VISIBLE);
        buttonBodyTemp.setVisibility(View.VISIBLE);
        buttonVaccine.setVisibility(View.VISIBLE);
        scrollView.setVerticalScrollBarEnabled(true);
    }


    public void setBodyTemperature(Float bodyTemperature) {
        appProof.setBodyTemperature(bodyTemperature);
    }
    public void setBodyTemperatureDate(Integer BodyTemperatureDate) {
        appProof.setBodyTemperatureDate(BodyTemperatureDate);
    }
    public Integer getBodyTemperatureDate() {
        return appProof.getBodyTemperatureDate();
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refreshProofFragment() {
        //データの表示
        dataUpdateDate = (TextView) view.findViewById(R.id.proof_data_update_date);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        dataUpdateDate.setText(sdf.format(c.getTime()));

        dataContact = (TextView) view.findViewById(R.id.proof_data_num_of_contacts);
        dataContact.setText(String.valueOf(GlobalField.hazardData.getMaxDangerLevel()));

        dataPositive = (TextView) view.findViewById(R.id.proof_data_num_of_positive_person);
        if(NUM_OF_POSITIVE == 0) {
            dataPositive.setText(R.string.proof_message_no_contact_with_positive);
        }else if(NUM_OF_POSITIVE > 0) {
            dataPositive.setText(NUM_OF_POSITIVE + R.string.proof_message_contact_with_positive);
        }

        dataBodyTemp = (TextView)view.findViewById(R.id.proof_data_body_temperature);
        sdf = new SimpleDateFormat("yyyyMMdd");
        Integer today = Integer.valueOf(sdf.format(c.getTime()));
        Integer bodyTemperatureDate = getBodyTemperatureDate();
        System.out.println(today);
        System.out.println(bodyTemperatureDate);
        System.out.println(appProof.getBodyTemperature());
        if(today - bodyTemperatureDate != 0) {
            setBodyTemperature(-1f);
        }
        if(appProof.getBodyTemperature() != -1) {
            dataBodyTemp.setText(String.valueOf(appProof.getBodyTemperature()) + getString(R.string.proof_unit_temperature));
        }else {
            dataBodyTemp.setText(getString(R.string.proof_data_no_data));
        }

        dataVaccine = (TextView)view.findViewById(R.id.proof_data_num_of_vaccine);
        if(appProof.getNumOfVaccine() > 0) {
            dataVaccine.setText(appProof.getNumOfVaccine() + getString(R.string.proof_unit_num) + "(" +
                    appProof.getVaccineDate()/10000 + "/" +
                    (appProof.getVaccineDate()/100)%100 + "/" +
                    appProof.getVaccineDate()%100 + ")");
        }else if(appProof.getNumOfVaccine() == 0) {
            dataVaccine.setText(getString(R.string.proof_data_un_inoculated));
        }else {
            dataVaccine.setText(getString(R.string.proof_data_no_data));
        }


        //アイコンの表示
        Resources res = getResources();
        Drawable okIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_proof_ok, null);
        Drawable ngIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_proof_ng, null);
        ivContact = (ImageView) view.findViewById(R.id.proof_icon_num_of_contacts);
        if(0 <= GlobalField.hazardData.getMaxDangerLevel() && GlobalField.hazardData.getMaxDangerLevel() < 4) {
            ivContact.setImageDrawable(okIcon);
        }else {
            ivContact.setImageDrawable(ngIcon);
        }

        ivPositivePerson = (ImageView) view.findViewById(R.id.proof_icon_num_of_positive_person);
        if(NUM_OF_POSITIVE == 0) {
            ivPositivePerson.setImageDrawable(okIcon);
        }else {
            ivPositivePerson.setImageDrawable(ngIcon);
        }

        ivBodyTemperature = (ImageView) view.findViewById(R.id.proof_icon_body_temperature);
        if(0 < appProof.getBodyTemperature() && appProof.getBodyTemperature() < 37.0) {
            ivBodyTemperature.setImageDrawable(okIcon);
        }else {
            ivBodyTemperature.setImageDrawable(ngIcon);
        }

        ivVaccine = (ImageView) view.findViewById(R.id.proof_icon_num_of_vaccine);
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