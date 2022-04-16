package com.example.neo_cocoa;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class AppProof {
    private Float bodyTemperature;
    private Integer bodyTemperatureDate;
    private Integer numOfVaccine;
    private String vaccineDate;
    private String updateDate;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
//    private enum pref_keys{
//        bodyTemperature,
//        numOfVaccine,
//        vaccineDate
//    }

    public AppProof(Context context) {
        this.sharedPref = context.getSharedPreferences("proof", MODE_PRIVATE);
        editor = sharedPref.edit();
        this.refreshData();
    }

    public void setBodyTemperature(Float bt) {
        this.bodyTemperature = bt;
        editor.putFloat("bodyTemperature", bt);
        editor.apply();
        this.refreshData();
    }

    public Float getBodyTemperature() {
        this.refreshData();
        return this.bodyTemperature;
    }

    public void setBodyTemperatureDate(Integer btd) {
        this.bodyTemperatureDate = btd;
        editor.putInt("bodyTemperatureDate", btd);
        editor.apply();
        this.refreshData();
    }

    public Integer getBodyTemperatureDate() {
        this.refreshData();
        return this.bodyTemperatureDate;
    }

    public void setNumOfVaccine(Integer nov) {
        this.numOfVaccine = nov;
        editor.putInt("numOfVaccine", nov);
        editor.apply();
        this.refreshData();
    }

    public Integer getNumOfVaccine() {
        this.refreshData();
        return this.numOfVaccine;
    }

    public void setVaccineDate(String vd) {
        this.vaccineDate = vd;
        editor.putString("vaccineDate", vd);
        editor.apply();
        this.refreshData();
    }

    public String getVaccineDate() {
        this.refreshData();
        return  this.vaccineDate;
    }

    public void setUpdateDate(String ud) {
        this.updateDate = ud;
        editor.putString("updateDate", ud);
        editor.apply();
        this.refreshData();
    }

    public void refreshData() {
//        editor.putFloat("bodyTemperature", 0.0F);
//        editor.apply();
        this.bodyTemperature = this.sharedPref.getFloat("bodyTemperature", -1F);
        this.bodyTemperatureDate = this.sharedPref.getInt("bodyTemperatureDate", -1);
        this.numOfVaccine = this.sharedPref.getInt("numOfVaccine", -1);
        this.vaccineDate = this.sharedPref.getString("vaccineDate", null);
        this.updateDate = this.sharedPref.getString("updateDate", null);

    }
}