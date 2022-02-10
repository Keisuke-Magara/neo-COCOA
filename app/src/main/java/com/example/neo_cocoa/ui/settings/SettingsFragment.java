package com.example.neo_cocoa.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.content.pm.PackageInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.neo_cocoa.R;
import com.example.neo_cocoa.AppSettings;
import com.example.neo_cocoa.databinding.FragmentSettingsBinding;
import com.example.neo_cocoa.quit;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private FragmentSettingsBinding binding;
    private AppSettings appsettings;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        appsettings = new AppSettings(this.getActivity());
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView version_number = view.findViewById(R.id.settings_appversion);
        getAppVersion(version_number);
        Switch s = view.findViewById(R.id.settings_bgnotification_switch);
        Button quit = view.findViewById(R.id.settings_quit_button);
        Button share = view.findViewById(R.id.settings_share_button);

        s.setOnCheckedChangeListener(this);
        s.setChecked(appsettings.getBgNotif());
        quit.setOnClickListener(quit_listener);
        share.setOnClickListener(share_listener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        appsettings.setBgNotif(isChecked);
    }

    // get app version.
    public boolean getAppVersion (TextView version_number) {
        try {
            // get Java package name.
            // android.content.Context#getPackageName
            String name = getActivity().getPackageName();

            // インストールされているアプリケーションパッケージの
            // 情報を取得するためのオブジェクトを取得
            // android.content.Context#getPackageManager
            PackageManager pm = getActivity().getPackageManager();

            // get application package info.
            PackageInfo info = pm.getPackageInfo(name, PackageManager.GET_META_DATA);

            // バージョン番号の文字列を返す
            version_number.setText(info.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version_number.setText("取得できませんでした.");
            return false;
        }
    return true;
    }

    private View.OnClickListener quit_listener = new View.OnClickListener() {
        public void onClick(View view) {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle(getString(R.string.settings_quit_ad_title));
            ad.setMessage(getString(R.string.settings_quit_ad_descripstion));
            ad.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    quit quit = new quit();
                }
            });
            ad.setNegativeButton(getString(R.string.ng), null);
            ad.show();
        }
    };

    private View.OnClickListener share_listener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.playstore_url));
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
    };

}