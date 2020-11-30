package com.example.awesomehabit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.PagerAdapter;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.database.sleeping.SleepNight;
import com.example.awesomehabit.database.water.Water;
import com.example.awesomehabit.running.demo;
import com.example.awesomehabit.sleeping.SleepTracker;
import com.example.awesomehabit.statistic.StatisticActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomPageAdapter extends PagerAdapter implements View.OnClickListener {
    private Context mContext;
    private  List<SleepNight> sleepNightList;
    private int waterIntake=0;
    AppDatabase db;
    List<Integer> goals;

    public CustomPageAdapter(Context mContext,AppDatabase db) {
        this.mContext = mContext;
        this.db=db;
    }
    @Override
    public int getCount() {
        return CustomCalendarView.NUMBER_OF_DAY_BUTTONS;
    }
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        goals=new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.card_list, collection, false);

        Button btnRun=(Button)layout.findViewById(R.id.startRunning);
        Button btnSleep=(Button)layout.findViewById(R.id.startSleeping);
        Button btnMeal=(Button)layout.findViewById(R.id.btnMeal);
        TextView distance=layout.findViewById(R.id.runDistance);
        TextView sleepTime=layout.findViewById(R.id.sleepTime);
        TextView tvWater=layout.findViewById(R.id.tvWater);
        Button btnAddWater=layout.findViewById(R.id.btn_waterAdd);
        Button btnMinusWater=layout.findViewById(R.id.btn_waterMinus);



        Calendar pageDay=Calendar.getInstance();
        pageDay.add(Calendar.DATE,position-CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
        Log.d("gay", String.valueOf(position-CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2));
        pageDay.set(Calendar.HOUR,0);
        pageDay.set(Calendar.MINUTE,0);
        pageDay.set(Calendar.SECOND,0);
        pageDay.set(Calendar.MILLISECOND,0);

        goals=db.goalDao().getTargets().getValue();
        if(goals==null){
            goals=new ArrayList<>();
            goals.add(99);
            goals.add(69);
            goals.add(12);
        }
        db.sleepDao().getHabitFrom(pageDay).observe((AppCompatActivity)mContext, new Observer<List<SleepNight>>() {
            @Override
            public void onChanged(List<SleepNight> sleepNights) {
                sleepNightList=sleepNights;
                if (sleepNightList!=null && sleepNightList.size()>0){
                    int totalSleepDuration=0;
                    for (int i=0;i<sleepNights.size();i++){
                        totalSleepDuration+=sleepNights.get(i).getSleepDuration();
                    }
                    sleepTime.setText(String.valueOf(totalSleepDuration)+" / "+goals.get(2));
                }
                else{
                    sleepTime.setText("0:00 / "+goals.get(2));
                }

            }
        });

        db.waterDao().getHabitFrom(pageDay).observe((AppCompatActivity) mContext, new Observer<Water>() {
            @Override
            public void onChanged(Water water) {
                //waterIntake=water.inTake;
                if(water!=null)
                {
                    tvWater.setText(String.valueOf(water.inTake)+" / "+goals.get(1));
                    if (water.inTake>0)
                        btnMinusWater.setVisibility(View.VISIBLE);
                    else
                        btnMinusWater.setVisibility(View.INVISIBLE);
                }
                else
                {
                    btnMinusWater.setVisibility(View.INVISIBLE);
                }

            }
        });

        db.goalDao().getTargets().observe((AppCompatActivity) mContext, new Observer<List<Integer>>() {
            @Override
            public void onChanged(List<Integer> targets) {
                if(targets!=null){
                    goals=targets;
                }
            }
        });

        btnRun.setOnClickListener(this);
        btnSleep.setOnClickListener(this);
        btnMeal.setOnClickListener(this);
        btnAddWater.setOnClickListener(this);
        btnMinusWater.setOnClickListener(this);
        ((Button)layout.findViewById(R.id.btnRunStatistic)).setOnClickListener(this);
        ((Button)layout.findViewById(R.id.btnSleepStatistic)).setOnClickListener(this);
        collection.addView(layout);
        return layout;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.startRunning: {
                Intent intent = new Intent(mContext, demo.class);
                mContext.startActivity(intent);
                break;
            }
            case R.id.startSleeping: {
                Intent intent = new Intent(mContext, SleepTracker.class);
                mContext.startActivity(intent);
                break;
            }
            case R.id.btnMeal:{
                //Intent intent = new Intent(mContext, MealActivity.class);
                //mContext.startActivity(intent);
                Toast.makeText(mContext,"This feature is coming soon!",Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.btnRunStatistic:
            {
                Intent intent = new Intent(mContext, StatisticActivity.class);
                intent.putExtra("statisticType", StatisticActivity.RUN_TYPE);
                mContext.startActivity(intent);
                break;
            }
            case R.id.btnSleepStatistic:
            {
                Intent intent = new Intent(mContext, StatisticActivity.class);
                intent.putExtra("statisticType", StatisticActivity.SLEEP_TYPE);
                mContext.startActivity(intent);
                break;
            }
            case R.id.btnWaterStatistic: {
                Intent intent = new Intent(mContext, StatisticActivity.class);
                intent.putExtra("statisticType", StatisticActivity.WATER_TYPE);
                mContext.startActivity(intent);
                break;
            }
            case R.id.btn_waterAdd:{
                LiveData<Water> waters= db.waterDao().getHabitFrom(CustomCalendarView.currentDay);
                Water water=waters.getValue();
                if(water!=null)
                {
                    water.inTake++;
                    db.waterDao().update(water);
                }
                else{
                    Water w=new Water(Habit.TYPE_COUNT,CustomCalendarView.currentDay,1);
                    db.waterDao().insert(w);
                }
                break;
            }
            case R.id.btn_waterMinus:{
                LiveData<Water> waters= db.waterDao().getHabitFrom(CustomCalendarView.currentDay);
                Water water=waters.getValue();
                if(water!=null)
                {
                    if(water.inTake>0)
                    {
                        water.inTake--;
                        db.waterDao().update(water);
                    }
                }
                else{
                    Water w=new Water(Habit.TYPE_COUNT,CustomCalendarView.currentDay,0);
                    db.waterDao().insert(w);
                }
                break;
            }
        }
    }
}
