package com.example.awesomehabit.statistic;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.awesomehabit.R;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class StatisticActivity extends AppCompatActivity {
    private static final String TAG = "StatisticActivity";
    private ArrayList<ArrayList<Float>> arrayListData;
    private ArrayList<ArrayList<String>> arrayListShortDay;
    private ArrayList<ArrayList<Calendar>> arrayListLongDay;

    private ArrayList<Long> listTime = new ArrayList<>();
    private ArrayList<Float> listData = new ArrayList<>();

    private int statisticType = 0;

    SnapHelper snapHelper = new PagerSnapHelper();

    public static float WEEK_MODE = 7f;
    public static float MONTH_MODE = 31f;
    public static float YEAR_MODE = 12f;
    public static String RUN_LABEL = "Quãng đường chạy";
    public static String WATER_LABEL = "Lượng nước uống";
    public static String RUN_UNIT = "km";
    public static String WATER_UNIT = "ly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        handleIntent();
        initSpinner();
    }

    void initArrayList(){
        arrayListData = new ArrayList<>();
        arrayListShortDay = new ArrayList<>();
        arrayListLongDay = new ArrayList<>();
    }

    private void initSpinner() {
        Spinner spinner = findViewById(R.id.dropdownList);
        final ArrayList<String> temp = new ArrayList<>(Arrays.asList("Tuần", "Tháng", "Năm"));

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, temp);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initArrayList();
                handleView(position);
                initRecyclerViewForMePlease(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleView(int mode) {
        for (int i = 0; i < listTime.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(listTime.get(i));

            if (!checkExisted(calendar, listData.get(i), mode))
                generateIfNotExisted(calendar, listData.get(i), mode);
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();
        listTime = (ArrayList<Long>) intent.getSerializableExtra("listTime");
        listData = (ArrayList<Float>) intent.getSerializableExtra("listData");
        statisticType = intent.getIntExtra("statisticType", 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateIfNotExisted(Calendar calendar, Float data, int position) {
        ArrayList<Float> aListdata = new ArrayList<>();
        ArrayList<String> aListShortDay = new ArrayList<>();
        ArrayList<Calendar> aListLongDay = new ArrayList<>();

        if (position == 0)
            generateWeek(calendar, data, aListdata, aListShortDay, aListLongDay);
        else if(position == 1)
            generateMonth(calendar, data, aListdata, aListShortDay, aListLongDay);
        else generateYear(calendar, data, aListdata, aListShortDay, aListLongDay);

        arrayListData.add(aListdata);
        arrayListLongDay.add(aListLongDay);
        arrayListShortDay.add(aListShortDay);
    }

    private void generateYear(Calendar calendar, Float data, ArrayList<Float> listData, ArrayList<String> listShortDay, ArrayList<Calendar> listLongDay) {
        long milli = calendar.getTimeInMillis();
        for (int j = 0; j < YEAR_MODE; j++) {
            Calendar temp = Calendar.getInstance();
            temp.setTimeInMillis(milli);
            temp.set(Calendar.MONTH, j);
            listShortDay.add(String.valueOf(j + 1));
            listLongDay.add(temp);
            if (temp.getTime().getMonth() == calendar.getTime().getMonth())
                listData.add(data);
            else listData.add(0f);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateMonth(Calendar calendar, Float data, ArrayList<Float> listData, ArrayList<String> listShortDay, ArrayList<Calendar> listLongDay) {
        long milli = calendar.getTimeInMillis();
        for (int j = 0; j < YearMonth.of(calendar.getTime().getYear(), calendar.getTime().getMonth() + 1).lengthOfMonth(); j++) {
            Calendar temp = Calendar.getInstance();
            temp.setTimeInMillis(milli);
            temp.set(Calendar.DAY_OF_MONTH, temp.getActualMinimum(Calendar.DAY_OF_MONTH));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
            temp.add(Calendar.DAY_OF_YEAR, j);
            listShortDay.add(simpleDateFormat.format(temp.getTime()));
            listLongDay.add(temp);
            if (temp.getTimeInMillis() == milli)
                listData.add(data);
            else listData.add(0f);
        }
    }

    private void generateWeek(Calendar calendar, float data, ArrayList<Float> listData, ArrayList<String> listShortDay, ArrayList<Calendar> listLongDay) {
        long milli = calendar.getTimeInMillis();
        for (int j = 0; j < WEEK_MODE; j++) {
            Calendar temp = Calendar.getInstance();
            temp.setTimeInMillis(milli);
            temp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
            temp.add(Calendar.DAY_OF_YEAR, j);
            listShortDay.add(simpleDateFormat.format(temp.getTime()));
            listLongDay.add(temp);
            if (temp.getTimeInMillis() == milli)
                listData.add(data);
            else listData.add(0f);
        }
    }

    boolean compareTime(Calendar calendar1, Calendar calendar2, int mode) {
        if (calendar1.getTime().getDate() == calendar2.getTime().getDate() && calendar1.getTime().getMonth() == calendar2.getTime().getMonth() && calendar1.getTime().getYear() == calendar2.getTime().getYear())
            return true;

        if (mode == 2) {
            Log.d(TAG, "compareTime1: " + String.valueOf(calendar1.getTime().getMonth()) + String.valueOf(calendar1.getTime().getYear()));
            Log.d(TAG, "compareTime2: " + String.valueOf(calendar2.getTime().getMonth()) + String.valueOf(calendar1.getTime().getYear()));
            if (calendar1.getTime().getYear() == calendar2.getTime().getYear() && calendar1.getTime().getMonth() == calendar2.getTime().getMonth()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkExisted(Calendar calendar, float data, int mode) {
        for (int i = 0; i < arrayListLongDay.size(); i++) {
            for (int j = 0; j < arrayListLongDay.get(i).size(); j++)
                if (compareTime(arrayListLongDay.get(i).get(j), calendar, mode)) {
                    Calendar calendar1 = arrayListLongDay.get(i).get(j);
                    Calendar calendar2 = calendar;

                    if (mode != 2)
                        arrayListData.get(i).set(j, data);
                    else
                        arrayListData.get(i).set(j, arrayListData.get(i).get(j) + data);
                    return true;
                }
        }
        return false;
    }

    private void initRecyclerViewForMePlease(int mode) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false );
        RecyclerView recyclerView = findViewById(R.id.recyclerViewDetail);
        recyclerView.setLayoutManager(layoutManager);

        MyStatisticViewAdapter myStatisticViewAdapter;
        if (mode == 0)
            myStatisticViewAdapter = new MyStatisticViewAdapter(this, arrayListData, arrayListShortDay, arrayListLongDay, WEEK_MODE, statisticType);
        else if (mode == 1)
            myStatisticViewAdapter = new MyStatisticViewAdapter(this, arrayListData, arrayListShortDay, arrayListLongDay, MONTH_MODE, statisticType);
        else myStatisticViewAdapter = new MyStatisticViewAdapter(this, arrayListData, arrayListShortDay, arrayListLongDay, YEAR_MODE, statisticType);

        recyclerView.setAdapter(myStatisticViewAdapter);
        snapHelper.attachToRecyclerView(recyclerView);
    }
}