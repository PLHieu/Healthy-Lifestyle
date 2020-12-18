package com.example.awesomehabit.database.custom;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Calendar;

@Dao
public interface DailyCustomHabitDao {
    @Insert
    void insert(DailyCustomHabit dailyCustomHabit);
    @Update
    void update(DailyCustomHabit dailyCustomHabit);
    @Query("select * from dailycustomhabit where HabitID=:id and time=:time limit 1")
    LiveData<DailyCustomHabit> getHabit(int id, Calendar time);//Lay habit vs ID vao ngay time
}
