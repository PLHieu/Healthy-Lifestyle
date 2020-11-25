package com.example.awesomehabit.database;
import java.util.Calendar;
public interface HabitDao {
    //LiveData<List<Habit>> getAll();
    int getType();
    Habit getHabitFrom(Calendar calendar);
}