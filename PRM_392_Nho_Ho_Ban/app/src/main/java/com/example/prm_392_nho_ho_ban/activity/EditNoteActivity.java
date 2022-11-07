package com.example.prm_392_nho_ho_ban.activity;

import static com.example.prm_392_nho_ho_ban.MyApplication.INTERNET_STATE;
import static com.example.prm_392_nho_ho_ban.MyApplication.dbRoom;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.example.prm_392_nho_ho_ban.dao.NoteDAO;
import com.example.prm_392_nho_ho_ban.dao.RoomNoteDAO;
import com.example.prm_392_nho_ho_ban.fragment.FragmentSetNotify;
import com.example.prm_392_nho_ho_ban.schedulingservice.AlarmReceiver;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class EditNoteActivity extends AppCompatActivity {

    private EditText edtTitle;
    private EditText edtaNote;
    private Button btnSave;
    private SharedPreferences sharedPreferences;
    private TextView txtId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Toolbar noteToolbar;
    private Menu noteMenu;

    private boolean notePin;
    private Timestamp createDate;
    private Timestamp remindDate;

    private String timeRemindPick;
    private String dateRemindPick;

    private long dateTimeRemind;

    private boolean setAlarm;

    private boolean noteIsActive;

    FragmentSetNotify fragmentSetNotify;
    FragmentManager fragmentManager;

    private RoomNoteDAO roomNoteDAO = dbRoom.createNoteDAO();

    public void bindingView() {
        edtTitle = findViewById(R.id.edtTitleEdit);
        edtaNote = findViewById(R.id.edtaNoteEdit);
        btnSave = findViewById(R.id.btnSaveNoteEdit);
        txtId = findViewById(R.id.noteId);
        noteToolbar = findViewById(R.id.toolbar);
        fragmentManager = getSupportFragmentManager();
    }

    public void bindingAction() {
        btnSave.setOnClickListener(this::onBtnSaveClick);

        setSupportActionBar(noteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intitialIntent();
    }

    private void intitialIntent() {
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
        dateTimeRemind = remindTimeMillis;
        }
        edtTitle.setText(title);
        edtaNote.setText(content);
        txtId.setText(id);
        notePin = pin;
        createDate = createTime;
        remindDate = remindTime;
        noteIsActive = isActive;
        setAlarm = isAlarm;
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof FragmentSetNotify) {
             fragmentSetNotify= (FragmentSetNotify) fragment;
            fragmentSetNotify.setOnBtnSaveClickListener(this::setDateTimeRemind);
        }
    }

    private void setDateTimeRemind(String datePick, String timePick, boolean alarm) {
        Log.i("TungDt","check :" + datePick + "; " +timePick);
        if(datePick.length()>=0 && timePick.length()>=0) {
            timeRemindPick = timePick;
            dateRemindPick = datePick;
            String toDate = datePick +" "+ timePick;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = null;
            try {
                date = sdf.parse(toDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Timestamp ts = new Timestamp(date);
            remindDate = ts;
            setAlarm = alarm;
        }
//        else  {
//            remindDate = null;
//            setAlarm = false;
//        }
        //        edtaNote.setText(dateRemindPick);
    }

    private void onBtnSaveClick(View view) {
        String title = edtTitle.getText().toString().trim();
        String content = edtaNote.getText().toString().trim();
        String id = txtId.getText().toString().trim();

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            updateNote(title,content, id);

        } else {
            Snackbar.make(view,"Please fill empty fields!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void updateNote(String title, String content, String id) {
        Note updateNote = new Note(id ,title,content, createDate, setAlarm, remindDate, User.USER.getUid(), notePin,true);
        updateNoteDataCallBack(updateNote, id);

    }

    private void updateNoteDataCallBack(Note updateNote, String id) {
        NoteDAO nDAO = new NoteDAO();
        roomNoteDAO.update(updateNote);
        if(updateNote.getDateRemind()!=null && !updateNote.isAlarm() && (updateNote.getDateRemind().getSeconds()*1000> new Date().getTime())){
            setNotiNote(updateNote);
        }else if(updateNote.isAlarm() && (updateNote.getDateRemind().getSeconds()*1000> new Date().getTime())){
            setAlarmNote(updateNote);
        }
        WelcomeActivity.updateFragment();
        if (INTERNET_STATE) {
            nDAO.updateNote(new NoteDAO.FirebaseCallBack() {
                @Override
                public void onCallBack(ArrayList<Note> noteList) {
                }
                @Override
                public void onCallBack() {
                    WelcomeActivity.updateFragment();
                    finish();
                }

                @Override
                public void onCallBack(ArrayList<Note> noteList, ArrayList<Note> noteUnpinList) {

                }
            },updateNote, id);
        } else finish();
    }

    private void setAlarmNote(Note note) {
        cancelOldNote(note);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        String noteJson = new Gson().toJson(note);
        intent.putExtra("noteJson",noteJson);
        int requestCode = Integer.parseInt(note.getId().split("_")[0]);
        intent.setAction("Alarm");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long destinationTime = note.getDateRemind().getSeconds()*1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager
                    .setExact(AlarmManager.RTC_WAKEUP, destinationTime, pendingIntent);
        } else {
            alarmManager
                    .set(AlarmManager.RTC_WAKEUP, destinationTime, pendingIntent);
        }
    }

    private void setNotiNote(Note note) {
        cancelOldNote(note);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        String noteJson = new Gson().toJson(note);
        intent.putExtra("noteJson",noteJson);
        int requestCode = Integer.parseInt(note.getId().split("_")[0]);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long destinationTime = note.getDateRemind().getSeconds()*1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager
                    .setExact(AlarmManager.RTC_WAKEUP, destinationTime, pendingIntent);
        } else {
            alarmManager
                    .set(AlarmManager.RTC_WAKEUP, destinationTime, pendingIntent);
        }
    }

    private void cancelOldNote(Note note){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        int requestCode = Integer.parseInt(note.getId().split("_")[0]);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), requestCode, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.note_menu, menu);
        noteMenu = menu;
        if(String.valueOf(notePin).equals("true")) {
            noteMenu.getItem(0).setIcon(R.drawable.ic_unpin_note);
        }else{

            noteMenu.getItem(0).setIcon(R.drawable.ic_pin_note);
        }

        if(String.valueOf(setAlarm).equals("true")) {
            noteMenu.getItem(1).setIcon(R.drawable.ic_notification_active);
        }
        else {
            noteMenu.getItem(1).setIcon(R.drawable.ic_notifications_note);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuPinNote:
                pinNote();
                break;
            case R.id.menuNoticeNote:
                onNotifySelect();
                break;
            case R.id.menuDeleteNote:
                moveToBin();
                break;
        }
        return true;
    }

    private void onNotifySelect() {
        fragmentSetNotify = new FragmentSetNotify();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putLong("dateTimePick", dateTimeRemind);
        bundle.putBoolean("isNotify", setAlarm);
        fragmentSetNotify.setArguments(bundle);
        fragmentSetNotify.show(fragmentManager, "NotifyFragment");
    }

    private void pinNote() {
        String id = txtId.getText().toString().trim();
        if(String.valueOf(notePin).equals("true")) {
            noteMenu.getItem(0).setIcon(R.drawable.ic_pin_note);
            notePin = false;
        }
        else if(String.valueOf(notePin).equals("false")) {
            noteMenu.getItem(0).setIcon(R.drawable.ic_unpin_note);
            notePin = true;
        }
    }
    
    private void moveToBin() {
        String id = txtId.getText().toString().trim();
        Note updateNoteToBin = new Note(id ,edtTitle.getText().toString() ,edtaNote.getText().toString(),
                createDate, setAlarm, remindDate, User.USER.getUid(), notePin, false);
        updateNoteToBinCallBack(updateNoteToBin, id);
    }

    private void updateNoteToBinCallBack(Note updateNoteToBin, String id) {
        NoteDAO nDAO = new NoteDAO();
        roomNoteDAO.update(updateNoteToBin);
        WelcomeActivity.updateFragment();
        if (INTERNET_STATE) {
            nDAO.updateNote(new NoteDAO.FirebaseCallBack() {
                @Override
                public void onCallBack(ArrayList<Note> noteList) {
                }
                @Override
                public void onCallBack() {
                    WelcomeActivity.updateFragment();
                    finish();
                }

                @Override
                public void onCallBack(ArrayList<Note> noteList, ArrayList<Note> noteUnpinList) {

                }
            },updateNoteToBin, id);
        } else finish();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeOfApp();
        setContentView(R.layout.activity_edit_note);
        bindingView();
        bindingAction();

    }
}