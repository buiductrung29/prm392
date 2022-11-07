package com.example.prm_392_nho_ho_ban.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prm_392_nho_ho_ban.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ProgressDialog progressDialog;
    EditText edtEmail;
    Button btnSend;

    private void bindingUI() {
        edtEmail = findViewById(R.id.edtEmail);
        btnSend = findViewById(R.id.btnSendReset);
        progressDialog = new ProgressDialog(this);
    }

    private void bindingAction() {
        btnSend.setOnClickListener(this::sendEmail);
    }

    private void sendEmail(View view) {
        String email = edtEmail.getText().toString();
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Please check your email", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Send failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        bindingUI();
        bindingAction();
    }
}