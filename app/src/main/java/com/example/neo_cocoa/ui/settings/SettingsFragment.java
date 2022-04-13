package com.example.neo_cocoa.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.neo_cocoa.AppLocationProvider;
import com.example.neo_cocoa.AppSettings;
import com.example.neo_cocoa.GlobalField;
import com.example.neo_cocoa.R;
import com.example.neo_cocoa.databinding.FragmentSettingsBinding;
import com.example.neo_cocoa.quit;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private FragmentSettingsBinding binding;
    private AppSettings appsettings;
    private Switch s;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        appsettings = new AppSettings(this.getActivity());
        appsettings = GlobalField.appSettings;
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView version_number = view.findViewById(R.id.settings_appversion);
        getAppVersion(version_number);
        s = view.findViewById(R.id.settings_bgnotification_switch);
        Button quit = view.findViewById(R.id.settings_quit_button);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!AppLocationProvider.checkBGPermission()) {
                appsettings.setBgNotif(false);
            }
        }
        s.setChecked(appsettings.getBgNotif());
        s.setOnCheckedChangeListener(this);
        quit.setOnClickListener(quit_listener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!AppLocationProvider.checkBGPermission()) {
                    AppLocationProvider.requestBGPermission();
                }
            }
        }
        appsettings.setBgNotif(isChecked);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.hazard_demo_alert_dialog_title);
        builder.setMessage(R.string.hazard_demo_alert_dialog_description)
                .setCancelable(false)
                .setIcon(R.drawable.ic_restart)
                .setPositiveButton(R.string.ShutdownApp, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
        builder.create();
        builder.show();
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

    private final View.OnClickListener quit_listener = new View.OnClickListener() {
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

}