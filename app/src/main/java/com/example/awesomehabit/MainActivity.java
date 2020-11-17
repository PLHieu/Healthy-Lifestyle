package com.example.awesomehabit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.database.HabitListAdapter;
import com.example.awesomehabit.database.HabitViewModel;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomCalendarView.SomeInterface{
    CustomCalendarView customCalendarView;
    ActionBar actionBar;
    RecyclerView rvHabit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        actionBar=getSupportActionBar();
        actionBar.setElevation(0);

        customCalendarView=(CustomCalendarView) findViewById(R.id.customCalendar);
        customCalendarView.setResponder(this);//For onclick

        //Create new running Habit
        rvHabit=findViewById(R.id.rvHabits);
        final HabitListAdapter habitListAdapter=new HabitListAdapter(new HabitListAdapter.HabitDiff());
        rvHabit.setAdapter(habitListAdapter);
        rvHabit.setLayoutManager(new LinearLayoutManager(this));

        //HabitViewModel mHabitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
        HabitViewModel mHabitViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(HabitViewModel.class);
        mHabitViewModel.allHabits.observe(this, new Observer<List<Habit>>() {
            @Override
            public void onChanged(List<Habit> words) {
                // Update the cached copy of the words in the adapter.
                habitListAdapter.submitList(words);
            }
        });

    }

    @Override
    public void foo(String day) {
        //scrollView.fullScroll(ScrollView.FOCUS_UP);
    }
}