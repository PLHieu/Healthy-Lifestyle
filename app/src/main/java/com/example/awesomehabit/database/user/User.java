package com.example.awesomehabit.database.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    @ColumnInfo(name="username")
    @NonNull
    public String username;

    @ColumnInfo(name="email")
    public String email;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="tuoi")
    public Integer tuoi;

    @ColumnInfo(name="diachi")
    public String diachi ;

    @ColumnInfo(name="profile_pic")
    public String profile_pic;

    @ColumnInfo(name="gioitinh")
    public Integer gioitinh;

    @ColumnInfo(name="ngaysinh")
    public String ngaysinh;

    public User(String username, String email , String name, Integer tuoi, String diachi, String profile_pic, Integer gioitinh, String ngaysinh) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.tuoi = tuoi;
        this.diachi = diachi;
        this.profile_pic = profile_pic;
        this.gioitinh = gioitinh;
        this.ngaysinh = ngaysinh;
    }







}
