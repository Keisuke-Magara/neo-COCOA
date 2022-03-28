package com.example.neo_cocoa.ui.hazard;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Icon;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.neo_cocoa.AppLocationProvider;
import com.example.neo_cocoa.GlobalField;
import com.example.neo_cocoa.R;
import com.example.neo_cocoa.databinding.FragmentHazardBinding;
import com.example.neo_cocoa.hazard_models.HazardModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HazardFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "HazardFragment";
    private FragmentHazardBinding binding;
    private Timer timer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HazardModel.init(getContext());
        binding = FragmentHazardBinding.inflate(inflater, container, false);

        View view = inflater.inflate(R.layout.fragment_hazard, container, false);
        TextView locationView = view.findViewById(R.id.hazard_location_address);
        TextView TimeOfStayView = view.findViewById(R.id.hazard_time_of_stay);
        TextView numOfContactView = view.findViewById(R.id.hazard_num_of_contact);
        TextView dangerLevelView = view.findViewById(R.id.hazard_danger_level_body);
        TextView commentView = view.findViewById(R.id.hazard_danger_level_comment);
        LineChart contactHistory = view.findViewById(R.id.hazard_graph_view);
        contactHistory.setMinimumWidth(800);
        contactHistory.setBackgroundColor(Color.LTGRAY);
        contactHistory.setNoDataText(getResources().getString(R.string.hazard_graph_no_data_text));
        contactHistory.setNoDataTextColor(Color.BLACK);
        contactHistory.animateY(2000, Easing.EaseInBack);
        timer = new Timer();
        Handler handler = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("schedule->run");
                contactHistory.setData(createGraphData(contactHistory, GlobalField.hazardData.getNumOfContactHistory()));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Runnnable->run");
                        contactHistory.setData(createGraphData(contactHistory, GlobalField.hazardData.getNumOfContactHistory()));
                        contactHistory.notifyDataSetChanged();

                    }
                });
            }
        }, 3000, 3600000);
        Switch demoModeState = view.findViewById(R.id.hazard_demo_switch);
        demoModeState.setChecked(GlobalField.mock_ens.isAlive());
        demoModeState.setOnCheckedChangeListener(this);
        AppLocationProvider.startUpdateLocation(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                try {
                    Location location = locationResult.getLastLocation();
                    String addressText = getResources().getString(R.string.hazard_location_address);
                    if (Geocoder.isPresent()) {
                        Geocoder coder = new Geocoder(getContext(), Locale.JAPANESE);
                        String address = HazardModel.getCurrentAddress(location, coder, addressText);
                        if (Objects.equals(address, "ERROR")) {
                            locationView.setText(R.string.getting_error);
                        } else {
                            locationView.setText(HazardModel.getCurrentAddress(location, coder, addressText));
                        }
                    } else {
                        addressText = addressText.replace("XXX", locationResult.getLastLocation().getLatitude() + ", " + locationResult.getLastLocation().getLongitude());
                        locationView.setText(addressText);
                    }
                    String time_of_stay_text = getResources().getString(R.string.hazard_time_of_stay);
                    time_of_stay_text = time_of_stay_text.replace("XXX", Long.toString((System.currentTimeMillis() - HazardModel.getStartTimeOfStay())/60000));
                    TimeOfStayView.setText(time_of_stay_text);
                    String num_of_contact_text = getResources().getString(R.string.hazard_num_of_contact);
                    int num_of_contact = HazardModel.getCurrentContact(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    String dangerLevel_text = getResources().getString(R.string.hazard_danger_level_body);
                    int dangerLevel = HazardModel.getDangerLevel(num_of_contact);
                    dangerLevel_text = dangerLevel_text.replace("XXX", String.valueOf(dangerLevel));
                    num_of_contact_text = num_of_contact_text.replace("XXX", String.valueOf(num_of_contact));
                    numOfContactView.setText(num_of_contact_text);
                    dangerLevelView.setText(dangerLevel_text);
                    switch (dangerLevel) {
                        case 0: dangerLevelView.setTextColor(Color.BLACK);
                                commentView.setText(R.string.hazard_danger_level_0_comment);
                                break;
                        case 1: dangerLevelView.setTextColor(getResources().getColor(R.color.dark_green, requireActivity().getTheme()));
                                commentView.setText(R.string.hazard_danger_level_1_comment);
                                break;
                        case 2: dangerLevelView.setTextColor(Color.BLUE);
                                commentView.setText(R.string.hazard_danger_level_2_comment);
                                break;
                        case 3: dangerLevelView.setTextColor(getResources().getColor(R.color.orange, requireActivity().getTheme()));
                                commentView.setText(R.string.hazard_danger_level_3_comment);
                                break;
                        case 4: dangerLevelView.setTextColor(Color.MAGENTA);
                                commentView.setText(R.string.hazard_danger_level_4_comment);
                                break;
                        case 5: dangerLevelView.setTextColor(Color.RED);
                                commentView.setText(R.string.hazard_danger_level_5_comment);
                                break;
                        default:commentView.setText(R.string.hazard_danger_level_0_comment);
                    }
                } catch (IllegalStateException ie) {
                    //Log.d(TAG, ie.toString());
                    AppLocationProvider.stopUpdateLocation();
                }
            }
        });
        return view;
    }

    static class refreshGraph extends Thread{
    }

    private LineData createGraphData(final LineChart chart, int[] data) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i=0; i<data.length; i++) {
            entries.add(new Entry(-8+i, data[i]));
        }
        LineDataSet dataSet;
        /*if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            dataSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            return null;
        } else {*/
            dataSet = new LineDataSet(entries, "history of number of contact people");
            dataSet.setDrawIcons(true);
            dataSet.setColor(Color.BLUE);
            dataSet.setLineWidth(1f);
            dataSet.setCircleRadius(3f);
            dataSet.setDrawCircleHole(true);
            dataSet.setValueTextSize(1f);
            dataSet.setDrawFilled(true);
            dataSet.setFormLineWidth(1f);

            dataSet.setFormLineDashEffect(new DashPathEffect(new float[] {10f, 5f}, 0f));
            dataSet.setFormSize(15.f);
            dataSet.setFillColor(Color.RED);
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(dataSet);
            LineData lineData = new LineData(dataSets);
            return lineData;
        //}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        AppLocationProvider.stopUpdateLocation();
        timer.cancel();
    }

    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        System.out.println(isChecked);
        GlobalField.hazardData.setDemoModeState(isChecked);
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
}