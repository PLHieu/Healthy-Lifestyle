package com.example.awesomehabit.database;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.util.Calendar;

public class Habit {
    public Habit(int type) {//Auto today
//        this.id = id;
        this.type = type;
        this.time = Calendar.getInstance();
        time.set(Calendar.HOUR, 12);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
    }
    public Habit(int type,Calendar time) {//Get time passed in
//        this.id = id;
        this.type = type;
        this.time = time;
    }

    public static final int TYPE_RUN = 0;
    public static final int TYPE_SLEEP = 1;
    public static final int TYPE_COUNT = 2;
    public static final int TYPE_MEAL = 3;

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "type")
    public int type;

    @ColumnInfo(name = "time")
    @TypeConverters(Converters.class)
    public Calendar time;

    @ColumnInfo(name="target")
    public int target=0;
}