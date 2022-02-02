package com.example.neo_cocoa.ui.proof;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProofViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProofViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is proof fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}