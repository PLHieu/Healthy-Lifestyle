package com.example.awesomehabit.database.water;

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
public interface WaterDao extends HabitDao {

    @Override
    @Query("SELECT type from daily_water_table limit 1")
    int getType();

    @TypeConverters(Converters.class)
    @Query("SELECT * from daily_water_table where time = :calendar")
    LiveData<Water> getHabitFrom(Calendar calendar);
    @Query("SELECT * FROM DAILY_WATER_TABLE")
    List<Water> getAllWater();
    @Insert
    void insert(Water water);
    @Delete
    void delete(Water water);
    @Update
    void update(Water water);
}
