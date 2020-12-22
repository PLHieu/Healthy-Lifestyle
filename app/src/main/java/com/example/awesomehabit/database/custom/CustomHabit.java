package com.example.awesomehabit.database.custom;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CustomHabit {
    final public  static int TYPE_COUNT=0;
    final public static int TYPE_TIME=1;
    final public static int TYPE_TICK=2;

    @PrimaryKey(autoGenerate = true)
    public int HabitID;
    public String name;
    public int type;

    public CustomHabit(String name,int type) {
        this.name = name;
        this.type = type;
    }
    //Bitmap
}
