package com.example.awesomehabit.database.sleeping
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.awesomehabit.database.Converters
import com.example.awesomehabit.database.HabitDao
import com.example.awesomehabit.database.running.Run
import java.util.*

@Dao
interface SleepDatabaseDao : HabitDao {

    @Insert
    fun insert(night: SleepNight)

    @Update
    fun update(night: SleepNight)

    @Query("SELECT * from daily_sleep_quality_table WHERE id = :key")
    fun get(key: Long): SleepNight?

    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY id DESC LIMIT 15")
    fun get15RecentNights(): LiveData<List<SleepNight>>

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY id DESC")
    fun getAllNights(): LiveData<List<SleepNight>>

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY id DESC")
    fun getAllNightsNonLive(): List<SleepNight>

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY id DESC LIMIT 1")
    fun getTonight(): SleepNight?

    @Query("SELECT type FROM daily_sleep_quality_table ORDER BY id DESC LIMIT 1")
    override fun getType(): Int

    @TypeConverters(Converters::class)
    @Query("SELECT * from daily_sleep_quality_table where day=:day and month=:month and year=:year limit 1")
    override fun getHabitFrom(day: Int, month: Int, year: Int): LiveData<List<SleepNight>>;

}