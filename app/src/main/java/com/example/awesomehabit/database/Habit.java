package com.example.awesomehabit.database;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.util.Calendar;

public class Habit {
    public static int RUN_AVAILABLE = 0;
    public static int SLEEP_AVAILABLE = 1;
    public static int MEAL_AVAILABLE = 0;

    @ColumnInfo(name = "isVisible")
    public int isVisible;

    public Habit(int type) {//Auto today
//        this.id = id;
        this.type = type;
        //this.time = Calendar.getInstance();
        //time.set(Calendar.HOUR, 12);
        //time.set(Calendar.MINUTE, 0);
        //time.set(Calendar.SECOND, 0);
        //time.set(Calendar.MILLISECOND, 0);
    }
    public Habit(int type,int day,int month,int year) {//Get time passed in
//        this.id = id;
        this.type = type;
        this.day=day;
        this.month=month;
        this.year=year;
        //this.time = time;
       switch (type){
           case TYPE_RUN:
               this.isVisible = RUN_AVAILABLE;
               break;
           case TYPE_SLEEP:
               this.isVisible = SLEEP_AVAILABLE;
               break;
           case TYPE_MEAL:
               this.isVisible = MEAL_AVAILABLE;
               break;
       }
    }

    public static final int TYPE_RUN = 0;
    public static final int TYPE_SLEEP = 1;
    public static final int TYPE_COUNT = 2;
    public static final int TYPE_MEAL = 3;

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "type")
    public int type;

    //@ColumnInfo(name = "time")
    //@TypeConverters(Converters.class)
    //public Calendar time;
    public int day;
    public int month;
    public int year;
    @ColumnInfo(name="target")
    public int target=0;

}