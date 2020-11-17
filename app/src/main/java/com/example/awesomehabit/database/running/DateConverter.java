package com.example.awesomehabit.database.running;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(String daystring) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        return daystring == null ? null: simpleDateFormat.parse(daystring);
    }

    @TypeConverter
    public static String fromDate(Date date){
        return date == null ? null : new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(date);
    }
}
