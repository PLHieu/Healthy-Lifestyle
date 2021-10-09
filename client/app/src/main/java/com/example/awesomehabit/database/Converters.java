package com.example.awesomehabit.database;

import androidx.room.TypeConverter;

import com.example.awesomehabit.meal.Meal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
public class Converters {
    @TypeConverter
    public static Calendar fromTimestamp(Long timeStamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeStamp);
        return c;
    }

    @TypeConverter
    public static Long fromCalendar(Calendar calendar) {
        return calendar.getTimeInMillis();
    }

    @TypeConverter
    public static ArrayList<Meal> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Meal>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Meal> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}