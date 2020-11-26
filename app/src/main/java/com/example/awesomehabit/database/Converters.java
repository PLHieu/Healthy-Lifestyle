package com.example.awesomehabit.database;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class Converters {
    @TypeConverter
    public static Calendar fromTimestamp(Long timeStamp) {
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(timeStamp);
        return c;
    }

    @TypeConverter
    public static Long fromCalendar(Calendar calendar) {
        return calendar.getTimeInMillis();
    }

}