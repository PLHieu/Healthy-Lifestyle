package com.example.awesomehabit.database.meal;
import androidx.room.Entity;

import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.meal.Meal;

import java.util.ArrayList;
import java.util.Calendar;

@Entity
public class DailyMeal extends Habit {
    public DailyMeal(int day,int month, int year) {
        super(Habit.TYPE_MEAL,day,month,year);
    }
    public ArrayList<Meal> mealList;
}
