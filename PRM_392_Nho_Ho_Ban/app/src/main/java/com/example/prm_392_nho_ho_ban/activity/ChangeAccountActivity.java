package com.example.prm_392_nho_ho_ban.activity;

import static com.example.prm_392_nho_ho_ban.MyApplication.dbRoom;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.example.prm_392_nho_ho_ban.dao.NoteDAO;
import com.example.prm_392_nho_ho_ban.dao.RoomNoteDAO;
import com.example.prm_392_nho_ho_ban.schedulingservice.AlarmReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

public class ChangeAccountActivity extends AppCompatActivity {
    private RoomNoteDAO roomNoteDAO = dbRoom.createNoteDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        cancelAllAlarmAndNotify();
        if (getIntent() != null) {
            String userEmail = getIntent().getStringExtra("userEmail");
            String password = getIntent().getStringExtra("password");
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        final FirebaseUser user = firebaseAuth.getCurrentUser();
                        User.USER = user;
                        NoteDAO noteDAO = new NoteDAO();
                        noteDAO.syncFirebaseToRoom(new NoteDAO.FirebaseCallBack() {
                            @Override
                            public void onCallBack(ArrayList<Note> noteList) {

                            }

                            @Override
                            public void onCallBack() {
                                resetNotifyAndAlarm();
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                            }

                            @Override
                            public void onCallBack(ArrayList<Note> noteList, ArrayList<Note> noteUnpinList) {
                            }
                        }, user.getUid());
                    } else {
                        finish();
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
        }

    }

    private void resetNotifyAndAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        ArrayList<Note> allNoteList = (ArrayList<Note>) roomNoteDAO.getAllNote(User.USER.getUid());
        if (!allNoteList.isEmpty()) {
            for (Note note : allNoteList) {
                int requestCode = Integer.parseInt(note.getId().split("_")[0]);
                if (note.getDateRemind() != null && !note.isAlarm() && note.getDateRemind().getSeconds() * 1000 > new Date().getTime()) {
                    String noteJson = new Gson().toJson(note);
                    intent.putExtra("noteJson", noteJson);
                    @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                            PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);// fix this
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager
                                .setExact(AlarmManager.RTC_WAKEUP, new Date().getTime() + 10000, pendingIntent);
                    } else {
                        alarmManager
                                .set(AlarmManager.RTC_WAKEUP, new Date().getTime() + 10000, pendingIntent);
                    }
                } else if (note.getDateRemind() != null && note.isAlarm() && note.getDateRemind().getSeconds() * 1000 > new Date().getTime()) {
                    Intent intentt = new Intent(getApplicationContext(), AlarmReceiver.class);
                    String noteJson = new Gson().toJson(note);
                    intent.putExtra("noteJson", noteJson);
                    intent.setAction("Alarm");
                    @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                            PendingIntent.getBroadcast(getApplicationContext(), requestCode, intentt, PendingIntent.FLAG_UPDATE_CURRENT);
                    long destinationTime = note.getDateRemind().getSeconds() * 1000 + 10000;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager
                                .setExact(AlarmManager.RTC_WAKEUP, destinationTime, pendingIntent);
                    } else {
                        alarmManager
                                .set(AlarmManager.RTC_WAKEUP, destinationTime, pendingIntent);
                    }
                }
            }
        }
    }

    private void cancelAllAlarmAndNotify() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        ArrayList<Note> noteList = (ArrayList<Note>) roomNoteDAO.getAllNote(User.USER.getUid());
        for (Note note : noteList) {
            if (note.getDateRemind() != null) {
                int requestCode = Integer.parseInt(note.getId().split("_")[0]);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), requestCode, myIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
        }
    }
}