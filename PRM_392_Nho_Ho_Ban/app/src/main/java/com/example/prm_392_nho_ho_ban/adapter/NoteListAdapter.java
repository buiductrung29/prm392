package com.example.prm_392_nho_ho_ban.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.activity.EditNoteActivity;
import com.example.prm_392_nho_ho_ban.bean.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.WordViewHolder> {

    private final ArrayList<Note> noteList;
    LayoutInflater mInflater;
    Context context;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");

    public NoteListAdapter(Context context, ArrayList<Note> noteList) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.noteList = noteList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(ArrayList<Note> datas){

        noteList.clear();
        noteList.addAll(datas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteListAdapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(
                R.layout.note_item_recycleview, parent, false);
        return new WordViewHolder(mItemView, this);

    }

    @Override
    public void onBindViewHolder(@NonNull NoteListAdapter.WordViewHolder holder, int position) {
        Note mCurrent = noteList.get(position);
        String title = mCurrent.getTitle();
        String content = mCurrent.getContent();
        if(mCurrent.isPin()){
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_pin_note_display));
        } else {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_note_display));
        }
        holder.createDate = mCurrent.getDateCreate().getSeconds()*1000;
        if(mCurrent.getDateRemind()!=null){
        holder.remindDate = mCurrent.getDateRemind().getSeconds()*1000;
        }else{
            holder.remindDate = 0;
        }
        holder.pin = mCurrent.isPin();
        holder.id = mCurrent.getId();

        holder.active = mCurrent.isActive();

        holder.alarm = mCurrent.isAlarm();

        holder.tvNoteTitle.setText(title);
        holder.tvNoteContent.setText(content);
        if(mCurrent.getDateRemind()!=null){
        holder.tvDate.setText(sdf.format(new Date(mCurrent.getDateRemind().getSeconds()*1000)));
        }else{
            holder.tvDate.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final NoteListAdapter mAdapter;
        protected TextView tvNoteTitle;
        protected TextView tvNoteContent;
        protected TextView tvDate;
        protected String id;
        protected boolean pin;
        protected View view;
        protected long createDate;
        protected long remindDate;
        protected boolean active;
        protected boolean alarm;

        public WordViewHolder(@NonNull View itemView, NoteListAdapter adapter) {
            super(itemView);
            tvNoteTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvNoteContent = itemView.findViewById(R.id.tvNoteContent);
            tvDate = itemView.findViewById(R.id.tvDate);
            view = itemView;
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           Intent i = new Intent(v.getContext(),EditNoteActivity.class);
                i.putExtra("title",tvNoteTitle.getText().toString());
                i.putExtra("content",tvNoteContent.getText().toString());
                i.putExtra("id",id);
                i.putExtra("pin", pin);
                i.putExtra("create", createDate);
                i.putExtra("remind", remindDate);
                i.putExtra("active", active);
                i.putExtra("alarm", alarm);
                v.getContext().startActivity(i);

        }
    }
}
