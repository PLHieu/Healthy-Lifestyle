package com.example.awesomehabit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.statistic.StatisticActivity;
import com.example.awesomehabit.statistic.WeekSummaryActivity;
import com.mapbox.mapboxsdk.Mapbox;

public class MainActivity extends AppCompatActivity implements CustomCalendarView.CustomCalendarViewInterface {
    CustomCalendarView customCalendarView;
    ActionBar actionBar;
    ViewPager viewPager;
    AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main);

        actionBar=getSupportActionBar();
        actionBar.setElevation(0);

        customCalendarView=(CustomCalendarView) findViewById(R.id.customCalendar);
        customCalendarView.setResponder(this);//For onclick

        db = AppDatabase.getDatabase(this);
        viewPager=(ViewPager)findViewById(R.id.pager);
        CustomPageAdapter customPageAdapter=new CustomPageAdapter(this,db);
        viewPager.setAdapter(customPageAdapter);

        viewPager.setCurrentItem(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
        viewPager.setPageTransformer(true,new DepthPageTransformer());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
               customCalendarView.smoothScrollTo(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDaySelected(int position) {
        viewPager.setCurrentItem(position, true);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_statistic:
                //startActivity(new Intent(this, MainActivity2.class));
                //customCalendarView.smoothScrollTo(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
                //viewPager.setCurrentItem(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
                //startActivity(new Intent(this, WeekSummaryActivity.class));
                return true;
            case R.id.actionSetGoal:
                startActivity(new Intent(this, SetGoal.class));
        }

        return super.onOptionsItemSelected(item);
    }
}