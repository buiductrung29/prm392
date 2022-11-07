package com.example.prm_392_nho_ho_ban.bean;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
@Entity(tableName = "user", primaryKeys = {"id"},
        indices = {@Index(value = {"id"},
                unique = true)})
public class User {
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseUser USER = firebaseAuth.getCurrentUser();

    @NonNull
    private String id;
    @ColumnInfo
    private String email;
    @ColumnInfo
    private String password;

    public User(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
