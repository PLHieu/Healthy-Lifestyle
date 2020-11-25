package com.example.awesomehabit.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity
public abstract class Habit {
    public Habit(int id,int type) {
        this.id = id;
        this.type=type;
    }

    public static final int TYPE_RUN=0;
    public static final int TYPE_SLEEP=1;
    public static final int TYPE_COUNT=2;

    @PrimaryKey
    public int id;

    @ColumnInfo(name="type")
    int type;

    @ColumnInfo(name="time")
    public Calendar time;
}