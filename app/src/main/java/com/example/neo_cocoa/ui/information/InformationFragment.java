package com.example.neo_cocoa.ui.information;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.icu.number.Scale;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neo_cocoa.AppInformation;
import com.example.neo_cocoa.AppLocationProvider;
import com.example.neo_cocoa.AppProof;
import com.example.neo_cocoa.GlobalField;
import com.example.neo_cocoa.R;
import com.example.neo_cocoa.databinding.FragmentInformationBinding;
import com.example.neo_cocoa.databinding.FragmentProofBinding;
import com.example.neo_cocoa.ui.proof.BodyTemperatureDialogFragment;
import com.example.neo_cocoa.ui.proof.ProofFragment;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InformationFragment extends Fragment {
    private FragmentInformationBinding binding;
    private AppInformation appInformation;
    private InformationFragment informationFragment;

    private String[] DateList = new String[4];//今日,1日前,2日前,3日前の日付を格納する配列

    private TextView wcData;
    private TextView clData;
    private TextView clTitle;
    private TextView testLoc;
    private ImageView wcImage;
    private ImageView clImage;
    private Button buttonTestLoc;
    private Button buttonShowDetails;
    private String wcUrl;
    private final String API_URL_PREFIX = "opendata.corona.go.jp";
    private String currentLocation = "東京都";

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

        buttonShowDetails = (Button)view.findViewById(R.id.information_button_show_details);
        buttonShowDetails.setOnClickListener(buttonShowDetailsListener);
        buttonTestLoc = (Button)view.findViewById(R.id.testLocationButton);
        buttonTestLoc.setOnClickListener(buttonTestLocListener);
        return view;
    }

    Button.OnClickListener buttonShowDetailsListener = new Button.OnClickListener() {
        public void onClick(View view) {
            System.out.println("ShowDetailsPressed");
            Uri uri = Uri.parse("https://covid19.mhlw.go.jp/?_fsi=Ew5xhpvh");
            Intent i = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(i);
        }
    };

    Button.OnClickListener buttonTestLocListener = new Button.OnClickListener() {
        public void onClick(View view) {
            System.out.println("pressed");
            CancellationTokenSource cts = new CancellationTokenSource();
            CancellationToken token1 = cts.getToken().onCanceledRequested(new OnTokenCanceledListener() {
                @Override
                public void onCanceled() {
                    System.out.println("Canceled.");
                }
            });

            AppLocationProvider.getCurrentLocation(getActivity(), token1, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    try {
                        //getResultがnullのときの例外処理
                        if(task.getResult() != null) {
                            if(Geocoder.isPresent()) {
                                Geocoder coder = new Geocoder(getContext(), Locale.JAPAN);
                                List<Address> addresses= coder.getFromLocation(task.getResult().getLatitude(), task.getResult().getLongitude(), 1);

                                Log.println(Log.ASSERT, "onComplete", String.valueOf(task.isSuccessful()));
                                Toast.makeText(getActivity(), "緯度:" + task.getResult().getLatitude() +
                                                "\n経度:" + task.getResult().getLongitude() +
                                                "\n都道府県:" + addresses.get(0).getAdminArea()
                                        , Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getActivity(), "geocoderが利用できませんでした", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "位置情報が取得できませんでした", Toast.LENGTH_LONG).show();
                        }
                    } catch (RuntimeExecutionException | IOException tasks) {
                        AppLocationProvider.goToSettings();
                        System.out.println("-------------------------"+tasks);
                        tasks.printStackTrace();
                    }
                }
            });
        }
    };

    //Fragmentの情報を更新
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refreshInformationFragment() {
        //データの表示
        wcData = (TextView)view.findViewById(R.id.information_data_whole_country);
        clData = (TextView)view.findViewById(R.id.information_data_current_location);
        wcImage = (ImageView)view.findViewById(R.id.information_image_whole_country);
        clImage = (ImageView)view.findViewById(R.id.information_image_current_location);
        clTitle =(TextView)view.findViewById(R.id.information_title_current_location);
        //現在地情報の更新
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken token1 = cts.getToken().onCanceledRequested(new OnTokenCanceledListener() {
            @Override
            public void onCanceled() {
                System.out.println("Canceled.");
            }
        });

        AppLocationProvider.getCurrentLocation(getActivity(), token1, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                try {
                    //getResultがnullのときの例外処理
                    if(task.getResult() != null) {
                        //現在地の都道府県を取得
                        if(!Geocoder.isPresent()) return;
                        Geocoder coder = new Geocoder(getContext(), Locale.JAPAN);
                        List<Address> addresses= coder.getFromLocation(task.getResult().getLatitude(), task.getResult().getLongitude(), 1);
                        currentLocation = addresses.get(0).getAdminArea();
                        clTitle.setText(currentLocation);
                    } else {
                        Toast.makeText(getActivity(), "位置情報が取得できませんでした", Toast.LENGTH_LONG).show();
                        System.out.println("-----------------else");
                    }
                } catch (RuntimeExecutionException | IOException tasks) {
                    AppLocationProvider.goToSettings();
                    System.out.println("-------------" + tasks);
                }
                //現在地の新規陽性者数を取得・表示
                ClAsync clAsync = new ClAsync();
                clAsync.execute();

            }
        });

        WcAsync wcAsync = new WcAsync();
//        ClAsync clAsync = new ClAsync();
        wcAsync.execute();
//        clAsync.execute();

        //アイコンの表示
        return;
    }

    //全国の新規陽性者数を取得するクラス
    class WcAsync extends AsyncTask<String, Void, String> {

        //apiからjsonを取得
        @Override
        protected String doInBackground(String... params) {
            final StringBuilder result = new StringBuilder();
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https");
            uriBuilder.authority(API_URL_PREFIX);
            uriBuilder.path("/api/OccurrenceStatusOverseas");
            uriBuilder.appendQueryParameter("dataName", "日本");
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

        //jsonから必要なデータを取得・表示
        @Override
        protected void onPostExecute(String result) { //doInBackgroundが終わると呼び出される
            try {
                //JSONファイルから全国の累計感染者数を取得
                JSONObject json = new JSONObject(result);
                String itemList = json.getString("itemList");
                JSONArray itemsArray = new JSONArray(itemList);
                //indexを増やすと過去のデータを表示可能
                String wc1 = itemsArray.getJSONObject(0).getString("infectedNum");
                String wc2 = itemsArray.getJSONObject(1).getString("infectedNum");
                String wc3 = itemsArray.getJSONObject(2).getString("infectedNum");
                //取得したデータは3文字置きに","が入っているので削除
                wc1 = wc1.replace(",", "");
                wc2 = wc2.replace(",", "");
                wc3 = wc3.replace(",", "");
                //最新の全国の感染者数の増加数と前日比を計算
                int wc = Integer.parseInt(wc1) - Integer.parseInt(wc2);
                int wcDiff = wc - Integer.parseInt(wc2) + Integer.parseInt(wc3);

                //ImageViewとTextViewを更新
                Resources res = getResources();
                Drawable upIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_information_up, null);
                Drawable downIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_information_down, null);
                String wcText;
                if(wcDiff >= 0) {
                    wcImage.setImageDrawable(upIcon);
                    wcText = wc + "(+" + wcDiff + ")";
                }else {
                    wcImage.setImageDrawable(downIcon);
                    wcText = wc + "(" + wcDiff + ")";
                }
                wcData.setText(wcText);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    //現在地の新規陽性者数を取得するクラス
    class ClAsync extends AsyncTask<String, Void, String> {

        //apiからjsonを取得
        @Override
        protected String doInBackground(String... params) {
            final StringBuilder result = new StringBuilder();
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https");
            uriBuilder.authority(API_URL_PREFIX);
            uriBuilder.path("/api/Covid19JapanAll");
            uriBuilder.appendQueryParameter("dataName", currentLocation);
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

        //jsonから必要なデータを取得・表示
        @Override
        protected void onPostExecute(String result) { //doInBackgroundが終わると呼び出される
            try {
                //JSONファイルから現在地の累計感染者数を取得
                JSONObject json = new JSONObject(result);
                String itemList = json.getString("itemList");
                JSONArray itemsArray = new JSONArray(itemList);
                //indexを増やすと過去のデータを表示可能
                String cl1 = itemsArray.getJSONObject(0).getString("npatients");
                String cl2 = itemsArray.getJSONObject(1).getString("npatients");
                String cl3 = itemsArray.getJSONObject(2).getString("npatients");
                //取得したデータは3文字置きに","が入っているので削除
                cl1 = cl1.replace(",", "");
                cl2 = cl2.replace(",", "");
                cl3 = cl3.replace(",", "");
                //最新の現在地の感染者数の増加数と前日比を計算
                int cl = Integer.parseInt(cl1) - Integer.parseInt(cl2);
                int clDiff = cl - Integer.parseInt(cl2) + Integer.parseInt(cl3);

                //ImageViewとTextViewを更新
                Resources res = getResources();
                Drawable upIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_information_up, null);
                Drawable downIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_information_down, null);
                String clText;
                if(clDiff >= 0) {
                    clImage.setImageDrawable(upIcon);
                    clText = cl + "(+" + clDiff + ")";
                }else {
                    clImage.setImageDrawable(downIcon);
                    clText = cl + "(" + clDiff + ")";
                }
                clData.setText(clText);

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