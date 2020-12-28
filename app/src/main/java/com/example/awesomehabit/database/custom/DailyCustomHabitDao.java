package com.example.awesomehabit.database.custom;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Calendar;
import java.util.List;

@Dao
public interface DailyCustomHabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailyCustomHabit dailyCustomHabit);
    @Update
    void update(DailyCustomHabit dailyCustomHabit);
    @Query("select * from dailycustomhabit where HabitID_=:id and day=:day and month=:month and year=:year limit 1")
    LiveData<DailyCustomHabit> getHabit(int id, int day, int month, int year);//Lay habit vs ID vao ngay time

    @Query("select * from dailycustomhabit ")
    List<DailyCustomHabit> getAllHabitNone();

}
