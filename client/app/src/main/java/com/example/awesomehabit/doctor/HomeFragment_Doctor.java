package com.example.awesomehabit.doctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.awesomehabit.CustomCalendarView;
import com.example.awesomehabit.CustomPageAdapter;
import com.example.awesomehabit.DepthPageTransformer;
import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment_Doctor extends Fragment implements CustomCalendarView.CustomCalendarViewInterface {
    CustomCalendarView customCalendarView;
    ViewPager viewPager;
    TextView txtName;
    AppDatabase db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        customCalendarView=(CustomCalendarView) view.findViewById(R.id.customCalendar);
        customCalendarView.setResponder(this);//For onclick

        txtName=view.findViewById(R.id.userBarName);
        SharedPreferences preferences;
        preferences = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
        if(preferences.getString("name", null) != null)
        {
            txtName.setText(preferences.getString("name", "guest"));
        }

        db = AppDatabase.getDatabase(view.getContext());
        viewPager=(ViewPager)view.findViewById(R.id.pager);
        CustomPageAdapter customPageAdapter=new CustomPageAdapter(view.getContext(), db,true);
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
        fab.setVisibility(View.VISIBLE);
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