package com.fptu.noobchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fptu.noobchat.R;
import com.fptu.noobchat.databinding.UserItemBinding;
import com.fptu.noobchat.models.Chat;
import com.fptu.noobchat.models.User;
import com.fptu.noobchat.utils.Constants;
import com.fptu.noobchat.views.activities.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    String last_msg = Constants.DEFAULT;
    private List<User> userList;
    private boolean isPresent;
    public UserAdapter(Context context, List<User> userList, boolean isPresent) {
        this.context = context;
        this.userList = userList;
        this.isPresent = isPresent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.user_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.binding.userItemUsername.setText(user.getUserName());
        if(user.getImageURL().equals("default")) {
            holder.binding.userItemProfileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.binding.userItemProfileImage);
        }

        if(isPresent) {
            lastMessage(user.getId(), holder.binding.   lastMsg);
        } else {
            holder.binding.lastMsg.setVisibility(View.GONE);
        }

        if(isPresent) {
            if(user.getStatus().equals(Constants.STATUS_ONLINE)) {
                holder.binding.imageOn.setVisibility(View.VISIBLE);
                holder.binding.imageOff.setVisibility(View.GONE);
            } else {
                holder.binding.imageOn.setVisibility(View.GONE);
                holder.binding.imageOff.setVisibility(View.VISIBLE);
            }
        } else {
            holder.binding.imageOn.setVisibility(View.GONE);
            holder.binding.imageOff.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            }
        });

        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if(userList == null) return 0;
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        UserItemBinding binding;
        public ViewHolder(@NonNull UserItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void lastMessage(String userId, TextView lastMsg) {
        last_msg = Constants.DEFAULT;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(user.getUid()) && chat.getSender().equals(userId)
                    || chat.getReceiver().equals(userId) && chat.getSender().equals(user.getUid())) {
                        last_msg = chat.getMessage();
                    }
                }
                switch (last_msg) {
                    case Constants.DEFAULT:
                        lastMsg.setText("No message");
                        break;
                    default:
                        lastMsg.setText(last_msg);
                        break;
                }
                last_msg = Constants.DEFAULT;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
