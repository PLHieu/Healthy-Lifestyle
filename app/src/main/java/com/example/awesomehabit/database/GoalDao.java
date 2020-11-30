package com.example.awesomehabit.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GoalDao {
    @Update
    void update(Goal goal);
    @Insert
    void insert(Goal goal);
    @Query("SELECT target from goal")
    LiveData<List<Integer>> getTargets();

    @Query("SELECT target from goal where type=:t limit 1")
    int getTarget(int t);
}
