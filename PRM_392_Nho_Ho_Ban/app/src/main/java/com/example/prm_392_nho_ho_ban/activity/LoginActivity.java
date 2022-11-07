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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.example.prm_392_nho_ho_ban.dao.NoteDAO;
import com.example.prm_392_nho_ho_ban.dao.RoomNoteDAO;
import com.example.prm_392_nho_ho_ban.dao.RoomUserDAO;
import com.example.prm_392_nho_ho_ban.schedulingservice.AlarmReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    private EditText edtEmail;
    private EditText edtPassword;
    private TextView tvForgot;
    private Button btnLogin;
    private Button btnRegister;
    private RoomUserDAO roomUserDAO = dbRoom.createUserDAO();
    private RoomNoteDAO roomNoteDAO = dbRoom.createNoteDAO();
    private void bindingUI(){
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgot = findViewById(R.id.tvForgot);
        btnRegister = findViewById(R.id.btnRegister);
        progressDialog = new ProgressDialog(this);
    }
    private void bindingAction(){
        btnLogin.setOnClickListener(this::login);
        btnRegister.setOnClickListener(this::startRegisterIntent);
        tvForgot.setOnClickListener(this::forgot);
    }

    private void forgot(View view) {
        startActivity(new Intent(this,ResetPasswordActivity.class));
    }

    private void startRegisterIntent(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void login(View view) {
       String email = edtEmail.getText().toString();
       String password = edtPassword.getText().toString();
        progressDialog.setTitle("Verifying and fetching your data");
        progressDialog.show();

       if(validate(email,password)){
       firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {

               if(task.isSuccessful()){
                   final FirebaseUser user = firebaseAuth.getCurrentUser();
                    User.USER = user;
                   NoteDAO noteDAO = new NoteDAO();
                   noteDAO.syncFirebaseToRoom(new NoteDAO.FirebaseCallBack() {
                       @Override
                       public void onCallBack(ArrayList<Note> noteList) {

                       }

                       @Override
                       public void onCallBack() {
                           Log.i("USer", user.getUid() + user.getEmail());
                           User u = new User(user.getUid(),user.getEmail(),password);
                           roomUserDAO.insert(u);
                           resetNotifyAndAlarm();
                           progressDialog.dismiss();
                           startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                       }

                       @Override
                       public void onCallBack(ArrayList<Note> noteList, ArrayList<Note> noteUnpinList) {
                       }
                   }, user.getUid());
               }else{
                   progressDialog.dismiss();
                   Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_LONG).show();
                   finish();
               }
           }
       });
       }else{
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
                if (note.getDateRemind() != null && !note.isAlarm() && note.getDateRemind().getSeconds()*1000 > new Date().getTime()) {
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
                }
                else if(note.getDateRemind()!=null && note.isAlarm() && note.getDateRemind().getSeconds()*1000 > new Date().getTime()){
                    Intent intentt = new Intent(getApplicationContext(), AlarmReceiver.class);
                    String noteJson = new Gson().toJson(note);
                    intent.putExtra("noteJson",noteJson);
                    intent.setAction("Alarm");
                    @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent =
                            PendingIntent.getBroadcast(getApplicationContext(), requestCode, intentt, PendingIntent.FLAG_UPDATE_CURRENT);
                    long destinationTime = note.getDateRemind().getSeconds()*1000 + 10000;
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

    private boolean validate(String email, String password){

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Enter email?",Toast.LENGTH_LONG).show();
            return false;
        }else {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
            if(!matcher.find()){
            Toast.makeText(this,"Enter right email?",Toast.LENGTH_LONG).show();
            return false;}
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter password?",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        bindingUI();
        bindingAction();
    }

    @Override
    protected void onStart() {
        super.onStart();
        authorize();
    }

    private void authorize() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }
    }
}