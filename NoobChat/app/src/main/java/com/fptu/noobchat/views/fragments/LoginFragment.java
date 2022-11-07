package com.fptu.noobchat.views.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fptu.noobchat.R;
import com.fptu.noobchat.databinding.FragmentLoginBinding;
import com.fptu.noobchat.models.Login;
import com.fptu.noobchat.viewmodels.LoginViewModel;
import com.fptu.noobchat.views.activities.MainActivity;
import com.fptu.noobchat.views.activities.ResetActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;
    LoginViewModel viewModel;
    FirebaseAuth auth;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        viewModel = new LoginViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        viewModel.getUser().observe(getViewLifecycleOwner(), login -> loginUser(login));
        binding.forgotReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ResetActivity.class);
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }
    private void loginUser(final Login login) {
        auth.signInWithEmailAndPassword(login.getEmail(), login.getPassword())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getContext(), "Login with this user failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}