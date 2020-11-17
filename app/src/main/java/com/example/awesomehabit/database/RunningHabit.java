package com.example.awesomehabit.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Habit.class,
        parentColumns = "id",
        childColumns = "id",
        onDelete = ForeignKey.CASCADE))
public class RunningHabit {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name="distance")
    private int distance;
    @ColumnInfo(name="goal")
    private int goal;
    @ColumnInfo(name="date")
    private String date;
}
