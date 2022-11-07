package com.example.prm_392_nho_ho_ban.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.prm_392_nho_ho_ban.bean.User;

import java.util.List;

@Dao
public interface RoomUserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Delete
    void delete(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM user")
    List<User> getAllUser();

}
