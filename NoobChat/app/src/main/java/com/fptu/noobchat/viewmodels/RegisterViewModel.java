package com.fptu.noobchat.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fptu.noobchat.models.Register;

public class RegisterViewModel extends ViewModel {
    //for input field
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> userName = new MutableLiveData<>();
    public MutableLiveData<String> rePassword = new MutableLiveData<>();

    private MutableLiveData<Register> newUser;

    public MutableLiveData<Register> getNewUser() {
        if(newUser == null) {
            newUser = new MutableLiveData<>();
        }
        return newUser;
    }

    public void registerButtonClick() {
        Register register = new Register(email.getValue(), password.getValue(), userName.getValue());
        newUser.setValue(register);
    }

}
