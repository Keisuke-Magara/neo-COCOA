package com.example.neo_cocoa.ui.proof;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.neo_cocoa.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareProof {
    private View view;
    private Context context;
    private Activity activity;
    public  ShareProof(View v, Context c, Activity a) {
        view = v;
        context = c;
        activity = a;
    }
    //PDFを生成する
    public void generatePDF() throws FileNotFoundException {
        //PDFファイル生成
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo.Builder builder = new PdfDocument.PageInfo.Builder(595, 842, 0);
        PdfDocument.PageInfo pageInfo = builder.create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint;
        paint = new Paint();
//        canvas.drawText("neoCOCOA",10f,10f, paint);
        CreateBitmap(view);
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dst = new Rect(0, 0, 595, bitmap.getHeight()*595/bitmap.getWidth());
        canvas.drawBitmap(bitmap, src, dst, paint);

        pdfDocument.finishPage(page);
        FileOutputStream out = null;
        out = context.openFileOutput("proof.pdf", context.MODE_PRIVATE);
        try {
            pdfDocument.writeTo(out);
            System.out.println("PDF generate");
            System.out.println(context.getFilesDir().toURI());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("PDF generate failed");
        }
        pdfDocument.close();
    }
    //PDFを共有する
    public void sharePDF() {
        // ストレージの権限の確認
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            // ストレージの権限の許可を求めるダイアログを表示する
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }
        //PDFファイル共有
        final String pathName = context.getFilesDir() +"/proof.pdf";
        System.out.println(pathName);
        Uri uri = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider",
                new File(context.getFilesDir() + "/proof.pdf"));

        //表示
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri,"application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(intent);
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("pdfを開けませんでした");
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_EMAIL, "");
            it.putExtra(Intent.EXTRA_SUBJECT, "");
            it.putExtra(Intent.EXTRA_STREAM, uri);
            it.setType("application/pdf");
            try {
                context.startActivity(Intent.createChooser(it, "選択"));
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
        Button buttonShare = view.findViewById(R.id.proof_button_share);
        buttonShare.setVisibility(View.INVISIBLE);
//        Button buttonBodyTemp = view.findViewById(R.id.proof_button_body_temperature);
//        buttonBodyTemp.setVisibility(View.INVISIBLE);
//        Button buttonVaccine = view.findViewById(R.id.proof_button_vaccine);
//        buttonVaccine.setVisibility(View.INVISIBLE);
        ScrollView scrollView = view.findViewById(R.id.proof_scrollView);
        scrollView.scrollTo(0,0);
        scrollView.setVerticalScrollBarEnabled(false);
        //描画
        v.draw(c);
        //ボタン等の再表示
        buttonShare.setVisibility(View.VISIBLE);
//        buttonBodyTemp.setVisibility(View.VISIBLE);
//        buttonVaccine.setVisibility(View.VISIBLE);
        scrollView.setVerticalScrollBarEnabled(true);
    }
}
