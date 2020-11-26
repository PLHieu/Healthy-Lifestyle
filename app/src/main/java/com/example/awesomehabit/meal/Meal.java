package com.example.awesomehabit.meal;

import android.graphics.Bitmap;

public class Meal {
    //Bitmap image;
    String name;
    int calories;

    public Meal( String name, int calories) {
        this.name = name;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }
}
