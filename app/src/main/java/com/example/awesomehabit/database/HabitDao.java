package com.example.awesomehabit.database;
import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.List;

public interface HabitDao {
    //LiveData<List<Habit>> getAll();
    int getType();
    List<?> getHabitFrom(Calendar calendar);
}