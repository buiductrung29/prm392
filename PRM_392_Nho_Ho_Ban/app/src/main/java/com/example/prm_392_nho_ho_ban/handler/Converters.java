package com.example.prm_392_nho_ho_ban.handler;

import androidx.room.TypeConverter;

import java.sql.Time;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Timestamp fromTimestamp(Long value) {
        return value == null ? null : new Timestamp(new Date(value));
    }

    @TypeConverter
    public static Long dateToTimestamp(Timestamp date) {
        return date == null ? null : date.getSeconds() * 1000;
    }
}
