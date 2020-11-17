package com.example.awesomehabit.database.running;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RunDao {
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

    @Delete
    void delete(Run run);
}
