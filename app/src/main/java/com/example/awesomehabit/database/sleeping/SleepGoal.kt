package com.example.awesomehabit.database.sleeping

import android.content.Context
import com.example.awesomehabit.database.AppDatabase
import com.example.awesomehabit.database.Habit
import com.example.awesomehabit.database.HabitGoal

class SleepGoal private constructor(context: Context) {
    private val habitGoalDao = AppDatabase.getDatabase(context).habitGoalDao()
    private var sleepGoalInMinutes: Int =
            if (habitGoalDao.getGoal(Habit.TYPE_SLEEP) == null) {
                habitGoalDao.insert(HabitGoal(Habit.TYPE_SLEEP, 8*60))
                habitGoalDao.getGoal(Habit.TYPE_SLEEP)!!
            } else {
                habitGoalDao.getGoal(Habit.TYPE_SLEEP)!!
            }

    fun setSleepGoal(hour: Int, minute: Int) : Boolean {
        if (hour < 0 || minute < 0 || hour >= 24 || minute >= 60 || (hour == 0 && minute == 0))
            return false
        sleepGoalInMinutes = hour * 60 + minute
        habitGoalDao.update(HabitGoal(Habit.TYPE_SLEEP, sleepGoalInMinutes))
        return true
    }

    fun getSleepGoalInMillis(): Long {
        return sleepGoalInMinutes.toLong() * 60 * 100
    }

    override fun toString(): String {
        return "%d:%02d".format(sleepGoalInMinutes / 60, sleepGoalInMinutes % 60)
    }

    companion object {
        private var INSTANCE: SleepGoal? = null

        fun getInstance(context: Context): SleepGoal {
            if (INSTANCE == null) {
                INSTANCE = SleepGoal(context)
            }
            return INSTANCE!!
        }
    }
}