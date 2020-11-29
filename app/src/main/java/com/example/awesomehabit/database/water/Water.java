package com.example.awesomehabit.database.water;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.awesomehabit.database.Habit;

import java.util.Calendar;

@Entity(tableName = "daily_water_table")
public class Water extends Habit {
    public Water(int type, Calendar time,int inTake) {
        super(type);
        this.time=time;
        this.inTake=inTake;
    }
    @ColumnInfo(name = "inTake")
    public int inTake;
}
