package com.example.awesomehabit.database;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Query;

import java.util.Calendar;
import java.util.List;

public interface HabitDao {
    //LiveData<List<Habit>> getAll();
    int getType();
    LiveData<?> getHabitFrom(Calendar calendar);
    LiveData<?> getLastestHabit();
}