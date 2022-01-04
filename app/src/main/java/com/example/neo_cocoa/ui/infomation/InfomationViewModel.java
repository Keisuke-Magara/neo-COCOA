package com.example.neo_cocoa.ui.infomation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfomationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public InfomationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is infomation fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}