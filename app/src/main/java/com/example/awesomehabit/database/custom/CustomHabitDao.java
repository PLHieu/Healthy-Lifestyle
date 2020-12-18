package com.example.awesomehabit.database.custom;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CustomHabitDao {
    @Insert
    void insert(CustomHabit customHabit);
    @Query("select * from customhabit")
    LiveData<List<CustomHabit>> getAll();
}
