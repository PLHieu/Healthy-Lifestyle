package com.example.awesomehabit.statistic;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awesomehabit.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class MainActivity2 extends AppCompatActivity {
    private long[]arrayTime;
    private float[]arrayDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        generateData();
    }

    private void generateData() {
        ArrayList<Long> listTime = new ArrayList<>();
        ArrayList<Float> listDistance = new ArrayList<>();
        listTime.add(Calendar.getInstance().getTimeInMillis());
        listDistance.add(10f);

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, new Random().nextInt(7));
            listTime.add(calendar.getTimeInMillis());

            listDistance.add((float) (new Random().nextInt(10) + 5));
        }
        Intent intent = new Intent(this, StatisticActivity.class);
        intent.putExtra("listTime", listTime);
        intent.putExtra("listData", listDistance);
        startActivity(intent);
    }

}