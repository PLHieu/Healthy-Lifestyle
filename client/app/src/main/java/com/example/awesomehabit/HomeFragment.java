package com.example.awesomehabit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.doctor.AddHabitActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment implements CustomCalendarView.CustomCalendarViewInterface {
    CustomCalendarView customCalendarView;
    ViewPager viewPager;
    LinearLayout userBar;
    AppDatabase db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        customCalendarView=(CustomCalendarView) view.findViewById(R.id.customCalendar);
        customCalendarView.setResponder(this);//For onclick

        userBar=(LinearLayout)view.findViewById(R.id.userBar);

        ViewGroup.LayoutParams params = userBar.getLayoutParams();
        params.height = 0;
        params.width = 0;
        userBar.setLayoutParams(params);


        db = AppDatabase.getDatabase(view.getContext());
        viewPager=(ViewPager)view.findViewById(R.id.pager);
        CustomPageAdapter customPageAdapter=new CustomPageAdapter(view.getContext(), db,false);
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
                //Nh???y t???i ng??y h??m nay
                customCalendarView.smoothScrollTo(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
                viewPager.setCurrentItem(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2,true);
        }

        return super.onOptionsItemSelected(item);
    }
}