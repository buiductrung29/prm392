package com.fptu.noobchat.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fptu.noobchat.R;
import com.fptu.noobchat.adapters.MessageAdapter;
import com.fptu.noobchat.databinding.ActivityMessageBinding;
import com.fptu.noobchat.models.Chat;
import com.fptu.noobchat.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {
    ActivityMessageBinding binding;
    FirebaseUser currentUser;
    DatabaseReference reference;
    Intent intent;
    String userId;
    List<Chat> chatList;
    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message);
        binding.chatBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



        binding.messageActivityMessages.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.messageActivityMessages.setLayoutManager(layoutManager);


        intent = getIntent();
        userId = intent.getStringExtra("userId");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                binding.messageActivityUsername.setText(user.getUserName());
                if (user.getImageURL().equals("default")) {
                    binding.messageActivityProfileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(binding.messageActivityProfileImage);
                }
                readMessage(currentUser.getUid(), userId, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.messageActivityBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                notify = true;
                String content = Objects.requireNonNull(binding.messageActivityInput.getText()).toString();
                if (!content.equals("")) {
                    sendMessage(currentUser.getUid(), userId, content);
                }
                binding.messageActivityInput.setText("");
            }
        });
        seenMessage(userId);
    }

    private void sendMessage(String sender, String receiver, String content) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> message = new HashMap<>();
        message.put("sender", sender);
        message.put("receiver", receiver);
        message.put("message", content);
        message.put("isseen", false);
        reference.child("Chats").push().setValue(message);

    }

    private void readMessage(final String currentId, final String userId, final String imageURL) {
        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(currentId) && chat.getSender().equals(userId)
                            || chat.getReceiver().equals(userId) && chat.getSender().equals(currentId)) {
                        chatList.add(chat);
                    }
                    binding.messageActivityMessages.setAdapter(
                            new MessageAdapter(MessageActivity.this, chatList, imageURL)
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void seenMessage(String userId) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isseen", true);
                        snapshot.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        reference.removeEventListener(seenListener);
        status("online");
    }
}