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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
    private EditText edtTitle;
    private EditText edtaNote;
    private Button btnSave;
    private Toolbar noteToolbar;
    private Menu noteMenu;
    private SharedPreferences sharedPreferences;
    private RoomNoteDAO roomNoteDAO = dbRoom.createNoteDAO();

    private Timestamp remindTimeSet = null;

    private String timeRemindPick;
    private String dateRemindPick;

    private boolean setAlarm;
    private boolean pin = false;
    FragmentSetNotify fragmentSetNotify;
    FragmentManager fragmentManager;

    public void bindingView() {
        edtTitle = findViewById(R.id.edtTitle);
        edtaNote = findViewById(R.id.edtaNote);
        btnSave = findViewById(R.id.btnSaveNote);
        noteToolbar = findViewById(R.id.toolbar);

        fragmentManager = getSupportFragmentManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void bindingAction()   {
        btnSave.setOnClickListener(this::onBtnSaveClick);
        setSupportActionBar(noteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onBtnSaveClick(View view) {
        String title = edtTitle.getText().toString().trim();
        String content = edtaNote.getText().toString().trim();

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            createNote(view ,title, content);
        } else {
            Snackbar.make(view,"Please fill empty fields!", Snackbar.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof FragmentSetNotify) {
            fragmentSetNotify= (FragmentSetNotify) fragment;
            fragmentSetNotify.setOnBtnSaveClickListener(this::setDateTimeRemind);
        }
    }

    private void setDateTimeRemind(String date, String time, boolean alarm) {
        if(date.length()>=0 && time.length()>=0) {
            timeRemindPick = time;
            dateRemindPick = date;
            String toDate = date +" "+ time;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date dateRemind = null;
            try {
                dateRemind = sdf.parse(toDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Timestamp ts = new Timestamp(dateRemind);
            setAlarm = alarm;
            remindTimeSet = ts;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNote(View view, String title, String content) {
        Note latestNote = roomNoteDAO.getLastestNoteVer2(User.USER.getUid());
        Note note;
        if(latestNote!=null){
            String num = latestNote.getId().split("_")[0];
            String newId = (Integer.parseInt(num)+1) +"_"+ User.USER.getUid();
            note = new Note(newId, title, content, new Timestamp(new Date()), setAlarm, remindTimeSet, User.USER.getUid(), pin,true);
        }else{
            note = new Note("1_"+User.USER.getUid(), title, content, new Timestamp(new Date()), setAlarm, remindTimeSet, User.USER.getUid(), false,true);
        }
        createNoteCallBack(note);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNoteCallBack(Note note) {
        NoteDAO nDAO = new NoteDAO();
        roomNoteDAO.insert(note);
        if(note.getDateRemind()!=null && !note.isAlarm() && (note.getDateRemind().getSeconds()*1000> new Date().getTime())){
            setTimerNotify(note);
        }else if(note.isAlarm() && (note.getDateRemind().getSeconds()*1000> new Date().getTime())){
            setAlarm(note);
        }
        if(INTERNET_STATE) {
        nDAO.syncRoomToFirebase(new NoteDAO.FirebaseCallBack() {
            @Override
            public void onCallBack(ArrayList<Note> noteList) {

            }

            @Override
            public void onCallBack() {
                finish();
                WelcomeActivity.updateFragment();
            }

            @Override
            public void onCallBack(ArrayList<Note> noteList, ArrayList<Note> noteUnpinList) {

            }
        });
        }
        else finish();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setTimerNotify(Note note) {
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

    private void setAlarm(Note note) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.note_menu, menu);
        noteMenu = menu;
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
                finish();
                break;
        }
        return true;
    }

    private void pinNote() {
        if(pin){
            noteMenu.getItem(0).setIcon(R.drawable.ic_pin_note);
            pin = false;
        } else {
            noteMenu.getItem(0).setIcon(R.drawable.ic_unpin_note);
           pin = true;
        }
    }

    private void onNotifySelect() {
        fragmentSetNotify = new FragmentSetNotify();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isNotify", false);
        fragmentSetNotify.setArguments(bundle);
        fragmentSetNotify.show(fragmentManager, "NotifyFragment");
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeOfApp();
        setContentView(R.layout.activity_add_note);
        bindingView();
        bindingAction();
    }
}