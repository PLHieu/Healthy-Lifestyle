package com.example.awesomehabit.database.sleeping

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.awesomehabit.database.Habit
import java.util.*

@Entity(tableName = "daily_sleep_quality_table")
class SleepNight(day: Int, month: Int, year: Int) : Habit(TYPE_SLEEP, day, month, year) {
//    @PrimaryKey(autoGenerate = true)
//    var id = 0;

    constructor(day:Int,month:Int,year:Int, startTimeMilli: Long, endTimeMilli: Long, sleepQuality: Int) : this(day, month, year) {
        this.startTimeMilli = startTimeMilli
        this.endTimeMilli = endTimeMilli
        this.sleepQuality = sleepQuality
    }

    @ColumnInfo(name = "start_time_milli")
    var startTimeMilli = System.currentTimeMillis()

    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli = startTimeMilli

    @ColumnInfo(name = "quality_rating")
    var sleepQuality = -1

    @ColumnInfo(name = "updated")
    var updated:Int = 0

    fun getSleepDuration(): Long { return endTimeMilli.minus(startTimeMilli) }
}