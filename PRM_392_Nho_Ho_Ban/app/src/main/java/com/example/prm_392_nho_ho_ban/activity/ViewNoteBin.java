package com.example.prm_392_nho_ho_ban.activity;

import static com.example.prm_392_nho_ho_ban.MyApplication.INTERNET_STATE;
import static com.example.prm_392_nho_ho_ban.MyApplication.dbRoom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.example.prm_392_nho_ho_ban.dao.NoteDAO;
import com.example.prm_392_nho_ho_ban.dao.RoomNoteDAO;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;

public class ViewNoteBin extends AppCompatActivity {

    private EditText txtTitle;
    private EditText txtContent;
    private ImageView iconDelete;
    private ImageView iconRestore;
    private ImageView iconReturn;


    private Timestamp createDate;
    private boolean setAlarm;
    private Timestamp remindDate;
    private boolean notePin;

    private String noteId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db.collection("note");

    private boolean noteIsActive;

    private RoomNoteDAO roomNoteDAO = dbRoom.createNoteDAO();

    private SharedPreferences sharedPreferences;

    private void bindingView() {
        txtTitle = findViewById(R.id.txtTitle);
        txtContent = findViewById(R.id.txtNote);
        iconDelete = findViewById(R.id.icnBin);
        iconRestore = findViewById(R.id.icnRecover);
        iconReturn = findViewById(R.id.icnBack);
    }

    private void bindingAction() {
        iconReturn.setOnClickListener(this::onBackBtnClick);
        iconDelete.setOnClickListener(this::onDeleteBtnClick);
        iconRestore.setOnClickListener(this::onRestoreBtnClick);

        intitialIntent();
    }

    private void intitialIntent(){
        Timestamp remindTime = null;
        Intent receiveIntent = getIntent();
        String title = receiveIntent.getStringExtra("title");
        String content = receiveIntent.getStringExtra("content");
        String id = receiveIntent.getStringExtra("id");
        boolean pin = receiveIntent.getExtras().getBoolean("pin");
        Timestamp createTime = new Timestamp(new Date(receiveIntent.getExtras().getLong("create")));
        long remindTimeMillis = receiveIntent.getExtras().getLong("remind");
        boolean isActive = receiveIntent.getExtras().getBoolean("active");
        boolean isAlarm = receiveIntent.getExtras().getBoolean("alarm");
        if(remindTimeMillis!=0){
            remindTime = new Timestamp(new Date(receiveIntent.getExtras().getLong("remind")));
        }
//        edtTitle.setText(String.valueOf(pin));
        txtTitle.setText(title);
        txtContent.setText(content);
        noteId = id;
        notePin = pin;
        createDate = createTime;
        remindDate = remindTime;
        noteIsActive = isActive;
        setAlarm = isAlarm;
    }

    private void onRestoreBtnClick(View view) {
        String id = noteId;
        String title = txtTitle.getText().toString().trim();
        String content = txtContent.getText().toString().trim();
        Note restoreNote = new Note(id, title, content, createDate, setAlarm, remindDate, User.USER.getUid(), notePin, true);
        restoreNoteDataCallBack(restoreNote, id);
    }

    private void restoreNoteDataCallBack(Note restoreNote, String id) {
        NoteDAO nDAO = new NoteDAO();
        roomNoteDAO.update(restoreNote);
        if (INTERNET_STATE) {
            nDAO.updateNote(new NoteDAO.FirebaseCallBack() {
                @Override
                public void onCallBack(ArrayList<Note> noteList) {
                }
                @Override
                public void onCallBack() {
                    finish();
                }

                @Override
                public void onCallBack(ArrayList<Note> noteList, ArrayList<Note> noteUnpinList) {

                }
            },restoreNote, id);
        } else finish();
    }

    private void onDeleteBtnClick(View view) {
        deleteNoteCallBack(noteId);
    }

    private void deleteNoteCallBack(String noteId) {
        NoteDAO nDAO = new NoteDAO();
        roomNoteDAO.delete(roomNoteDAO.getSelectedNote(User.USER.getUid(), noteId));
        WelcomeActivity.updateFragment();
        if (INTERNET_STATE) {
            nDAO.deleteNote(new NoteDAO.FirebaseCallBack() {
                @Override
                public void onCallBack(ArrayList<Note> noteList) {
                }
                @Override
                public void onCallBack() {
                    finish();
                }

                @Override
                public void onCallBack(ArrayList<Note> noteList, ArrayList<Note> noteUnpinList) {

                }
            }, noteId);
        } else finish();
    }

    private void onBackBtnClick(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeOfApp();
        setContentView(R.layout.activity_view_note_bin);
        bindingView();
        bindingAction();
    }

    public void setThemeOfApp() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        switch (sharedPreferences.getString("color_option", "DEFAULT")) {
            case "DEFAULT":
                setTheme(R.style.Theme_PRM_392_Nho_Ho_Ban);
                break;
            case "BLACK":
                setTheme(R.style.BlackTheme);
                break;
            case "RED":
                setTheme(R.style.RedTheme);
                break;
            case "GREEN":
                setTheme(R.style.GreenTheme);
                break;
            case "PINK":
                setTheme(R.style.PinkTheme);
                break;

        }
    }
}