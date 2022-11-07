package com.fptu.noobchat.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fptu.noobchat.R;
import com.fptu.noobchat.databinding.ActivityResetBinding;
import com.fptu.noobchat.models.Reset;
import com.fptu.noobchat.viewmodels.ResetViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {
    private ActivityResetBinding binding;
    private ResetViewModel viewModel;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset);
        viewModel = ViewModelProviders.of(this).get(ResetViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getReset().observe(this, new Observer<Reset>() {
            @Override
            public void onChanged(Reset reset) {
                resetPassword(reset.getEmail());
            }
        });
        binding.executePendingBindings();
    }


    private void resetPassword(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ResetActivity.this, "Please check email inbox", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetActivity.this, AuthenticationActivity.class);
                    startActivity(intent);
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ResetActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}