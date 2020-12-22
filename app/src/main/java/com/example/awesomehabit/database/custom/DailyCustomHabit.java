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
,primaryKeys = {"HabitID","day","month","year"})
public class DailyCustomHabit {
    public int HabitID;
    public int current; //So hien tai da dat duoc
    public int target; //So can dat duoc

    int day;
    int month;
    int year;
    public Calendar time=Calendar.getInstance();//Please remove me
    public DailyCustomHabit(int HabitID, int current, int target,int day,int month,int year) {
        this.HabitID = HabitID;
        this.current = current;
        this.target = target;
        this.day=day;
        this.month=month;
        this.year=year;
    }
}
