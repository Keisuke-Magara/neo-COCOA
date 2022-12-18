package com.example.neo_cocoa.ui.information;

import android.content.ClipData;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.icu.number.Scale;
import android.icu.text.SimpleDateFormat;
import android.icu.text.SymbolTable;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neo_cocoa.AppInformation;
import com.example.neo_cocoa.AppLocationProvider;
import com.example.neo_cocoa.AppProof;
import com.example.neo_cocoa.GlobalField;
import com.example.neo_cocoa.MainActivity;
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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CancellationException;

public class InformationFragment extends Fragment {
    private FragmentInformationBinding binding;
    private AppInformation appInformation;
    private InformationFragment informationFragment;

    private TextView wcData;
    private TextView clData;
    private TextView clTitle;
    private TextView testLoc;
    private TextView ppTitle;
    private ImageView wcImage;
    private ImageView clImage;
    private Drawable upIcon;
    private Drawable downIcon;
    private Button buttonTestLoc;
    private Button buttonShowDetails;
    private Button buttonViewMore;
    private RecyclerView lvNews;
    private String ppTitleDefault;
    private CancellationTokenSource cts;
    private LinearLayoutManager layout;
    private WcAsync wcAsync;
    private ClAsync clAsync;
    private NewsAsync newsAsync;
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
        buttonViewMore = (Button)view.findViewById(R.id.information_button_view_more);
        buttonViewMore.setOnClickListener(buttonViewMoreListener);
//        buttonTestLoc = (Button)view.findViewById(R.id.testLocationButton);
//        buttonTestLoc.setOnClickListener(buttonTestLocListener);
        return view;
    }

    //新規陽性者数の推移詳細ボタンのリスナ
    Button.OnClickListener buttonShowDetailsListener = new Button.OnClickListener() {
        public void onClick(View view) {
            System.out.println("Show details pressed");
            Uri uri = Uri.parse("https://covid19.mhlw.go.jp/?_fsi=Ew5xhpvh");
            Intent i = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(i);
        }
    };
    //ニュースをさらに表示ボタンのリスナ
    Button.OnClickListener buttonViewMoreListener = new Button.OnClickListener() {
        public void onClick(View view) {
            System.out.println("Show details pressed");
            Uri uri = Uri.parse("https://news.google.com/topics/CAAqRggKIkBDQklTS2pvUVkyOTJhV1JmZEdWNGRGOXhkV1Z5ZVlJQkZRb0lMMjB2TURKcU56RVNDUzl0THpBeFkzQjVlU2dBUAE/sections/CAQqSggAKkYICiJAQ0JJU0tqb1FZMjkyYVdSZmRHVjRkRjl4ZFdWeWVZSUJGUW9JTDIwdk1ESnFOekVTQ1M5dEx6QXhZM0I1ZVNnQVAB?hl=ja&gl=JP&ceid=JP%3Aja");
            Intent i = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(i);
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
        ppTitle =(TextView)view.findViewById(R.id.information_title_num_of_new_positive_person);

        //アイコンの表示
        Resources res = getResources();
        upIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_information_up, null);
        downIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_information_down, null);
        ppTitleDefault = getString(R.string.information_title_num_of_new_positive_person);

        //現在地情報の更新
        cts = new CancellationTokenSource();
        CancellationToken token1 = cts.getToken().onCanceledRequested(new OnTokenCanceledListener() {
            @Override
            public void onCanceled() {
                System.out.println("get location canceled");
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
                        System.out.println("------------------"+Geocoder.isPresent());
                        Geocoder coder = new Geocoder(getContext(), Locale.JAPAN);
                        List<Address> addresses= coder.getFromLocation(task.getResult().getLatitude(), task.getResult().getLongitude(), 1);
                        currentLocation = addresses.get(0).getAdminArea();
                        clTitle.setText(currentLocation);
                    } else {
                        Toast.makeText(getActivity(), "位置情報が取得できませんでした", Toast.LENGTH_LONG).show();
                    }
                } catch (RuntimeExecutionException | IOException tasks) {
//                    AppLocationProvider.goToSettings();
                    Toast.makeText(getActivity(), "位置情報が取得できませんでした", Toast.LENGTH_LONG).show();
                } catch (CancellationException e) {
                    System.out.println("---------------task has already been canceled");
                }
                //現在地の新規陽性者数を取得・表示
                clAsync = new ClAsync();
                clAsync.execute();

            }
        });

        //全国の新規陽性者数を取得・表示
        wcAsync = new WcAsync();
        wcAsync.execute();

        //ニュースを取得・表示;
        newsAsync = new NewsAsync();
        newsAsync.execute();

        //リサイクラービューのデータ表示処理
        lvNews = (RecyclerView)view.findViewById(R.id.information_news_list);
        layout = new LinearLayoutManager(getActivity());
        lvNews.setLayoutManager(layout);

        return;
    }

    //newsのビューホルダークラス
    private class RecyclerListViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNewsTitle;
        public TextView tvNewsDate;
        public TextView tvNewsUrl;
        public RecyclerListViewHolder(View itemView) {
            super(itemView);
            tvNewsTitle = itemView.findViewById(R.id.information_row_title);
            tvNewsDate = itemView.findViewById(R.id.information_row_date);
            tvNewsUrl = itemView.findViewById(R.id.information_row_url);
            System.out.println("------------------RecyclerListViewHolder");
        }
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
                //情報更新日を取得
                String updateDate = itemsArray.getJSONObject(0).getString("date");
                updateDate = updateDate.substring(4,10);
                updateDate = updateDate.replace("-0", "-");
                updateDate = updateDate.substring(1);
                updateDate = updateDate.replace("-", "/");

                //ImageViewとTextViewを更新
                String clText;
                if(clDiff >= 0) {
                    clImage.setImageDrawable(upIcon);
                    clText = cl + "(+" + clDiff + ")";
                }else {
                    clImage.setImageDrawable(downIcon);
                    clText = cl + "(" + clDiff + ")";
                }
                clData.setText(clText);
                ppTitle.setText(ppTitleDefault + "(" + updateDate + ")");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //ニュースを取得するクラス
    class NewsAsync extends AsyncTask<String, Void, String> {

        //apiからjsonを取得
        @Override
        protected String doInBackground(String... params) {
            System.out.println("***********************doInBackground");
            final StringBuilder result = new StringBuilder();
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https");
            uriBuilder.authority("api.rss2json.com");
            uriBuilder.path("/v1/api.json");
            uriBuilder.appendQueryParameter("rss_url", "https://bit.ly/3CPl6t7");
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
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String result) { //doInBackgroundが終わると呼び出される
            try {
                //JSONファイルから全国の累計感染者数を取得
                JSONObject json = new JSONObject(result);
                String itemList = json.getString("items");
                JSONArray itemsArray = new JSONArray(itemList);
                //今日の日時を取得
                Calendar now = Calendar.getInstance();
                System.out.println("time----------"+now.getTime());
                //news情報のlistを作成
                List<Map<String, Object>> newsList = new ArrayList<>();
                for(int n=0; n<10; n++) {
                    Map<String, Object> news = new HashMap<>();
                    news.put("title", itemsArray.getJSONObject(n).getString("title"));
                    Calendar pubDate = Calendar.getInstance();
                    String strPubDate = itemsArray.getJSONObject(n).getString("pubDate");

                    pubDate.set(
                            Integer.valueOf(strPubDate.substring(0,4)),
                            Integer.valueOf(strPubDate.substring(5,7))-1,
                            Integer.valueOf(strPubDate.substring(8,10)),
                            Integer.valueOf(strPubDate.substring(11,13)),
                            Integer.valueOf(strPubDate.substring(14,16)),
                            Integer.valueOf(strPubDate.substring(17,19))
                    );
                    System.out.println(pubDate.getTime());

//                    pubDate = pubDate.add();
                    news.put("date", itemsArray.getJSONObject(n).getString("pubDate"));
                    news.put("url", itemsArray.getJSONObject(n).getString("link"));
                    newsList.add(news);
                }
                RecyclerListAdapter adapter = new RecyclerListAdapter(newsList);
                lvNews.setAdapter(adapter);
                DividerItemDecoration decorator = new DividerItemDecoration(getActivity(), layout.getOrientation());
                lvNews.addItemDecoration(decorator);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //newsのアダプタクラス
    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder> {
        private List<Map<String, Object>> _listData;
        public RecyclerListAdapter(List<Map<String, Object>> listData) {
            System.out.println("-----------------------RecyclerListAdapter");
            _listData = listData;
            System.out.println("**************************"+listData);
        }
        @Override
        public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            System.out.println("----------------------onCreateViewHolder");
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.information_row, parent, false);
            view.setOnClickListener(new ItemClickListener());
            RecyclerListViewHolder holder = new RecyclerListViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(RecyclerListViewHolder holder, int position) {
            System.out.println("------------onBindViewHolder");
            Map<String, Object> item = _listData.get(position);
            String newsTitle = (String) item.get("title");
            String newsDate = (String) item.get("date");
            String newsUrl = (String) item.get("url");
            holder.tvNewsTitle.setText(newsTitle);
            holder.tvNewsDate.setText(newsDate);
            holder.tvNewsUrl.setText(newsUrl);
        }
        @Override
        public int getItemCount() {
            return _listData.size();
        }
    }

    //newsのリスナクラス
    private  class  ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TextView tvNewsUrl = view.findViewById(R.id.information_row_url);
            String url = tvNewsUrl.getText().toString();
            Uri uri = Uri.parse(url);
            Intent i = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(i);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wcAsync.cancel(true);
        cts.cancel();
        try {
            clAsync.cancel(true);
        }catch (NullPointerException e) {
            System.out.println("clAsync does not exist");
        }
        newsAsync.cancel(true);
        System.out.println("stop Async");
        binding = null;
    }
}