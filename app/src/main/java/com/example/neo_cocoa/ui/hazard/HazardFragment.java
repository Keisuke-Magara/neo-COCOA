package com.example.neo_cocoa.ui.hazard;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HazardFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    /* config */
    private final static int refreshInterval = 10*1000; // [ms] graph is refreshed in this time.

    private static final String TAG = "HazardFragment";
    private FragmentHazardBinding binding;
    private Timer timer;
    private LocationCallback lc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HazardModel.init();
        binding = FragmentHazardBinding.inflate(inflater, container, false);

        View view = inflater.inflate(R.layout.fragment_hazard, container, false);
        TextView locationView = view.findViewById(R.id.hazard_location_address);
        TextView TimeOfStayView = view.findViewById(R.id.hazard_time_of_stay);
        TextView numOfContactView = view.findViewById(R.id.hazard_num_of_contact);
        TextView dangerLevelView = view.findViewById(R.id.hazard_danger_level_body);
        TextView commentView = view.findViewById(R.id.hazard_danger_level_comment);
        LineChart contactHistory = view.findViewById(R.id.hazard_graph_view);
        configureGraphArea(contactHistory);
        {
            int[] data = GlobalField.hazardData.getNumOfContactHistory();
            contactHistory.setData(createGraphData(data));
            setY_Range(contactHistory, data);
            contactHistory.invalidate();
            contactHistory.animateY(1000, Easing.EaseInBack);
        }
        timer = new Timer();
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("schedule->run");
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int[] data = GlobalField.hazardData.getNumOfContactHistory();
                        contactHistory.setData(createGraphData(data));
                        setY_Range(contactHistory, data);
                        contactHistory.invalidate();
                    }
                });
            }
        }, refreshInterval, refreshInterval);
        Switch demoModeState = view.findViewById(R.id.hazard_demo_switch);
        demoModeState.setChecked(GlobalField.mock_ens.isAlive());
        demoModeState.setOnCheckedChangeListener(this);
        this.lc = new LocationCallback() {
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
                        case 0: dangerLevelView.setTextColor(getResources().getColor(R.color.black, requireActivity().getTheme()));
                                commentView.setText(R.string.hazard_danger_level_0_comment);
                                break;
                        case 1: dangerLevelView.setTextColor(getResources().getColor(R.color.dark_green, requireActivity().getTheme()));
                                commentView.setText(R.string.hazard_danger_level_1_comment);
                                break;
                        case 2: dangerLevelView.setTextColor(getResources().getColor(R.color.blue, requireActivity().getTheme()));
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
                    AppLocationProvider.stopUpdateLocation(lc);
                }
            }
        };
        AppLocationProvider.startUpdateLocation(lc);
        return view;
    }

    
    private void configureGraphArea (LineChart chart) {
        chart.setTouchEnabled(true);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setExtraRightOffset(chart.getExtraRightOffset()+45);
        chart.setExtraBottomOffset(chart.getExtraBottomOffset()+10);
        chart.setBackgroundColor(Color.LTGRAY);
        chart.setNoDataText(getResources().getString(R.string.hazard_graph_no_data_text));
        chart.setNoDataTextColor(Color.BLACK);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        leftAxis.setLabelCount(1, true);
        leftAxis.setDrawGridLines(true);
        Legend l = chart.getLegend();
        l.setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Description des = new Description();
        des.setText(" (時間前)");
        des.setXOffset(-40);
        des.setYOffset(-12);
        chart.setDescription(des);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                super.getFormattedValue(value);
                return String.valueOf((int) value) + "人";
            }
        });
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                super.getFormattedValue(value);
                int val = (int) (-1*value);
                if (val == 0) {
                    return "今";
                } else {
                    return String.valueOf(val);
                }
            }
        });
    }
    private LineData createGraphData(int[] data) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i=data.length-1; i>=0; i--) {
            entries.add(new Entry(-i, data[i]));
        }
        LineDataSet dataSet = new LineDataSet(entries, "history of number of contact people");
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
    }

    private void setY_Range(LineChart chart, int[] data) {
        int max = -1;
        int interval = 10;
        boolean force = false;
        for (int i=0; i<data.length; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }
        if (max < 1) {
            max = 1;
        }
        if (max < 10) {
            interval = max+1;
            force = true;
        } else {
            // do nothing.
        }
        YAxis axis = chart.getAxisLeft();
        axis.setAxisMinimum(0);
        axis.setAxisMaximum(max);
        axis.setLabelCount(interval, force);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        AppLocationProvider.stopUpdateLocation(lc);
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