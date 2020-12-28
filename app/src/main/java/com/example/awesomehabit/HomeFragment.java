package com.example.awesomehabit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.statistic.StatisticActivity;
import com.example.awesomehabit.statistic.WeekSummaryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.zip.Inflater;

public class HomeFragment extends Fragment implements CustomCalendarView.CustomCalendarViewInterface {
    CustomCalendarView customCalendarView;
    ViewPager viewPager;
    AppDatabase db;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
//
//        setContentView(R.layout.activity_main);
//
//        actionBar=getSupportActionBar();
//        actionBar.setElevation(0);
//
//        customCalendarView=(CustomCalendarView) findViewById(R.id.customCalendar);
//        customCalendarView.setResponder(this);//For onclick
//
//        db = AppDatabase.getDatabase(this);
//        viewPager=(ViewPager)findViewById(R.id.pager);
//        CustomPageAdapter customPageAdapter=new CustomPageAdapter(this,db);
//        viewPager.setAdapter(customPageAdapter);
//
//        viewPager.setCurrentItem(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
//        viewPager.setPageTransformer(true,new DepthPageTransformer());
//
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//               customCalendarView.smoothScrollTo(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        FloatingActionButton fab = findViewById(R.id.fabAddHabit);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setActionForFAB();
//            }
//        });
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        customCalendarView=(CustomCalendarView) view.findViewById(R.id.customCalendar);
        customCalendarView.setResponder(this);//For onclick

        db = AppDatabase.getDatabase(view.getContext());
        viewPager=(ViewPager)view.findViewById(R.id.pager);
        CustomPageAdapter customPageAdapter=new CustomPageAdapter(view.getContext(), db);
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

        FloatingActionButton fab = view.findViewById(R.id.fabAddHabit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActionForFAB();
            }
        });
        return view;
    }

    void setActionForFAB(){
        Intent intent = new Intent(getActivity(), AddHabitActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDaySelected(int position) {
        viewPager.setCurrentItem(position, true);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_statistic:
//                //startActivity(new Intent(this, MainActivity2.class));
//                //customCalendarView.smoothScrollTo(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
//                //viewPager.setCurrentItem(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
//                //startActivity(new Intent(this, WeekSummaryActivity.class));
//                return true;
//            case R.id.actionSetGoal:
////                startActivity(new Intent(this, SetGoal.class));
//                break;
            case R.id.actionGoToToday:
                //Nhảy tới ngày hôm nay
                customCalendarView.smoothScrollTo(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
                viewPager.setCurrentItem(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2,true);
        }

        return super.onOptionsItemSelected(item);
    }
}