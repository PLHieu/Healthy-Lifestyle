package com.example.awesomehabit.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Habit {
    public Habit(int id, String habitName) {
        this.id = id;
        this.habitName = habitName;
    }

    @PrimaryKey
    public int id;

    @ColumnInfo(name="icon")
    public int bitmapID;

    @ColumnInfo(name="habit_name")
    public String habitName;

    @ColumnInfo(name="creation_date")//ngay tao
    public String creation_date;

    @ColumnInfo(name="repeat")//so ngay lap lai
    public int repeat;
}