package com.example.awesomehabit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.Goal;
import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.database.custom.CustomHabitDao;
import com.example.awesomehabit.database.custom.DailyCustomHabit;
import com.example.awesomehabit.database.meal.DailyMeal;
import com.example.awesomehabit.database.running.Run;
import com.example.awesomehabit.database.sleeping.SleepNight;
import com.example.awesomehabit.meal.MealActivity;
import com.example.awesomehabit.running.RunningTracking;
import com.example.awesomehabit.sleeping.SleepTracker;
import com.example.awesomehabit.statistic.StatisticActivity;
import java.util.Calendar;
import java.util.List;

public class CustomPageAdapter extends PagerAdapter implements View.OnClickListener {
    private Context mContext;
    AppDatabase db;
    Boolean doctorMode;
    public CustomPageAdapter(Context mContext,AppDatabase db,Boolean doctormode) {
        this.mContext = mContext;
        this.db=db;
        this.doctorMode=doctormode;
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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.card_list, collection, false);

        RecyclerView recyclerView=layout.findViewById(R.id.rvCards);
        CardAdapter cardAdapter=new CardAdapter(doctorMode);
        cardAdapter.setmContext(mContext);
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        Calendar pageDay=Calendar.getInstance();
        pageDay.add(Calendar.DATE,position-CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
        int day=pageDay.get(Calendar.DAY_OF_MONTH);
        int month=pageDay.get(Calendar.MONTH);
        int year=pageDay.get(Calendar.YEAR);



        db.runDao().getHabitFrom(day,month,year).observe((AppCompatActivity) mContext, new Observer<List<Run>>() {
            @Override
            public void onChanged(List<Run> runList) {
                int totalRunDistance=0;
                int isVisible = Habit.RUN_AVAILABLE;
                if (runList != null && runList.size()>0){
                    for(int i=0;i<runList.size();i++){
                        totalRunDistance+=runList.get(i).distance;
                    }
                }
                //distance.setText(String.valueOf((float)totalRunDistance/1000)+"/");
                cardAdapter.updateRun(totalRunDistance);
                cardAdapter.updateRunVisible(isVisible);
            }
        });
        db.sleepDao().getHabitFrom(day,month,year).observe((AppCompatActivity)mContext, new Observer<List<SleepNight>>() {
            @Override
            public void onChanged(List<SleepNight> sleepNights) {
                int totalSleepDuration=0;
                int isVisible = Habit.SLEEP_AVAILABLE;
                if (sleepNights!=null && sleepNights.size()>0){
                    for (int i=0;i<sleepNights.size();i++){
                        totalSleepDuration+=sleepNights.get(i).getSleepDuration();
                    }
                }
                else{
                }
                cardAdapter.updateSleep(totalSleepDuration);
                cardAdapter.updateSleepVisible(isVisible);
            }
        });
        db.dailyMealDao().getHabitFrom(day,month,year).observe((AppCompatActivity) mContext, new Observer<DailyMeal>() {
            @Override
            public void onChanged(DailyMeal dailyMeal) {
                int isVisible = Habit.MEAL_AVAILABLE;

                int totalcalo=0;
                if(dailyMeal!=null){


                for(int i=0;i<dailyMeal.mealList.size();i++){
                    totalcalo+=dailyMeal.mealList.get(i).getCalories();
                }
                }
                cardAdapter.updateMeal(totalcalo);
                cardAdapter.updateMealVisible(isVisible);
            }
        });

        db.customHabitDao().getAllCustomHabitFromDate(day,month,year).observe((AppCompatActivity) mContext, new Observer<List<CustomHabitDao.CustomHabit_DailyCustomHabit>>() {
            @Override
            public void onChanged(List<CustomHabitDao.CustomHabit_DailyCustomHabit> customHabit_dailyCustomHabits) {
                cardAdapter.setHabitPairs(customHabit_dailyCustomHabits);
                Log.d("@@@",String.valueOf(customHabit_dailyCustomHabits.size()));
            }
        });

        if(layout.findViewById(R.id.btnMeal)!=null){
            layout.findViewById(R.id.btnMeal).setVisibility(View.GONE);
        }

        db.goalDao().getGoalLive(0).observe((AppCompatActivity) mContext, new Observer<Goal>() {
            @Override
            public void onChanged(Goal goal) {
                cardAdapter.setDistanceGoal(String.valueOf(goal.target));
            }
        });

        db.goalDao().getGoalLive(1).observe((AppCompatActivity) mContext, new Observer<Goal>() {
            @Override
            public void onChanged(Goal goal) {
                cardAdapter.setSleepGoal(String.valueOf(goal.target));
            }
        });
        db.goalDao().getGoalLive(2).observe((AppCompatActivity) mContext, new Observer<Goal>() {
            @Override
            public void onChanged(Goal goal) {
                cardAdapter.setMealGoal(String.valueOf(goal.target));
            }
        });

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
                Intent intent = new Intent(mContext, RunningTracking.class);
                mContext.startActivity(intent);
                break;
            }
            case R.id.startSleeping: {
                Intent intent = new Intent(mContext, SleepTracker.class);
                mContext.startActivity(intent);
                break;
            }
            case R.id.btnMeal:{
                Intent intent = new Intent(mContext, MealActivity.class);
                mContext.startActivity(intent);
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
            case R.id.btnCountStatistic: {
                Intent intent = new Intent(mContext, StatisticActivity.class);
                intent.putExtra("statisticType", StatisticActivity.COUNT_TYPE);
                mContext.startActivity(intent);
                break;
            }
            case R.id.btn_countAdd: {
                LiveData<DailyCustomHabit> LDdailyCustom=db.dailyCustomHabitDao().getHabit(1,CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
                Observer observer=new Observer<DailyCustomHabit>() {
                    @Override
                    public void onChanged(DailyCustomHabit dailyCustom) {
                        LDdailyCustom.removeObserver(this);
                        if(dailyCustom!=null){
                            dailyCustom.current++;
                            db.dailyCustomHabitDao().update(dailyCustom);
                        }
                        else{
                            DailyCustomHabit dailyCustomHabit=new DailyCustomHabit(1,1,10,CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
                            db.dailyCustomHabitDao().insert(dailyCustomHabit);
                        }
                    }
                };
                LDdailyCustom.observe((AppCompatActivity)mContext, observer);
            }
            break;
            case R.id.btn_countMinus:{
                LiveData<DailyCustomHabit> LDdailyCustom=db.dailyCustomHabitDao().getHabit(1,CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
                Observer observer=new Observer<DailyCustomHabit>() {
                    @Override
                    public void onChanged(DailyCustomHabit dailyCustom) {
                        LDdailyCustom.removeObserver(this);
                        if(dailyCustom!=null){
                            dailyCustom.current--;
                            db.dailyCustomHabitDao().update(dailyCustom);
                        }
                        else{
                            DailyCustomHabit dailyCustomHabit=new DailyCustomHabit(1,0,10,CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
                            db.dailyCustomHabitDao().insert(dailyCustomHabit);
                        }
                    }
                };
                LDdailyCustom.observe((AppCompatActivity)mContext, observer);
            }

        }
    }
}
