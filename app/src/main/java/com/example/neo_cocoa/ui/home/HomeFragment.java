package com.example.neo_cocoa.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.neo_cocoa.GlobalField;
import com.example.neo_cocoa.R;
import com.example.neo_cocoa.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View view;
    private TextView activeTitle;
    private ImageView activeIcon;
    private ImageView activeIconEffect;
    private Drawable stopIcon;
    private Button registerPositiveButton;
    private Button shareButton;
    private ActiveIconAsync activeIconAsync;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        view = inflater.inflate(R.layout.fragment_home, container, false);

        //陽性情報の登録ボタンのリスナを設定
        registerPositiveButton = view.findViewById(R.id.home_button_register_positive);
        Button.OnClickListener registerPositiveButtonListener= new Button.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.home_toast_register_positive, Toast.LENGTH_SHORT).show();
            }
        };
        registerPositiveButton.setOnClickListener(registerPositiveButtonListener);

        //共有ボタンのリスナを設定
        shareButton = view.findViewById(R.id.home_button_share);
        Button.OnClickListener shareButtonListener = new Button.OnClickListener() {
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.playstore_url));
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        };
        shareButton.setOnClickListener(shareButtonListener);

        //画面の表示データを更新
        refreshHomeFragment();

        return view;
    }

    @SuppressLint("ResourceAsColor")
    public void refreshHomeFragment() {
        //動作中の表示
        activeIcon = view.findViewById(R.id.home_icon_active);
        activeIconEffect = view.findViewById(R.id.home_icon_active_effect);
        activeTitle = view.findViewById(R.id.home_title_active);
        if(GlobalField.mock_ens!=null && GlobalField.mock_ens.isAlive()) {
            //動作中であれば動作中アイコンを表示
            activeIcon.setColorFilter(Color.GREEN);
            activeIconEffect.setColorFilter(Color.GREEN);
            activeIconEffect.setAlpha(0.25f);
            activeIconAsync = new ActiveIconAsync();
            activeIconAsync.execute();
        }else {
            //動作中でなければ停止中アイコンを表示
            Resources res = getResources();
            stopIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_stop, null);
            activeIcon.setImageDrawable(stopIcon);
            activeIcon.setColorFilter(Color.RED);
            activeIconEffect.setAlpha(0.0f);
            activeTitle.setText(R.string.home_title_stop);

        }

    }

    //動作中アイコンのアニメーションを表示するクラス
    public class ActiveIconAsync extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer ... params) {
            while(!isCancelled()){
                zoomIn();
                try{
                    Thread.sleep(1501);
                }catch (InterruptedException e) {
                }
                fadeOut();
                try{
                    Thread.sleep(1200);
                }catch (InterruptedException e) {
                }
            }
            return 0;
        }
        //エフェクトをフェードアウトさせる関数
        private void fadeOut() {
            AlphaAnimation alphaFadeout = new AlphaAnimation(1.0f, 0.0f);
            alphaFadeout.setDuration(200);
            alphaFadeout.setFillAfter(true);
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                activeIconEffect.startAnimation(alphaFadeout);
            });
        }
        //エフェクトをズームインする関数
        private void zoomIn() {
            ScaleAnimation scaleZoomIn = new ScaleAnimation(0.6f, 1.35f, 0.6f, 1.35f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleZoomIn.setDuration(1500);
            scaleZoomIn.setRepeatCount(0);
            scaleZoomIn.setFillAfter(true);
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                activeIconEffect.startAnimation(scaleZoomIn);
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            activeIconAsync.cancel(true);
            System.out.println("stop async");
        }catch (NullPointerException e) {
            System.out.println("activeIconAsync does not exist");
        }
        binding = null;
    }

}