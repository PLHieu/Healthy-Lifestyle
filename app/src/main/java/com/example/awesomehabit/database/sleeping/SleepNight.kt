package com.example.awesomehabit.database.sleeping

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.awesomehabit.database.Habit
import java.util.*

@Entity(tableName = "daily_sleep_quality_table")
class SleepNight() : Habit(TYPE_SLEEP) {
//    @PrimaryKey(autoGenerate = true)
//    var id = 0;

    constructor(day:Int,month:Int,year:Int, startTimeMilli: Long, endTimeMilli: Long, sleepQuality: Int) : this() {
        this.startTimeMilli = startTimeMilli
        this.endTimeMilli = endTimeMilli
        this.sleepQuality = sleepQuality
        this.day=day
        this.month=month
        this.year=year
    }

    @ColumnInfo(name = "start_time_milli")
    var startTimeMilli = System.currentTimeMillis()

    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli = startTimeMilli

    @ColumnInfo(name = "quality_rating")
    var sleepQuality = -1

    fun getSleepDuration(): Long { return endTimeMilli.minus(startTimeMilli) }
}