package com.example.awesomehabit;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.PagerAdapter;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.running.Run;
import com.example.awesomehabit.database.sleeping.SleepNight;
import com.example.awesomehabit.meal.MealActivity;
import com.example.awesomehabit.running.demo;
import com.example.awesomehabit.sleeping.SleepTracker;

import java.util.Calendar;
import java.util.List;

public class CustomPageAdapter extends PagerAdapter implements View.OnClickListener {
    private Context mContext;
    public  List<SleepNight> sleepNightList;

    public CustomPageAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return CustomCalendarView.NUMBER_OF_DAY_BUTTONS;
    }
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
    public class SleepViewModel extends AndroidViewModel{
        public final LiveData<List<SleepNight>> sleepNights;
        public SleepViewModel(@NonNull Application application) {
            super(application);
            sleepNights=new LiveData<List<SleepNight>>(){};
        }
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.card_list, collection, false);

        Button btnRun=(Button)layout.findViewById(R.id.startRunning);
        Button btnSleep=(Button)layout.findViewById(R.id.startSleeping);
        Button btnMeal=(Button)layout.findViewById(R.id.btnMeal);
        TextView distance=layout.findViewById(R.id.runDistance);
        TextView sleepTime=layout.findViewById(R.id.sleepTime);

        AppDatabase db = AppDatabase.getDatabase(mContext);

        //Get this page date
        //NUMBER_OF_DAY_BUTTON/2 is today
        Calendar pageDay=Calendar.getInstance();
        pageDay.add(Calendar.DATE,position-CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
        pageDay.set(Calendar.HOUR,0);
        pageDay.set(Calendar.MINUTE,0);
        pageDay.set(Calendar.SECOND,0);
        pageDay.set(Calendar.MILLISECOND,0);

        List<Run> runs=db.runDao().getHabitFrom(pageDay);
        List<SleepNight> sleepNights=db.sleepDao().getHabitFrom(pageDay);

        db.sleepDao().getAllNights().observe((AppCompatActivity)mContext, new Observer<List<SleepNight>>() {
            @Override
            public void onChanged(List<SleepNight> sleepNights) {
                sleepNightList=sleepNights;
                if (sleepNightList!=null && sleepNightList.size()>0){
                    sleepTime.setText(String.valueOf(sleepNightList.get(0).getSleepDuration()));
                }
                else{
                    sleepTime.setText("-- / X hours");
                }

            }
        });

        if (runs.size()>0)
        {
            distance.setText(String.valueOf(runs.get(0).distance)+"/ 3.0 km");
        }
        else {
            distance.setText("-- / 3.0 km");
        }
        if (sleepNightList!=null && sleepNightList.size()>0){
            sleepTime.setText(String.valueOf(sleepNightList.get(0).getSleepDuration()));
        }
        else{
            sleepTime.setText("-- / 8 hours");
        }

        btnRun.setOnClickListener(this);
        btnSleep.setOnClickListener(this);
        btnMeal.setOnClickListener(this);
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.startRunning){
            Intent intent=new Intent(mContext, demo.class);
            mContext.startActivity(intent);
        }
        else if(v.getId()==R.id.startSleeping){
            Intent intent=new Intent(mContext, SleepTracker.class);
            mContext.startActivity(intent);
        }
        else if(v.getId()==R.id.btnMeal){
            Intent intent=new Intent(mContext, MealActivity.class);
            mContext.startActivity(intent);
        }
    }
}
