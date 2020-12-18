package com.example.awesomehabit.database.custom;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.awesomehabit.database.Converters;

import java.util.Calendar;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = CustomHabit.class,parentColumns = "HabitID",childColumns = "HabitID",onDelete = CASCADE)
,primaryKeys = {"HabitID","time"})
public class DailyCustomHabit {
    public int HabitID;
    public int current; //So hien tai da dat duoc
    public int target; //So can dat duoc
    @TypeConverters(Converters.class)
    @NonNull
    public Calendar time;

    public DailyCustomHabit(int HabitID, int current, int target, @NonNull Calendar time) {
        this.HabitID = HabitID;
        this.current = current;
        this.target = target;
        this.time = time;
    }
}
