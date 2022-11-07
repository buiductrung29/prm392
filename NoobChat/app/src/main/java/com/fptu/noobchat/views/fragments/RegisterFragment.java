package com.fptu.noobchat.views.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fptu.noobchat.views.activities.MainActivity;
import com.fptu.noobchat.R;
import com.fptu.noobchat.databinding.FragmentRegisterBinding;
import com.fptu.noobchat.models.Register;
import com.fptu.noobchat.viewmodels.RegisterViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterFragment extends Fragment {
    FragmentRegisterBinding binding;
    RegisterViewModel viewModel;
    FirebaseAuth auth;
    DatabaseReference reference;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);

        viewModel = new RegisterViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getNewUser().observe(getViewLifecycleOwner(), this::registerNewUser);
        return binding.getRoot();
    }

    private void registerNewUser(final Register register) {
        auth.createUserWithEmailAndPassword(register.getEmail(), register.getPassword())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        String userId = user.getUid();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("id", userId);
                        userMap.put("userName", register.getUserName());
                        userMap.put("imageURL", "default");
                        userMap.put("status", "offline");
                        userMap.put("search", register.getUserName().toLowerCase());
                        reference.setValue(userMap).addOnCompleteListener(refTask -> {
                            if(refTask.isSuccessful()) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Create new account failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}