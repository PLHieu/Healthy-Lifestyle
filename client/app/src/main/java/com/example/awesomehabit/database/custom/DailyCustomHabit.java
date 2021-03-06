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

@Entity(foreignKeys = @ForeignKey(entity = CustomHabit.class,parentColumns = "HabitID",childColumns = "HabitID_",onDelete = CASCADE)
,primaryKeys = {"HabitID_","day","month","year"})
public class DailyCustomHabit {
    public int HabitID_;
    public int current; //So hien tai da dat duoc
    public int target; //So can dat duoc

    int day;
    int month;
    int year;
//    public Calendar time=Calendar.getInstance();//Please remove me
    int updated;

    public DailyCustomHabit(int HabitID_, int current, int target,int day,int month,int year) {
        this.HabitID_ = HabitID_;
        this.current = current;
        this.target = target;
        this.day=day;
        this.month=month;
        this.year=year;
        this.updated = 0;
    }
}
