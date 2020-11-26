package com.example.awesomehabit.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Calendar;

@Entity
public class Habit {
    public Habit(int type) {
//        this.id = id;
        this.type=type;
        this.time = Calendar.getInstance();
        time.set(Calendar.HOUR, 0);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
    }

    public static final int TYPE_RUN=0;
    public static final int TYPE_SLEEP=1;
    public static final int TYPE_COUNT=2;

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name="type")
    public int type;

    @ColumnInfo(name="time")
    @TypeConverters(Converters.class)
    public Calendar time;
}