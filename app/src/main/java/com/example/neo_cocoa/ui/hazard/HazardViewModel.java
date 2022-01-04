package com.example.neo_cocoa.ui.hazard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HazardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HazardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is hazard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}