package com.example.prm_392_nho_ho_ban.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.prm_392_nho_ho_ban.bean.Note;
import com.google.firebase.Timestamp;
import java.util.List;

@Dao
public interface RoomNoteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Delete
    void delete(Note note);

    @Update
    void update(Note note);

    @Query("SELECT * FROM note WHERE uId = :uId")
    List<Note> getAllNote(String uId);

    @Query("SELECT * FROM note WHERE uId = :uId AND dateRemind IS NULL AND pin = :isPin AND active =:isActive")
    List<Note> getAllNotRemindNote(String uId, boolean isPin, boolean isActive);

    @Query("SELECT * FROM note WHERE dateRemind >= :date AND uId = :uId AND pin= :isPin AND active =:isActive")
    List<Note> getAllUpcomingNote(Timestamp date, boolean isPin, String uId, boolean isActive);

    @Query("SELECT * FROM note WHERE dateRemind >= :firstDay AND dateRemind<=:lastDay AND uId = :uId AND active =:isActive")
    List<Note> getAllNoteByDay(Timestamp firstDay, Timestamp lastDay, String uId, boolean isActive);

    @Query("SELECT * FROM note WHERE dateRemind >= :firstDay AND dateRemind <= :lastDay AND pin = :isPin AND uId = :uId AND active =:isActive")
    List<Note> getAllPinByDay(Timestamp firstDay, Timestamp lastDay, boolean isPin, String uId, boolean isActive);

    @Query("SELECT * FROM note WHERE pin = :isPin AND uId = :uId AND active =:isActive")
    List<Note> getAllPin(boolean isPin, String uId, boolean isActive);

    @Query("SELECT * FROM note WHERE uId = :uId AND active =:isActive ORDER BY dateCreate DESC LIMIT 1 ")
    Note getLatestNote(String uId, boolean isActive);

    @Query("SELECT * FROM note WHERE uId = :uId AND id = :nId")
    Note getSelectedNote(String uId, String nId);

    @Query("SELECT * FROM note WHERE uId = :uId AND active =:isActive AND title LIKE '%' || :note  || '%' OR content LIKE '%' || :note  || '%'")
    List<Note> searchNote(String uId, String note, boolean isActive);

    @Query("SELECT * FROM note WHERE uId = :uId AND active = :isActive")
    List<Note> getNoteBin (String uId, boolean isActive);

    @Query("SELECT * FROM note WHERE uId = :uId ORDER BY dateCreate DESC LIMIT 1")
    Note getLastestNoteVer2(String uId);
}

