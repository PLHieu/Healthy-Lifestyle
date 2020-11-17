package com.example.awesomehabit.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HabitDao {
    @Query("SELECT * FROM Habit")
    LiveData<List<Habit>> getAll();
    @Insert
    void insert(Habit... habits);
}
