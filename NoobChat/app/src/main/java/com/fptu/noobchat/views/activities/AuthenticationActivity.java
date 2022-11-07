package com.fptu.noobchat.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.fptu.noobchat.R;
import com.fptu.noobchat.adapters.AuthenticationAdapter;
import com.fptu.noobchat.databinding.ActivityAuthenticationBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {
    ActivityAuthenticationBinding binding;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication);
        initViewPagerLayout();
    }

    private void initViewPagerLayout() {
        binding.authenticationContainer.setAdapter(new AuthenticationAdapter(this));
        new TabLayoutMediator(binding.authenticationLayout, binding.authenticationContainer, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Login");
                    break;
                case 1:
                    tab.setText("Register");
                    break;
                case 2:
                    tab.setText("About");
                    break;
            }
        }).attach();
    }
}

