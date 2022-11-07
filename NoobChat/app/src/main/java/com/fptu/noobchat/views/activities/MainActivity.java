package com.fptu.noobchat.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.fptu.noobchat.R;
import com.fptu.noobchat.adapters.AuthenticationAdapter;
import com.fptu.noobchat.adapters.MainAdapter;
import com.fptu.noobchat.databinding.ActivityMainBinding;
import com.fptu.noobchat.models.User;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseUser currentUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                binding.mainActivityUsername.setText(user.getUserName());
                //if not set image yet
                if (user.getImageURL().equals("default")) {
                    binding.mainActivityProfileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(binding.mainActivityProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        initViewPagerLayout();
        logoutUser();
    }

    private void logoutUser() {
        binding.mainActivityLogoutBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Do you want to log out?")
                    .setPositiveButton("Yes", (dialog, i) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }).setNegativeButton("No", (dialog, i) -> dialog.cancel())
                    .show();
        });
    }

    private void initViewPagerLayout() {
        binding.mainContainer.setAdapter(new MainAdapter(this));
        new TabLayoutMediator(binding.mainLayout, binding.mainContainer, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Chats");
                    break;
                case 1:
                    tab.setText("Users");
                    break;
                default:
                    tab.setText("Profile");
                    break;
            }
        }).attach();
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        reference.updateChildren(map);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }
}