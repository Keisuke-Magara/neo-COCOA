package com.example.neo_cocoa.ui.radar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RadarViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RadarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is radar fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}