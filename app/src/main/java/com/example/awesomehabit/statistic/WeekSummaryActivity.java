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
import java.util.Random;

public class WeekSummaryActivity extends AppCompatActivity {
    ArrayList<Long> listRunDateInMilli = new ArrayList<>();
    ArrayList<Float> listRunDistance = new ArrayList<>();
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
                sendWaterIntent();
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
        qualifiedRunDay = 6;
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

}