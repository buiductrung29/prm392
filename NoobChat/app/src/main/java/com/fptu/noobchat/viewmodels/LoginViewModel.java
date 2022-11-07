package com.fptu.noobchat.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fptu.noobchat.models.Login;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();


    private MutableLiveData<Login> user;

    public MutableLiveData<Login> getUser() {
        if (user == null) {
            user = new MutableLiveData<>();
        }
        return user;
    }

    public void loginButtonClick() {
        Login login = new Login(email.getValue(), password.getValue());
        user.setValue(login);
    }
}
