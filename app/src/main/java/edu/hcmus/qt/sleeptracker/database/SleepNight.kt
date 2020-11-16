package edu.hcmus.qt.sleeptracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_sleep_quality_table")
class SleepNight {
    @PrimaryKey(autoGenerate = true)
    var nightId = 0L

    @ColumnInfo(name = "start_time_milli")
    var startTimeMilli = System.currentTimeMillis()

    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli = startTimeMilli

    @ColumnInfo(name = "quality_rating")
    var sleepQuality = -1
}