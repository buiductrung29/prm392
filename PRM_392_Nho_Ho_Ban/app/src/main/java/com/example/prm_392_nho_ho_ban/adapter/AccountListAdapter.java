package com.example.prm_392_nho_ho_ban.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.activity.ChangeAccountActivity;
import com.example.prm_392_nho_ho_ban.activity.EditNoteActivity;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.WordViewHolder> {

    private final ArrayList<User> userList;
    LayoutInflater mInflater;
    Context context;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");


    public AccountListAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.userList = userList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(ArrayList<User> datas) {

        userList.clear();
        userList.addAll(datas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountListAdapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(
                R.layout.account_item_recycleview, parent, false);
        return new WordViewHolder(mItemView, this);

    }

    @Override
    public void onBindViewHolder(@NonNull AccountListAdapter.WordViewHolder holder, int position) {
        User mCurrent = userList.get(position);
        String email = mCurrent.getEmail();
        if (email.equals(User.USER.getEmail())) {
            email += "( CURRENT )";
        }
        holder.email.setText(email);
        holder.password = mCurrent.getPassword();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final AccountListAdapter mAdapter;
        protected TextView email;
        protected String password;

        public WordViewHolder(@NonNull View itemView, AccountListAdapter adapter) {
            super(itemView);
            email = itemView.findViewById(R.id.tvUserEmail);
            this.mAdapter = adapter;
            if (!email.getText().toString().equals(User.USER.getEmail())) {
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(context, tvNoteTitle.getText().toString() + " Click ", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(v.getContext(), ChangeAccountActivity.class);
            i.putExtra("userEmail", email.getText());
            i.putExtra("password", password);
            v.getContext().startActivity(i);
        }
    }
}
