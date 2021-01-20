package com.example.awesomehabit.database.meal;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.example.awesomehabit.database.Converters;
import com.example.awesomehabit.database.HabitDao;

import java.util.Calendar;
import java.util.List;

@Dao
public interface DailyMealDao extends HabitDao {
    @Insert()
    void insert(DailyMeal dailyMeal);
    @Delete()
    void delete(DailyMeal dailyMeal);
    @Update()
    void update(DailyMeal dailyMeal);
    @Query("select type from dailymeal limit 1")
    int getType();

    @TypeConverters(Converters.class)
    @Query("select * from dailymeal where day=:day and month=:month and year=:year limit 1")
    LiveData<DailyMeal> getHabitFrom(int day, int month, int year);

    @Query("select * from dailymeal")
    List<DailyMeal> getAllDailyMeal();

    @Query("DELETE FROM dailymeal")
    public void deleteTable();

}
