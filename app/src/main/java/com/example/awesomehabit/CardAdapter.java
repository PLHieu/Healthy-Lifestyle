package com.example.awesomehabit;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.database.custom.CustomHabit;
import com.example.awesomehabit.database.custom.CustomHabitDao;
import com.example.awesomehabit.database.custom.DailyCustomHabit;
import com.example.awesomehabit.meal.MealActivity;
import com.example.awesomehabit.running.RunningTracking;
import com.example.awesomehabit.sleeping.SleepTracker;
import com.example.awesomehabit.statistic.StatisticActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    public CardAdapter(Boolean hideButton) {
        this.hideButton = hideButton;
    }

    public CardAdapter() {
    }

    public class viewCardType {
        public final static int RUN=0;
        public final static int SLEEP=1;
        public final static int FOOD=2;
        public final static int COUNT=3;
        public final static int TICK=4;
        public final static int TIME=5;
    }

    String totalDistanceString ="no data";
    String totalSleepdurationString ="no data";
    Context mContext;
    Boolean hideButton=false;
    int runVisible = 1;
    int foodVisible = 1;
    int sleepVisible = 1;

    AppDatabase db;

    public void setHabitPairs(List<CustomHabitDao.CustomHabit_DailyCustomHabit> habit_pairs) {
        this.habit_pairs = habit_pairs;
        notifyDataSetChanged();
    }

    List<CustomHabitDao.CustomHabit_DailyCustomHabit> habit_pairs;

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: " + String.valueOf(viewType));
        switch (viewType){
            case viewCardType.RUN: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.running_card, parent, false);
                return new RunViewHolder(view);
            }
            case viewCardType.SLEEP: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sleeping_card, parent, false);
                return new SleepViewHolder(view);
            }
            case viewCardType.FOOD: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_card, parent, false);
                return new FoodViewHolder(view);
            }
            case viewCardType.COUNT: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.counting_card, parent, false);
                return new CountViewHolder(view);
            }
            case viewCardType.TICK:{
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticking_card, parent, false);
                return new TickViewHolder(view);
            }
            case viewCardType.TIME:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timing_card, parent, false);
                return new TimeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int staticPos = runVisible + foodVisible + sleepVisible;
        Log.d(TAG, "onBindViewHolder: " + String.valueOf(position));
        switch (holder.getItemViewType()){
            case viewCardType.RUN:
                RunViewHolder runViewHolder=(RunViewHolder)holder;
                runViewHolder.distance.setText(totalDistanceString);
                runViewHolder.distanceGoal.setText("99.99km");
                if(hideButton){
                    runViewHolder.btnRunStat.setVisibility(View.GONE);
                    runViewHolder.btnRun.setVisibility(View.GONE);
                }
            break;
            case viewCardType.SLEEP:
                SleepViewHolder sleepViewHolder=(SleepViewHolder)holder;
                sleepViewHolder.sleepTime.setText(totalSleepdurationString);
                if(hideButton)
                {
                    sleepViewHolder.btnSleep.setVisibility(View.GONE);
                    sleepViewHolder.btnSleepStat.setVisibility(View.GONE);
                }

                break;
            case viewCardType.FOOD:
                FoodViewHolder foodViewHolder=(FoodViewHolder)holder;
                //if(hideButton)
                //    foodViewHolder.btnMeal.setVisibility(View.GONE);
                break;
            case viewCardType.COUNT:
                CountViewHolder countViewHolder=(CountViewHolder)holder;
                CustomHabitDao.CustomHabit_DailyCustomHabit pair=habit_pairs.get(position-staticPos);
                countViewHolder.tvName.setText(pair.customHabit_.name);
                countViewHolder.imageView.setImageResource(pair.customHabit_.iconID);
                if(hideButton)
                {
                    countViewHolder.btnAdd.setVisibility(View.GONE);
                    countViewHolder.btnMinus.setVisibility(View.GONE);
                }
                if(pair.dailyCustomHabit_!=null)
                    countViewHolder.current.setText(String.valueOf(pair.dailyCustomHabit_.current)+"/" );
                else
                    countViewHolder.current.setText("NULL /");
                countViewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setActionForBtnCountAdd(pair);
                    }
                });
                countViewHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setActionForBtnCountMinus(pair);
                    }
                });
                break;
            case viewCardType.TIME:
                TimeViewHolder timeViewHolder = (TimeViewHolder)holder;
                CustomHabitDao.CustomHabit_DailyCustomHabit timePair = habit_pairs.get(position - staticPos);
                timeViewHolder.txtViewTimeName.setText(timePair.customHabit_.name);
                timeViewHolder.imageViewTimeIcon.setImageResource(timePair.customHabit_.iconID);
                break;
            case viewCardType.TICK:
                TickViewHolder tickViewHolder = (TickViewHolder)holder;
                CustomHabitDao.CustomHabit_DailyCustomHabit tickPair = habit_pairs.get(position - staticPos);
                tickViewHolder.textView.setText(tickPair.customHabit_.name);
                if (tickPair.dailyCustomHabit_ != null) {
                    if (tickPair.dailyCustomHabit_.current == 1)
                        tickViewHolder.checkBox.setChecked(true);
                    else
                        tickViewHolder.checkBox.setChecked(false);
                }
                tickViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        setTickChanged(tickPair, isChecked);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        int staticPos = runVisible + foodVisible + sleepVisible;
        if(habit_pairs!=null)
            return staticPos+habit_pairs.size();
        else return staticPos;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
        }
    }

    public class RunViewHolder extends RecyclerView.ViewHolder{
        Button btnRun;
        TextView distance;
        TextView distanceGoal;
        Button btnRunStat;
        CheckBox checkBoxAvailable;
        public RunViewHolder(@NonNull View itemView) {
            super(itemView);
            btnRun=itemView.findViewById(R.id.startRunning);
            distance=itemView.findViewById(R.id.runDistance);
            distanceGoal=itemView.findViewById(R.id.runDistanceGoal);
            btnRunStat=itemView.findViewById(R.id.btnRunStatistic);
            btnRun.setOnClickListener(CardAdapter.this);
            btnRunStat.setOnClickListener(CardAdapter.this);
            checkBoxAvailable = itemView.findViewById(R.id.checkboxRun);
            if(Habit.RUN_AVAILABLE == 1)
                checkBoxAvailable.setChecked(true);
            checkBoxAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(checkBoxAvailable.isChecked())
                        Habit.RUN_AVAILABLE = 1;
                    else
                        Habit.RUN_AVAILABLE = 0;
                }
            });
            if(!hideButton)
                checkBoxAvailable.setVisibility(View.GONE);
        }
    }
    public class SleepViewHolder extends RecyclerView.ViewHolder{
        Button btnSleep;
        TextView sleepTime;
        TextView sleepTimeGoal;
        Button btnSleepStat;
        CheckBox checkBoxAvailable;
        public SleepViewHolder(@NonNull View itemView) {
            super(itemView);
            btnSleep=itemView.findViewById(R.id.startSleeping);
            sleepTime=itemView.findViewById(R.id.sleepTime);
            sleepTimeGoal=itemView.findViewById(R.id.sleepTimeGoal);
            btnSleepStat=itemView.findViewById(R.id.btnSleepStatistic);
            checkBoxAvailable = itemView.findViewById(R.id.checkboxSleep);
            if(Habit.SLEEP_AVAILABLE == 1)
                checkBoxAvailable.setChecked(true);
            checkBoxAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(checkBoxAvailable.isChecked())
                        Habit.SLEEP_AVAILABLE = 1;
                    else
                        Habit.SLEEP_AVAILABLE = 0;
                }
            });
            if(!hideButton)
                checkBoxAvailable.setVisibility(View.GONE);
            btnSleepStat.setOnClickListener(CardAdapter.this);
            btnSleep.setOnClickListener(CardAdapter.this);
        }
    }
    public class FoodViewHolder extends RecyclerView.ViewHolder{
        Button btnMeal;
        TextView calo;
        TextView caloGoal;
        CheckBox checkBoxAvailable;
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            btnMeal=itemView.findViewById(R.id.btnMeal);

            btnMeal.setOnClickListener(CardAdapter.this);
            checkBoxAvailable = itemView.findViewById(R.id.checkboxMeal);
            if(Habit.MEAL_AVAILABLE == 1)
                checkBoxAvailable.setChecked(true);
            checkBoxAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(checkBoxAvailable.isChecked())
                        Habit.MEAL_AVAILABLE = 1;
                    else
                        Habit.MEAL_AVAILABLE = 0;
                }
            });
            if(!hideButton)
                checkBoxAvailable.setVisibility(View.GONE);
        }
    }
    public class CountViewHolder extends RecyclerView.ViewHolder{
        Button btnAdd;
        Button btnMinus;
        TextView current;
        TextView tvName;
        ImageView imageView;
        public CountViewHolder(@NonNull View itemView) {
            super(itemView);
            current=itemView.findViewById(R.id.tvCountCurrent);
            tvName=itemView.findViewById(R.id.countingHabitName);
            imageView = itemView.findViewById(R.id.countingHabitIcon);
            btnAdd = itemView.findViewById(R.id.btn_countAdd);
            btnMinus = itemView.findViewById(R.id.btn_countMinus);
        }
    }
    public class TickViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        AppCompatCheckBox checkBox;
        public TickViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txtViewTickName);
            checkBox = itemView.findViewById(R.id.checkboxTicking);
            if(hideButton)
                checkBox.setClickable(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ArrayList<Integer> listStaticAct = new ArrayList<>();
        if(runVisible == 1)
            listStaticAct.add(viewCardType.RUN);
        if(sleepVisible == 1)
            listStaticAct.add(viewCardType.SLEEP);
        if(foodVisible == 1)
            listStaticAct.add(viewCardType.FOOD);
        int staticPos = listStaticAct.size();

        if (position < staticPos)//3 habit dau la default
        {
            return listStaticAct.get(position);
        }
        else {
            switch (habit_pairs.get(position - staticPos).customHabit_.type) {
                case CustomHabit.TYPE_COUNT:
                    return viewCardType.COUNT;
                case CustomHabit.TYPE_TICK:
                    return viewCardType.TICK;
                case CustomHabit.TYPE_TIME:
                    return viewCardType.TIME;
            }
        }
        return -1;
    }

    public void updateRunVisible(int isVisible) {
        if(!hideButton)
            runVisible = isVisible;
    }

    public void updateRun(int totalDistance){
        totalDistanceString =String.valueOf((float)totalDistance/1000)+"/";
        notifyDataSetChanged();
    }

    public void updateSleepVisible(int isVisible) {
        if (!hideButton)
            sleepVisible = isVisible;
    }

    public void updateSleep(int totalSleepDuration){
        int seconds = (int) (totalSleepDuration / 1000) % 60 ;
        int minutes = (int) ((totalSleepDuration / (1000*60)) % 60);
        int hours   = (int) ((totalSleepDuration / (1000*60*60)) % 24);
        totalSleepdurationString=String.valueOf(hours)+":"+String.valueOf(minutes)+"/";
    }

    public void updateMealVisible(int isVisible) {
        if (!hideButton)
            foodVisible = isVisible;
    }

    private class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView txtViewTimeName;
        ImageView imageViewTimeIcon;
        public TimeViewHolder(View view) {
            super(view);
            txtViewTimeName = itemView.findViewById(R.id.timingHabitName);
            imageViewTimeIcon = itemView.findViewById(R.id.timingHabitIcon);
        }
    }

    private void setActionForBtnCountAdd(CustomHabitDao.CustomHabit_DailyCustomHabit pair) {
        db = AppDatabase.getDatabase(mContext);
        LiveData<DailyCustomHabit> LDdailyCustom=db.dailyCustomHabitDao().getHabit(pair.customHabit_.HabitID, CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
        Observer observer=new Observer<DailyCustomHabit>() {
            @Override
            public void onChanged(DailyCustomHabit dailyCustom) {
                LDdailyCustom.removeObserver(this);
                if(dailyCustom!=null){
                    dailyCustom.current++;
                    db.dailyCustomHabitDao().update(dailyCustom);
                }
                else{
                    DailyCustomHabit dailyCustomHabit=new DailyCustomHabit(pair.customHabit_.HabitID,1,10,CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
                    db.dailyCustomHabitDao().insert(dailyCustomHabit);
                }
            }
        };
        LDdailyCustom.observe((AppCompatActivity)mContext, observer);
    }

    private void setActionForBtnCountMinus(CustomHabitDao.CustomHabit_DailyCustomHabit pair) {
        db = AppDatabase.getDatabase(mContext);
        LiveData<DailyCustomHabit> LDdailyCustom=db.dailyCustomHabitDao().getHabit(pair.customHabit_.HabitID, CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
        Observer observer=new Observer<DailyCustomHabit>() {
            @Override
            public void onChanged(DailyCustomHabit dailyCustom) {
                LDdailyCustom.removeObserver(this);
                if(dailyCustom!=null){
                    dailyCustom.current--;
                    db.dailyCustomHabitDao().update(dailyCustom);
                }
                else{
                    DailyCustomHabit dailyCustomHabit=new DailyCustomHabit(pair.customHabit_.HabitID,0,10,CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
                    db.dailyCustomHabitDao().insert(dailyCustomHabit);
                }
            }
        };
        LDdailyCustom.observe((AppCompatActivity)mContext, observer);
    }

    private void setTickChanged(CustomHabitDao.CustomHabit_DailyCustomHabit tickPair, boolean isChecked) {
        db = AppDatabase.getDatabase(mContext);
        LiveData<DailyCustomHabit> LDdailyCustom=db.dailyCustomHabitDao().getHabit(tickPair.customHabit_.HabitID, CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
        Observer observer=new Observer<DailyCustomHabit>() {
            @Override
            public void onChanged(DailyCustomHabit dailyCustom) {
                LDdailyCustom.removeObserver(this);
                if(dailyCustom!=null){
                    if (isChecked)
                        dailyCustom.current=1;
                    else
                        dailyCustom.current=0;
                    db.dailyCustomHabitDao().update(dailyCustom);
                }
                else{
                    DailyCustomHabit dailyCustomHabit=new DailyCustomHabit(tickPair.customHabit_.HabitID,0,10,CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
                    db.dailyCustomHabitDao().insert(dailyCustomHabit);
                }
            }
        };
        LDdailyCustom.observe((AppCompatActivity)mContext, observer);
    }
}
