package com.example.neo_cocoa.ui.information;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.icu.number.Scale;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neo_cocoa.AppInformation;
import com.example.neo_cocoa.AppProof;
import com.example.neo_cocoa.GlobalField;
import com.example.neo_cocoa.R;
import com.example.neo_cocoa.databinding.FragmentInformationBinding;
import com.example.neo_cocoa.databinding.FragmentProofBinding;
import com.example.neo_cocoa.ui.proof.BodyTemperatureDialogFragment;
import com.example.neo_cocoa.ui.proof.ProofFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class InformationFragment extends Fragment {
    private FragmentInformationBinding binding;
    private AppInformation appInformation;
    private InformationFragment informationFragment;

    private String[] DateList = new String[4];//今日,1日前,2日前,3日前の日付を格納する配列

    private TextView wcData;
    private TextView clData;
    private TextView testLoc;
    private Button buttonTestLoc;
    private String wcUrl;
    private final String API_URL_PREFIX = "opendata.corona.go.jp";
    private String prefectures = "東京都";

    View view;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        informationFragment = this;
        appInformation = new AppInformation(this.getActivity());

        binding = FragmentInformationBinding.inflate(inflater, container, false);

        view = inflater.inflate(R.layout.fragment_information, container, false);

        //画面の表示データを更新
        refreshInformationFragment();

        buttonTestLoc = (Button)view.findViewById(R.id.testLocationButton);
        buttonTestLoc.setOnClickListener(button_TestLoc_listener);
        return view;
    }

    Button.OnClickListener button_TestLoc_listener = new Button.OnClickListener() {
        public void onClick(View view) {
            System.out.println("pressed");
            /*if(GlobalField.mainActivity.myLocationCallback != null) {
                GlobalField.mainActivity.myLocationCallback.printLocation();
            }else{
                GlobalField.mainActivity.startUpdateLocation();
            }*/
        }
    };

    //Fragmentをの情報を更新
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refreshInformationFragment() {
        //データの表示
        wcData = (TextView)view.findViewById(R.id.information_data_whole_country);
        clData = (TextView)view.findViewById(R.id.information_data_current_location);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        for(int i = 0; i<4; i++) {
            DateList[i] = date.format(c.getTime());
            c.add(Calendar.DAY_OF_MONTH, -1);
        }
        wcUrl = "https://opendata.corona.go.jp/api/OccurrenceStatusOverseas?date="+ DateList[0] +"&dataName=%E6%97%A5%E6%9C%AC";
        wcData.setText(DateList[0]);

        MyAsync asynk = new MyAsync(wcData, clData);
        asynk.execute();

        //アイコンの表示
        return;
    }

    class MyAsync extends AsyncTask<String, Void, String> {

        private final WeakReference<TextView> wcDataReference;
        private final WeakReference<TextView> clDataReference;

        public MyAsync(TextView titletView, TextView dateView) {
            wcDataReference = new WeakReference<TextView>(wcData);
            clDataReference = new WeakReference<TextView>(clData);
        }


        @Override
        protected String doInBackground(String... params) {
            final StringBuilder result = new StringBuilder();
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https");
            uriBuilder.authority(API_URL_PREFIX);
            uriBuilder.path("/api/Covid19JapanAll");
            uriBuilder.appendQueryParameter("q", prefectures);
            final String uriStr = uriBuilder.build().toString();

            try {
                URL url = new URL(uriStr);
                HttpURLConnection con = null;
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect(); //HTTP接続

                final InputStream in = con.getInputStream();
                final InputStreamReader inReader = new InputStreamReader(in);
                final BufferedReader bufReader = new BufferedReader(inReader);

                String line = null;
                while((line = bufReader.readLine()) != null) {
                    result.append(line);
                }
                Log.e("but", result.toString());
                bufReader.close();
                inReader.close();
                in.close();
            }

            catch(Exception e) { //エラー
                Log.e("button", e.getMessage());
            }

            return result.toString(); //onPostExecuteへreturn
        }

        @Override
        protected void onPostExecute(String result) { //doInBackgroundが終わると呼び出される
            try {
                JSONObject json = new JSONObject(result);
                String items = json.getString("itemList");
                JSONArray itemsArray = new JSONArray(items);
                JSONObject bookInfo = itemsArray.getJSONObject(0).getJSONObject("");

                String title = bookInfo.getString("npatients");
                String publishedDate = bookInfo.getString("date");

                TextView wcData = wcDataReference.get();
                TextView clData = clDataReference.get();

                wcData.setText(title);
                clData.setText(publishedDate);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}