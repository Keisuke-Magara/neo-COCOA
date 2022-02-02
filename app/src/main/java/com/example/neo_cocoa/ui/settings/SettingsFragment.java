package com.example.neo_cocoa.ui.settings;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.content.pm.PackageInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.neo_cocoa.R;
import com.example.neo_cocoa.appSettings;
import com.example.neo_cocoa.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private FragmentSettingsBinding binding;
    private appSettings appsettings;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        appsettings = new appSettings(this.getActivity());
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView version_number = view.findViewById(R.id.settings_appversion);
        Switch s = view.findViewById(R.id.settings_bgnotification_switch);
        // get app version.
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
        }

        s.setOnCheckedChangeListener(this);
        s.setChecked(appsettings.getBgNotif());
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
}