package com.fptu.noobchat.views.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fptu.noobchat.R;
import com.fptu.noobchat.adapters.UserAdapter;
import com.fptu.noobchat.databinding.FragmentChatBinding;
import com.fptu.noobchat.models.Chat;
import com.fptu.noobchat.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    List<User> mUsers;
    List<String> userList;
    FragmentChatBinding binding;
    private UserAdapter userAdapter;
    private FirebaseUser currentUser;
    private DatabaseReference reference;

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        binding.setLifecycleOwner(this);
        binding.recyclerUserRecentList.setHasFixedSize(true);
        binding.recyclerUserRecentList.setLayoutManager(new LinearLayoutManager(getContext()));
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            userList.clear();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Chat chat = snapshot.getValue(Chat.class);
                if(chat.getSender().equals(currentUser.getUid())) {
                    userList.add(chat.getReceiver());
                }
                if(chat.getReceiver().equals(currentUser.getUid())) {
                    userList.add(chat.getSender());
                }
            }
            readChat();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
        return binding.getRoot();
    }




    private void readChat() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    // display 1 user from chats
                    for (String id : userList) {
                        if(user.getId().equals(id)) {
                            if(mUsers.size() != 0) {
                                if(!mUsers.contains(user)) {
                                    mUsers.add(user);

                                }
                            } else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
                binding.recyclerUserRecentList.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}