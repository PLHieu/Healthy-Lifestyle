package com.example.awesomehabit.statistic;

import java.util.Calendar;

public class MyDataView {
    public Calendar date;
    public Float data;
    public Float mode;
    public int type;
    public MyDataView(Calendar date, Float data, Float mode, int type) {
        this.date = date;
        this.data = data;
        this.mode = mode;
        this.type = type;
    }
}
