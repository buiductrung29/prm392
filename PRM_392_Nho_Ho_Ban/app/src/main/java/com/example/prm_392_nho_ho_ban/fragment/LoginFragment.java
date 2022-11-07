package com.example.prm_392_nho_ho_ban.fragment;

import static com.example.prm_392_nho_ho_ban.MyApplication.dbRoom;
import static com.example.prm_392_nho_ho_ban.activity.LoginActivity.VALID_EMAIL_ADDRESS_REGEX;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.example.prm_392_nho_ho_ban.R;
import com.example.prm_392_nho_ho_ban.activity.RegisterActivity;
import com.example.prm_392_nho_ho_ban.activity.ResetPasswordActivity;
import com.example.prm_392_nho_ho_ban.activity.WelcomeActivity;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.example.prm_392_nho_ho_ban.dao.NoteDAO;
import com.example.prm_392_nho_ho_ban.dao.RoomUserDAO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class LoginFragment extends DialogFragment {
    private EditText tvEmail;
    private EditText tvPassword;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    private TextView forgot;
    private Button btnRegister;
    private RoomUserDAO roomUserDAO = dbRoom.createUserDAO();

    private SharedPreferences sharedPreferences;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        bindingView(view);
        bindingAction();
        return view;
    }

    private void bindingAction() {
        btnLogin.setOnClickListener(this::login);
        forgot.setOnClickListener(this::forgot);
        btnRegister.setOnClickListener(this::register);
    }

    private void register(View view) {
        startActivity(new Intent(getActivity(), RegisterActivity.class));
    }

    private void forgot(View view) {
        startActivity(new Intent(getActivity(), ResetPasswordActivity.class));
    }

    private void login(View view) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();
        progressDialog.show();
        if (validate(email, password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                User u = new User(user.getUid(), user.getEmail(), password);
                                roomUserDAO.insert(u);
                                progressDialog.dismiss();
                                startActivity(new Intent(getActivity(), WelcomeActivity.class));
                            }

                            @Override
                            public void onCallBack(ArrayList<Note> noteList, ArrayList<Note> noteUnpinList) {
                            }
                        }, user.getUid());
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
        }
    }

    private void bindingView(View view) {
        btnRegister = view.findViewById(R.id.buttonRegister);
        progressDialog = new ProgressDialog(getActivity());
        forgot = view.findViewById(R.id.tvForgotAdd);
        tvEmail = view.findViewById(R.id.username_input);
        tvPassword = view.findViewById(R.id.password_input);
        btnLogin = view.findViewById(R.id.loginButton);
    }

    private void closeFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
        requireActivity().getSupportFragmentManager().popBackStack();
    }


    private boolean validate(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Enter email?", Toast.LENGTH_LONG).show();
            return false;
        } else {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
            if (!matcher.find()) {
                Toast.makeText(getActivity(), "Enter right email?", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Enter password?", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}