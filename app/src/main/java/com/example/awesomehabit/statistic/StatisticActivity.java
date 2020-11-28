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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.running.Run;
import com.example.awesomehabit.database.sleeping.SleepDatabaseDao;
import com.example.awesomehabit.database.sleeping.SleepNight;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class StatisticActivity extends AppCompatActivity {
    private static final String TAG = "StatisticActivity";
    private ArrayList<ArrayList<Float>> arrayListData;
    private ArrayList<ArrayList<String>> arrayListShortDay;
    private ArrayList<ArrayList<Calendar>> arrayListLongDay;
    private ArrayList<ArrayList<Long>> arrayListTimeLength;
    private ArrayList<ArrayList<Integer>> arrayListSleepQuality;

    private ArrayList<Long> listTime = new ArrayList<>();
    private ArrayList<Float> listData = new ArrayList<>();
    private ArrayList<Integer> listSleepQuality = new ArrayList<>();
    private ArrayList<Long> listTimeLength = new ArrayList<>();

    private int statisticType = 0;

    SnapHelper snapHelper = new PagerSnapHelper();

    public static float WEEK_MODE = 7f;
    public static float MONTH_MODE = 31f;
    public static float YEAR_MODE = 12f;

    public static String RUN_LABEL = "Quãng đường chạy";
    public static String WATER_LABEL = "Lượng nước uống";
    public static final String SLEEP_LABEL = "Số tiếng ngủ";

    public static String RUN_UNIT = "km";
    public static String WATER_UNIT = "ly";
    public static String SLEEP_UNIT = "tiếng";

    public static int RUN_TYPE = 0;
    public static int SLEEP_TYPE = 1;
    public static int WATER_TYPE = 2;

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
        arrayListSleepQuality = new ArrayList<>();
        arrayListTimeLength = new ArrayList<>();
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

            if (!checkExisted(calendar, listData.get(i), listTimeLength.get(i), listSleepQuality.get(i), mode))
                generateIfNotExisted(calendar, listData.get(i), listTimeLength.get(i), listSleepQuality.get(i), mode);
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();
        listTime = new ArrayList<>();
        listData = new ArrayList<>();
        listTimeLength = new ArrayList<>();
        listSleepQuality = new ArrayList<>();

        statisticType = intent.getIntExtra("statisticType", 0);

        if (statisticType == RUN_TYPE)
            getRunData();
        else if (statisticType == SLEEP_TYPE)
            getSleepData();
        else if (statisticType == WATER_TYPE)
            getWaterData();

    }

    private void getWaterData() {
        //to be continued
    }

    private void getSleepData() {
        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        List<SleepNight> nights = database.sleepDao().getAllNights().getValue();

        for (int i = 0; i < nights.size(); i++) {
            listData.add((float) nights.get(i).getEndTimeMilli() - nights.get(i).getStartTimeMilli());
            listTime.add(nights.get(i).getStartTimeMilli());
            listSleepQuality.add(nights.get(i).getSleepQuality());
            listTimeLength.add(nights.get(i).getEndTimeMilli() - nights.get(i).getStartTimeMilli());
        }
    }

    private void getRunData() {
        List<Run> temp = AppDatabase.getDatabase(getApplicationContext()).runDao().getAllRun();

        for (int i = 0; i < temp.size(); i++) {
            listTime.add(Long.parseLong(temp.get(i).timeStart));
            listTimeLength.add(temp.get(i).runningTime);
            listData.add((float) temp.get(i).distance);
            listSleepQuality.add(0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateIfNotExisted(Calendar calendar, Float data, Long timeLength, Integer sleepQuality, int position) {
        ArrayList<Float> aListdata = new ArrayList<>();
        ArrayList<String> aListShortDay = new ArrayList<>();
        ArrayList<Calendar> aListLongDay = new ArrayList<>();
        ArrayList<Long> aListTimeLength = new ArrayList<>();
        ArrayList<Integer> aListSleepQuality = new ArrayList<>();

        if (position == 0)
            generateWeek(calendar, data, timeLength, sleepQuality, aListdata, aListShortDay, aListLongDay, aListTimeLength, aListSleepQuality, statisticType);
        else if(position == 1)
            generateMonth(calendar, data, timeLength, sleepQuality, aListdata, aListShortDay, aListLongDay, aListTimeLength, aListSleepQuality, statisticType);
        else generateYear(calendar, data, timeLength, sleepQuality, aListdata, aListShortDay, aListLongDay, aListTimeLength, aListSleepQuality, statisticType);

        arrayListData.add(aListdata);
        arrayListLongDay.add(aListLongDay);
        arrayListShortDay.add(aListShortDay);
    }

    private void generateYear(Calendar calendar, Float data, Long timeLength, Integer sleepQuality, ArrayList<Float> listData, ArrayList<String> listShortDay, ArrayList<Calendar> listLongDay, ArrayList<Long> aListTimeLength, ArrayList<Integer> aListSleepQuality, int statisticType) {
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
            aListSleepQuality.add(sleepQuality);
            aListTimeLength.add(timeLength);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateMonth(Calendar calendar, Float data, Long timeLength, Integer sleepQuality, ArrayList<Float> listData, ArrayList<String> listShortDay, ArrayList<Calendar> listLongDay, ArrayList<Long> aListTimeLength, ArrayList<Integer> aListSleepQuality, int statisticType) {
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
            aListSleepQuality.add(sleepQuality);
            aListTimeLength.add(timeLength);
        }
    }

    private void generateWeek(Calendar calendar, Float data, Long timeLength, Integer sleepQuality, ArrayList<Float> listData, ArrayList<String> listShortDay, ArrayList<Calendar> listLongDay, ArrayList<Long> aListTimeLength, ArrayList<Integer> aListSleepQuality, int statisticType) {
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
            aListSleepQuality.add(sleepQuality);
            aListTimeLength.add(timeLength);
        }
    }

    boolean compareTime(Calendar calendar1, Calendar calendar2, int mode) {
        if (calendar1.getTime().getDate() == calendar2.getTime().getDate() && calendar1.getTime().getMonth() == calendar2.getTime().getMonth() && calendar1.getTime().getYear() == calendar2.getTime().getYear())
            return true;

        if (mode == 2) {
            if (calendar1.getTime().getYear() == calendar2.getTime().getYear() && calendar1.getTime().getMonth() == calendar2.getTime().getMonth()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkExisted(Calendar calendar, Float data, Long timeLength, Integer sleepQuality, int mode) {
        for (int i = 0; i < arrayListLongDay.size(); i++) {
            for (int j = 0; j < arrayListLongDay.get(i).size(); j++)
                if (compareTime(arrayListLongDay.get(i).get(j), calendar, mode)) {
                    arrayListData.get(i).set(j, arrayListData.get(i).get(j) + data);
                    arrayListTimeLength.get(i).set(j, arrayListTimeLength.get(i).get(j) + timeLength);
                    arrayListSleepQuality.get(i).set(j, (arrayListSleepQuality.get(i).get(j) + sleepQuality) / 2);
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
            myStatisticViewAdapter = new MyStatisticViewAdapter(this, arrayListData, arrayListTimeLength, arrayListSleepQuality, arrayListShortDay, arrayListLongDay, WEEK_MODE, statisticType);
        else if (mode == 1)
            myStatisticViewAdapter = new MyStatisticViewAdapter(this, arrayListData, arrayListTimeLength, arrayListSleepQuality, arrayListShortDay, arrayListLongDay, MONTH_MODE, statisticType);
        else myStatisticViewAdapter = new MyStatisticViewAdapter(this, arrayListData, arrayListTimeLength, arrayListSleepQuality, arrayListShortDay, arrayListLongDay, YEAR_MODE, statisticType);

        recyclerView.setAdapter(myStatisticViewAdapter);
        snapHelper.attachToRecyclerView(recyclerView);
    }
}