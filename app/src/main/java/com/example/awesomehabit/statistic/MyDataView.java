package com.example.awesomehabit.statistic;

import com.example.awesomehabit.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MyDataView {
    public Calendar date;
    public Float data;
    public Float mode;
    public int type;
    public long timeLength = 0;
    public int icon_id = 0;

    private ArrayList<Integer> listSleepID = new ArrayList<>(Arrays.asList(
            R.drawable.ic_sleep_0,
            R.drawable.ic_sleep_1,
            R.drawable.ic_sleep_2,
            R.drawable.ic_sleep_3,
            R.drawable.ic_sleep_4,
            R.drawable.ic_sleep_5));

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
        this.icon_id = listSleepID.get(icon_id);
    }
    public MyDataView(Calendar date, Float data, Float mode, int type, int timeLength) {
        this.date = date;
        this.data = data;
        this.mode = mode;
        this.type = type;
        this.timeLength = timeLength;
    }

}
