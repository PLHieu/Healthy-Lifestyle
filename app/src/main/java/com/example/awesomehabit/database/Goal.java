package com.example.awesomehabit.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Goal {
    @PrimaryKey(autoGenerate = true)
    int id;

    public Goal(int type, int target) {
        this.type = type;
        this.target = target;
    }

    int type;
    int target;
}
