package com.example.prm_392_nho_ho_ban;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.prm_392_nho_ho_ban.handler.Converters;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.example.prm_392_nho_ho_ban.bean.User;
import com.example.prm_392_nho_ho_ban.dao.RoomNoteDAO;
import com.example.prm_392_nho_ho_ban.dao.RoomUserDAO;

@Database(entities = {Note.class, User.class},version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RoomNoteDAO createNoteDAO();
    public abstract RoomUserDAO createUserDAO();

}
