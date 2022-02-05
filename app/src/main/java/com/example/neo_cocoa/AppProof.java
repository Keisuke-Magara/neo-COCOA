package com.example.neo_cocoa;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class AppProof {
    private Float bodyTemperature;
    private Integer numOfVaccine;
    private Integer vaccineDate;
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

    public void setVaccineDate(Integer nd) {
        this.vaccineDate = nd;
        editor.putInt("vaccineDate", nd);
        editor.apply();
        this.refreshData();
    }

    public Integer getVaccineDate() {
        this.refreshData();
        return  this.vaccineDate;
    }

    public void refreshData() {
//        editor.putFloat("bodyTemperature", 0.0F);
//        editor.apply();
        this.bodyTemperature = this.sharedPref.getFloat("bodyTemperature", 0.0F);
        this.numOfVaccine = this.sharedPref.getInt("numOfVaccine", 0);
        this.vaccineDate = this.sharedPref.getInt("vaccineDate", 0);

    }
}