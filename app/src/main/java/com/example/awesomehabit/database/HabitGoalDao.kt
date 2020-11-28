package com.example.awesomehabit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitGoalDao {
    @Insert
    fun insert(habitGoal: HabitGoal)

    @Update
    fun update(habitGoal: HabitGoal)

    @Query("SELECT habit_goal FROM habit_goal WHERE type = :type")
    fun getGoal(type: Int): Int?
}