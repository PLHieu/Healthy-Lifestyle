package com.example.awesomehabit.database.running;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.example.awesomehabit.database.Converters;
import com.example.awesomehabit.database.HabitDao;

import java.util.Calendar;
import java.util.List;

@Dao
public interface RunDao extends HabitDao {
    @Query("SELECT * FROM running_table")
    List<Run> getAllRun();

//    @Query("SELECT * FROM running_table WHERE WEEK(timeStart)== WEEK(:startDayofWeek)")
//    List<Run> getAllWeekRun(Date startDayofWeek);
//
//    @Query("SELECT * FROM running_table WHERE DAY(timeStart) == DAY(:day)")
//    List<Run> getAllDayRun(Date day);
//
//    @Query("SELECT * FROM running_table WHERE MONTH(timeStart) == MONTH(:startDayofMonth)")
//    List<Run> getAllMonthRun(Date startDayofMonth);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRun(Run run);

    @Update
    void update(Run run);

    @Query("SELECT Distance FROM running_table")
    List<Integer> getAllDistance();

    @Query("SELECT timeStart FROM running_table")
    List<String> getAllTimeStart();

    @Query("SELECT runningTime FROM running_table")
    List<Long> getAllRunningtime();

    @Delete
    void delete(Run run);

    @Query("SELECT type from running_table limit 1")
    int getType();

    @TypeConverters(Converters.class)
    @Query("SELECT * from running_table where day=:day and month=:month and year=:year")
    LiveData<List<Run>> getHabitFrom(int day,int month,int year);

    @Query("SELECT * FROM running_table where id = '1'")
    Run gettestRun();

    @Query("DELETE FROM running_table")
    public void deleteTable();
}
