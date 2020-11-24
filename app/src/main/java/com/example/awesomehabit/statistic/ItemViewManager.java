package com.example.awesomehabit.statistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.awesomehabit.R;

import java.util.Calendar;

public class ItemViewManager {

    private static final String TAG = "ItemViewManager";
    public static View createItemView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.my_run_view, null);
    }


    public static void bindPlease(Context context, MyDataView item, View convertView) {
        TextView textView = (TextView) convertView.findViewById(R.id.textViewDay);
        String date ="";
        if (item.mode != StatisticActivity.YEAR_MODE)
            date = "Ngày " + String.valueOf(item.date.get(Calendar.DATE) + " ");
        String month = "Tháng " + String.valueOf(item.date.get(Calendar.MONTH) + 1);
        String year = " Năm " + String.valueOf(item.date.get(Calendar.YEAR));

        textView.setText(date + month + year);

        textView = (TextView) convertView.findViewById(R.id.textViewDistance);
        textView.setText(getText(item));
    }

    private static String getText(MyDataView item) {
        if (item.type == 0)
            return String.valueOf(item.data) + " " + StatisticActivity.RUN_UNIT;
        else
            return String.valueOf((int) (float) item.data) + " " + StatisticActivity.WATER_UNIT;
    }
}
