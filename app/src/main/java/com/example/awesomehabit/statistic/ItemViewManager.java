package com.example.awesomehabit.statistic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.awesomehabit.R;

import org.w3c.dom.Text;

import java.util.Calendar;

public class ItemViewManager {

    private static final String TAG = "ItemViewManager";
    public static View createItemView(Context context, MyDataView item) {
        if (item.type == StatisticActivity.RUN_TYPE)
            return LayoutInflater.from(context).inflate(R.layout.my_run_view, null);
        else if (item.type == StatisticActivity.SLEEP_TYPE)
            return LayoutInflater.from(context).inflate(R.layout.my_sleep_view, null);
        return LayoutInflater.from(context).inflate(R.layout.my_run_view, null);
    }


    public static void bindPlease(Context context, MyDataView item, View convertView) {
        setTextViewDate(item, convertView);

        if (item.type != StatisticActivity.WATER_TYPE)
            setTextViewTime(item, convertView);

        if (item.type == StatisticActivity.SLEEP_TYPE)
            setSleepIconAndQuality(context, item, convertView);

        if (item.type == StatisticActivity.RUN_TYPE) {
            TextView textView = (TextView) convertView.findViewById(R.id.textViewDistance);
            textView.setText(getText(item));
        }

    }

    private static void setSleepIconAndQuality(Context context, MyDataView item, View convertView) {
        if (item.timeLength > 0) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imgViewSleepQualityIcon);
            imageView.setImageResource(item.icon_id);
        }
    }

    private static void setTextViewDate(MyDataView item, View convertView) {
        TextView textView = (TextView) convertView.findViewById(R.id.textViewDay);
        String date ="";
        if (item.mode != StatisticActivity.YEAR_MODE)
            date = "Ngày " + item.date.get(Calendar.DATE) + " ";
        String month = "Tháng " + (item.date.get(Calendar.MONTH) + 1);
        String year = " Năm " + item.date.get(Calendar.YEAR);
        textView.setText(date + month + year);
    }

    private static void setTextViewTime(MyDataView item, View convertView) {
        long timeInMilli = item.timeLength;
        TextView textView = convertView.findViewById(R.id.textViewTime);
        long hour = timeInMilli / 1000 / 60 / 60;
        long minute = timeInMilli / 1000 / 60 % 60;
        long second = timeInMilli / 1000 % 60;
        String temp = "";
        if (hour < 10)
            temp += "0";
        temp += hour + ":";
        if (minute < 10)
            temp += "0";
        temp += minute + ":";
        if (second < 10)
            temp += "0";
        temp += String.valueOf(second);
        textView.setText(temp);
    }

    private static String getText(MyDataView item) {
        if (item.type == 0)
            return item.data + " " + StatisticActivity.RUN_UNIT;
        else if (item.type == 2)
            return (int) (float) item.data + " " + StatisticActivity.SLEEP_UNIT;
        else
            return (int) (float) item.data + " " + StatisticActivity.WATER_UNIT;
    }
}
