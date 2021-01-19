package com.example.awesomehabit.database.user;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert()
    void insert(User person);
    @Delete()
    void delete(User person);
    @Update()
    void update(User person);
    @Query("select * from User")
    LiveData<List<User>> getAllPerson();
}
