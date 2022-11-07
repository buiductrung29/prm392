package com.example.prm_392_nho_ho_ban.bean;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.google.firebase.Timestamp;

@Entity(tableName = "note", primaryKeys = {"id","uId"},
        indices = {@Index(value = {"id", "uId"},
        unique = true)})
public class Note {
    @NonNull
    private String id;
    @ColumnInfo
    private String title;
    @ColumnInfo
    private String content;
    @ColumnInfo
    private Timestamp dateCreate;
    @ColumnInfo
    private boolean alarm;
    @ColumnInfo
    private Timestamp dateRemind;
    @ColumnInfo @NonNull
    private String uId;
    @ColumnInfo
    private boolean pin;
    @ColumnInfo
    private boolean active;

    public Note() {
    }

    public Note(String id) {
        this.id=id;
    }

    public Note(String id, String title, String content, Timestamp dateCreate, boolean alarm, Timestamp dateRemind, String uId, boolean active) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dateCreate = dateCreate;
        this.alarm = alarm;
        this.dateRemind = dateRemind;
        this.uId = uId;
        this.active = active;
    }

    public Note(String id, String title, String content, Timestamp dateCreate, boolean alarm, Timestamp dateRemind, String uId, boolean pin, boolean isActive) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dateCreate = dateCreate;
        this.alarm = alarm;
        this.dateRemind = dateRemind;
        this.uId = uId;
        this.pin = pin;
        this.active = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Timestamp dateCreate) {
        this.dateCreate = dateCreate;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public Timestamp getDateRemind() {
        return dateRemind;
    }

    public void setDateRemind(Timestamp dateRemind) {
        this.dateRemind = dateRemind;
    }

    public String getUId() {
        return uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }

    public boolean isPin() {
        return pin;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
