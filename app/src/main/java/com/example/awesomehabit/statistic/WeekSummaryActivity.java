package com.example.awesomehabit.statistic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.running.Run;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class WeekSummaryActivity extends AppCompatActivity {
    ArrayList<Long> listRunDateInMilli = new ArrayList<>();
    ArrayList<Float> listRunDistance = new ArrayList<>();
    ArrayList<Long> listRunTimeLength= new ArrayList<>();
    int qualifiedRunDay = 0;

    ArrayList<Float> listWaterAmount = new ArrayList<>();
    ArrayList<Long> listWaterDateInMilli = new ArrayList<>();
    int qualifiedWaterDay = 0;

    BarChart barChart;
    TextView textView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_summary);

        getQualifiedRunDay();
        getQualifiedWaterDay();

        generateBarChart();
        setContextForRunDetail();

        getData();
    }

    private void setContextForRunDetail() {
        ((Button)findViewById(R.id.btnRunDetail)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRunIntent();
            }
        });

        ((Button)findViewById(R.id.btnWaterDetail)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendWaterIntent();
            }
        });
    }

    private void sendWaterIntent() {
        Intent intent = new Intent(this, StatisticActivity.class);
        intent.putExtra("listTime", listWaterDateInMilli);
        intent.putExtra("listData", listWaterAmount);
        intent.putExtra("statisticType", 1);
        startActivity(intent);
    }

    private void generateBarChart() {
        barChart = findViewById(R.id.barChartWeekSummary);
        barChart.getDescription().setText("");
        setContextForBarChart();
        barChart.animateY(2000);
        customizeXAxis();
        customizeYAxis();
        barChart.setScaleEnabled(false);
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos = (int)e.getX();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void customizeYAxis() {
        YAxis y = barChart.getAxisLeft();
        y.setStartAtZero(true);
        y.setAxisMaxValue(7.5f);

        y =barChart.getAxisRight();
        y.setStartAtZero(true);
        y.setAxisMaxValue(7.5f);
    }

    private void customizeXAxis() {
        ArrayList<String> title = new ArrayList<>(Arrays.asList("Chạy", "Uống nước"));
        XAxis x = barChart.getXAxis();
        x.setDrawGridLines(false);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setValueFormatter(new IndexAxisValueFormatter(title));
        x.setLabelCount(title.size());
        x.setTextSize(10);
        x.setDrawLabels(true);
    }

    private void setContextForBarChart() {
        ArrayList<BarEntry> data = new ArrayList<>();
        data.add(new BarEntry(0,qualifiedRunDay));
        data.add(new BarEntry(1, qualifiedWaterDay));

        BarDataSet barDataSet = new BarDataSet(data, "Số ngày hoàn thành chỉ tiêu");
        barDataSet.setColors(ColorTemplate.rgb("#039dfc"));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.4f);
        barChart.setFitBars(true);
        barChart.setData(barData);
    }

    private void getQualifiedWaterDay() {
        qualifiedWaterDay = 4;
    }

    private void getQualifiedRunDay() {
        getRunData();
        float runData = 0;
        long runTime = 0;
        for (int i = 0; i < listRunDateInMilli.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(listRunDateInMilli.get(i));
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(listRunDateInMilli.get(i));
            if (calendar1.get(Calendar.WEEK_OF_YEAR) == calendar.get(Calendar.WEEK_OF_YEAR) && calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)) {
                runData += listRunDistance.get(i);
                runTime += listRunTimeLength.get(i);
            }
        }
        setContextForRunTextview(runData, runTime);
        qualifiedRunDay = 6;
    }

    private void setContextForRunTextview(float runData, long runTime) {
        TextView textView1 = findViewById(R.id.textViewRunDataSum);
        textView1.setText(runData + " km");
        textView1 = findViewById(R.id.textViewRunTimeSum);
        long hour = runTime / 1000 / 60 / 60;
        long minute = runTime / 1000 / 60 % 60;
        long second = runTime / 1000 % 60;
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
        textView1.setText(temp);
    }

    private void sendRunIntent() {
        Intent intent = new Intent(this, StatisticActivity.class);
        intent.putExtra("listTime", listRunDateInMilli);
        intent.putExtra("listData", listRunDistance);
        startActivity(intent);
    }

    private void getData() {
        generateRunData();
        generateWaterData();
    }

    private void generateWaterData() {

    }

    private void generateRunData() {
        listRunDateInMilli.add(Calendar.getInstance().getTimeInMillis());
        listRunDistance.add(10f);

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, new Random().nextInt(7));
            listRunDateInMilli.add(calendar.getTimeInMillis());

            listRunDistance.add((float) (new Random().nextInt(10) + 5));
        }
    }

    private void getRunData() {
        List<Run> temp = AppDatabase.getDatabase(getApplicationContext()).runDao().getAllRun();

        for (int i = 0; i < temp.size(); i++) {
            listRunDateInMilli.add(Long.parseLong(temp.get(i).timeStart));
            listRunDistance.add((float) temp.get(i).distance);
            listRunTimeLength.add(temp.get(i).runningTime);
        }
    }

}