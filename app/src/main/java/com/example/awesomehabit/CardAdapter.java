package com.example.awesomehabit;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomehabit.database.custom.CustomHabit;
import com.example.awesomehabit.database.custom.CustomHabitDao;
import com.example.awesomehabit.database.custom.DailyCustomHabit;
import com.example.awesomehabit.meal.MealActivity;
import com.example.awesomehabit.running.RunningTracking;
import com.example.awesomehabit.sleeping.SleepTracker;
import com.example.awesomehabit.statistic.StatisticActivity;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    String totalDistanceString ="no data";
    String totalSleepdurationString ="no data";
    Context mContext;

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
        switch (viewType){
            case 0: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.running_card, parent, false);
                return new RunViewHolder(view);
            }
            case 1: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sleeping_card, parent, false);
                return new SleepViewHolder(view);
            }
            case 2: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_card, parent, false);
                return new FoodViewHolder(view);
            }
            case 3: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.counting_card, parent, false);
                return new CountViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 0:
                RunViewHolder runViewHolder=(RunViewHolder)holder;
                runViewHolder.distance.setText(totalDistanceString);
                runViewHolder.distanceGoal.setText("99.99km");
            break;
            case 1:
                SleepViewHolder sleepViewHolder=(SleepViewHolder)holder;
                sleepViewHolder.sleepTime.setText(totalSleepdurationString);
                break;
            case 2:
                FoodViewHolder foodViewHolder=(FoodViewHolder)holder;
                break;
            case 3:
                CountViewHolder countViewHolder=(CountViewHolder)holder;
                CustomHabitDao.CustomHabit_DailyCustomHabit pair=habit_pairs.get(position-3);
                countViewHolder.tvName.setText(pair.customHabit_.name);
                if(pair.dailyCustomHabit_!=null)
                    countViewHolder.current.setText(String.valueOf(pair.dailyCustomHabit_.current)+"/" );
                else
                    countViewHolder.current.setText("NULL /");
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(habit_pairs!=null)
            return 3+habit_pairs.size();
        else return 3;
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
        public RunViewHolder(@NonNull View itemView) {
            super(itemView);
            btnRun=itemView.findViewById(R.id.startRunning);
            distance=itemView.findViewById(R.id.runDistance);
            distanceGoal=itemView.findViewById(R.id.runDistanceGoal);
            btnRunStat=itemView.findViewById(R.id.btnRunStatistic);
            btnRun.setOnClickListener(CardAdapter.this);
            btnRunStat.setOnClickListener(CardAdapter.this);
        }
    }
    public class SleepViewHolder extends RecyclerView.ViewHolder{
        Button btnSleep;
        TextView sleepTime;
        TextView sleepTimeGoal;
        Button btnSleepStat;
        public SleepViewHolder(@NonNull View itemView) {
            super(itemView);
            btnSleep=itemView.findViewById(R.id.startSleeping);
            sleepTime=itemView.findViewById(R.id.sleepTime);
            sleepTimeGoal=itemView.findViewById(R.id.sleepTimeGoal);
            btnSleepStat=itemView.findViewById(R.id.btnSleepStatistic);
            btnSleepStat.setOnClickListener(CardAdapter.this);
            btnSleep.setOnClickListener(CardAdapter.this);
        }
    }
    public class FoodViewHolder extends RecyclerView.ViewHolder{
        Button btnMeal;
        TextView calo;
        TextView caloGoal;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            btnMeal=itemView.findViewById(R.id.btnMeal);

            btnMeal.setOnClickListener(CardAdapter.this);
        }
    }
    public class CountViewHolder extends RecyclerView.ViewHolder{
        Button btnAdd;
        Button btnMinus;
        TextView current;
        TextView tvName;
        public CountViewHolder(@NonNull View itemView) {
            super(itemView);
            current=itemView.findViewById(R.id.tvCountCurrent);
            tvName=itemView.findViewById(R.id.countingHabitName);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(position<=2)
            return position;
        else
            return 3;
    }

    public void updateRun(int totalDistance){
        totalDistanceString =String.valueOf((float)totalDistance/1000)+"/";
        notifyDataSetChanged();
    }
    public void updateSleep(int totalSleepDuration){
        int seconds = (int) (totalSleepDuration / 1000) % 60 ;
        int minutes = (int) ((totalSleepDuration / (1000*60)) % 60);
        int hours   = (int) ((totalSleepDuration / (1000*60*60)) % 24);
        totalSleepdurationString=String.valueOf(hours)+":"+String.valueOf(minutes)+"/";
    }
}
