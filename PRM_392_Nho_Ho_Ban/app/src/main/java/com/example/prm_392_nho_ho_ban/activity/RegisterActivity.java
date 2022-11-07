package com.example.prm_392_nho_ho_ban.activity;

import static com.example.prm_392_nho_ho_ban.MyApplication.dbRoom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.example.prm_392_nho_ho_ban.dao.RoomUserDAO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtRePassword;
    private Button btnRegister;
    private RoomUserDAO roomUserDAO = dbRoom.createUserDAO();

    private void bindingUI() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressDialog = new ProgressDialog(this);

    }

    private void bindingAction() {
        btnRegister.setOnClickListener(this::register);
    }

    private void register(View view) {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String rePassword = edtRePassword.getText().toString();
        progressDialog.show();
        if (validate(email, password, rePassword)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        User u = new User(user.getUid(), user.getEmail(), password);
                        roomUserDAO.insert(u);
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Register failed, email exist", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
        }
    }

    private boolean validate(String email, String password, String rePassword) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter email?", Toast.LENGTH_LONG).show();
            return false;
        } else {
            Matcher matcher = LoginActivity.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
            if (!matcher.find()) {
                Toast.makeText(this, "Enter right email?", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, "Enter password?(At least 6 characters)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equalsIgnoreCase(rePassword)) {
            Toast.makeText(this, "Your Re-Password is not match", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        bindingUI();
        bindingAction();

    }
}