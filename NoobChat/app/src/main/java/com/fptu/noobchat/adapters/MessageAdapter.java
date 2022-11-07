package com.fptu.noobchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fptu.noobchat.R;
import com.fptu.noobchat.databinding.ChatItemLeftBinding;
import com.fptu.noobchat.databinding.ChatItemRightBinding;
import com.fptu.noobchat.models.Chat;
import com.fptu.noobchat.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<Chat> chatList;
    private FirebaseUser currentUser;
    private String imageURL;

    public MessageAdapter(Context context, List<Chat> chatList, String imageURL) {
        this.context = context;
        this.chatList = chatList;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == Constants.MSG_TYPE_RIGHT) {
            ChatItemRightBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_item_right, parent, false);
            return new ViewHolder(binding);
        } else {
            ChatItemLeftBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_item_left, parent, false);
            return new ViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        if(holder.leftBinding != null) {
            holder.leftBinding.receiverLeftMessage.setText(chat.getMessage());
            if(imageURL.equals("default")) {
                holder.leftBinding.receiverLeftProfileImage.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(context).load(imageURL).into(holder.leftBinding.receiverLeftProfileImage);
            }
            if(position == chatList.size() - 1) {
                if(chat.getisseen()) {
                    holder.leftBinding.txtSeen.setText(Constants.STATUS_SEEN);
                } else {
                    holder.leftBinding.txtSeen.setText(Constants.STATUS_NOT_SEEN_YET);
                }
            } else {
                holder.leftBinding.txtSeen.setVisibility(View.GONE);
            }
        } else {
            holder.rightBinding.senderRightMessage.setText(chat.getMessage());
            if(imageURL.equals("default")) {
                holder.rightBinding.senderRightProfileImage.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(context).load(imageURL).into(holder.rightBinding.senderRightProfileImage);
            }
            if(position == chatList.size() - 1) {
                if(chat.getisseen()) {
                    holder.rightBinding.txtSeen.setText(Constants.STATUS_SEEN);
                } else {
                    holder.rightBinding.txtSeen.setText(Constants.STATUS_NOT_SEEN_YET);
                }
            } else {
                holder.rightBinding.txtSeen.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ChatItemLeftBinding leftBinding;
        ChatItemRightBinding rightBinding;

        public ViewHolder(@NonNull ChatItemLeftBinding leftBinding) {
            super(leftBinding.getRoot());
            this.leftBinding = leftBinding;
        }

        public ViewHolder(@NonNull ChatItemRightBinding rightBinding) {
            super(rightBinding.getRoot());
            this.rightBinding = rightBinding;
        }


    }

    @Override
    public int getItemViewType(int position) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(currentUser.getUid())) {
            return Constants.MSG_TYPE_RIGHT;
        } else {
            return Constants.MSG_TYPE_LEFT;
        }
    }
}
