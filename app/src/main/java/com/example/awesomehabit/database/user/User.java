package com.example.awesomehabit.database.user;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String address;
    public String avatarBitmap;

    public User(String name, String address, String avatarBitmap) {
        this.name = name;
        this.address = address;
        this.avatarBitmap = avatarBitmap;
    }


}
