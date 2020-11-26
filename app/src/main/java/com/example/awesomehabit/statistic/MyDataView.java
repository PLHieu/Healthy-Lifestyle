package com.example.awesomehabit.statistic;

import java.util.Calendar;

public class MyDataView {
    public Calendar date;
    public Float data;
    public Float mode;
    public int type;
    public long timeLength = 0;
    public int icon_id = 0;
    public MyDataView(Calendar date, Float data, Float mode, int type) {
        this.date = date;
        this.data = data;
        this.mode = mode;
        this.type = type;
    }
    public MyDataView(Calendar date, Float data, Float mode, int type, Long timeLength, int icon_id) {
        this.date = date;
        this.data = data;
        this.mode = mode;
        this.type = type;
        this.timeLength = timeLength;
        this.icon_id = icon_id;
    }
    public MyDataView(Calendar date, Float data, Float mode, int type, int timeLength) {
        this.date = date;
        this.data = data;
        this.mode = mode;
        this.type = type;
        this.timeLength = timeLength;
    }

}
