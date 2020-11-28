package com.example.awesomehabit.sleeping

class SleepGoal private constructor() {
    private var sleepGoalInMinutes: Int = 8*60 // In minutes

    fun setSleepGoal(hour: Int, minute: Int) : Boolean {
        if (hour < 0 || minute < 0 || hour >= 24 || minute >= 60 || (hour == 0 && minute == 0))
            return false
        sleepGoalInMinutes = hour * 60 + minute
        return true
    }

    fun getSleepGoalInMillis(): Long {
        return sleepGoalInMinutes.toLong() * 6000
    }

    override fun toString(): String {
        return "%d:%02d".format(sleepGoalInMinutes / 60, sleepGoalInMinutes % 60)
    }

    companion object {
        private var INSTANCE: SleepGoal? = null

        fun getInstance(): SleepGoal {
            if (INSTANCE == null) {
                INSTANCE = SleepGoal()
            }
            return INSTANCE!!
        }
    }
}