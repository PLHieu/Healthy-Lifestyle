package com.example.awesomehabit.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface GoalDao {
    @Update
    void update(Goal goal);
    @Insert
    void insert(Goal goal);
    @Query("SELECT target from goal where type=:t limit 1")
    int getTarget(int t);
}
