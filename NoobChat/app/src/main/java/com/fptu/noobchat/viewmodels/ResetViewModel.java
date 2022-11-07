package com.fptu.noobchat.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fptu.noobchat.models.Reset;

public class ResetViewModel extends ViewModel {
    //for input field
    public MutableLiveData<String> email = new MutableLiveData<>();

    private MutableLiveData<Reset> reset;
    public MutableLiveData<String> emailError;

    public MutableLiveData<Reset> getReset() {
        if(reset == null) {
            reset = new MutableLiveData<>();
        }
        return reset;
    }

    public void resetButtonClick() {
        Reset r = new Reset(email.getValue());
        reset.setValue(r);
    }

}
