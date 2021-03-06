package com.example.awesomehabit.database.custom;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CustomHabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CustomHabit customHabit);

    @Query("select * from customhabit")
    LiveData<List<CustomHabit>> getAll();

    @Query("select * from customhabit")
    List<CustomHabit> getAllNone();

    @Query("select customhabit.*,dailycustomhabit.* from customhabit left join dailycustomhabit " +
            "ON customhabit.HabitID = dailycustomhabit.HabitID_ and day=:day and month=:month and year=:year")
    LiveData<List<CustomHabit_DailyCustomHabit>> getAllCustomHabitFromDate(int day,int month,int year);

    class CustomHabit_DailyCustomHabit{
        @Embedded
        public CustomHabit customHabit_;
        @Embedded
        public DailyCustomHabit dailyCustomHabit_;
    }

    @Query("delete from customhabit")
    public void  deleteTable();

    @Query("select * from customhabit where CHupdated = 0")
    public List<CustomHabit>  getOutdated();

    @Query("update customhabit set CHupdated =1 where CHupdated = 0")
    public void updateAll();
}
