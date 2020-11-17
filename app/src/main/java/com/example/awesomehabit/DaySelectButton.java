package com.example.awesomehabit;

public class DaySelectButton {
    private String mDay;
    private String mDayOfWeek;
    private String mMonth;

    public DaySelectButton(String mDay, String mDayOfWeek,String mMonth) {
        this.mDay = mDay;
        this.mDayOfWeek = mDayOfWeek;
        this.mMonth=mMonth;
    }

    public String getmDay() {
        return mDay;
    }

    public String getmDayOfWeek() {
        return mDayOfWeek;
    }

    public void setmDay(String mDay) {
        this.mDay = mDay;
    }

    public void setmDayOfWeek(String mDayOfWeek) {
        this.mDayOfWeek = mDayOfWeek;
    }

    public String getmMonth() {
        return mMonth;
    }

    public void setmMonth(String mMonth) {
        this.mMonth = mMonth;
    }
}
