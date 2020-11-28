package com.example.awesomehabit.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_goal")
class HabitGoal(@PrimaryKey val type: Int, @ColumnInfo(name = "habit_goal") var habit_goal: Int = 0)