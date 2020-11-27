package com.example.awesomehabit.sleeping

class SleepGoal private constructor() {
    private var sleepGoalInMinutes: Int = 8*60 // In minutes

    fun setSleepGoal(hour: Int, minute: Int) {
        if (hour < 0 || minute < 0)
            return
        sleepGoalInMinutes = (hour % 24) * 60 + (minute % 60)
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