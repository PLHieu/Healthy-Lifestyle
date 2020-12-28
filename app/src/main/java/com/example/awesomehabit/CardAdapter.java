package com.example.awesomehabit;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomehabit.database.custom.CustomHabit;
import com.example.awesomehabit.database.custom.CustomHabitDao;
import com.example.awesomehabit.meal.MealActivity;
import com.example.awesomehabit.running.RunningTracking;
import com.example.awesomehabit.sleeping.SleepTracker;
import com.example.awesomehabit.statistic.StatisticActivity;

import java.util.List;

import static android.content.ContentValues.TAG;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

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
        Log.d(TAG, "onCreateViewHolder: " + String.valueOf(viewType) );
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
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + String.valueOf(position));
        switch (holder.getItemViewType()){
            case viewCardType.RUN:
                RunViewHolder runViewHolder=(RunViewHolder)holder;
                runViewHolder.distance.setText(totalDistanceString);
                runViewHolder.distanceGoal.setText("99.99km");
            break;
            case viewCardType.SLEEP:
                SleepViewHolder sleepViewHolder=(SleepViewHolder)holder;
                sleepViewHolder.sleepTime.setText(totalSleepdurationString);
                break;
            case viewCardType.FOOD:
                FoodViewHolder foodViewHolder=(FoodViewHolder)holder;
                break;
            case viewCardType.TICK:
                TickViewHolder tickViewHolder =(TickViewHolder)holder;
                CustomHabitDao.CustomHabit_DailyCustomHabit tick_pair=habit_pairs.get(position-3);
                tickViewHolder.imageView.setImageResource(tick_pair.customHabit_.iconID);
                break;
            case viewCardType.COUNT:
                CountViewHolder countViewHolder=(CountViewHolder)holder;
                CustomHabitDao.CustomHabit_DailyCustomHabit pair=habit_pairs.get(position-3);
                countViewHolder.tvName.setText(pair.customHabit_.name);
                countViewHolder.imageView.setImageResource(pair.customHabit_.iconID);
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
        ImageView imageView;
        public CountViewHolder(@NonNull View itemView) {
            super(itemView);
            current=itemView.findViewById(R.id.tvCountCurrent);
            tvName=itemView.findViewById(R.id.countingHabitName);
            imageView = itemView.findViewById(R.id.countingHabitIcon);
        }
    }
    public class TickViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public TickViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.tickingHabitIcon);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position<=2)//3 habit dau la default
            return position;
        else
        {
            switch (habit_pairs.get(position-3).customHabit_.type){
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
