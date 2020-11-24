package com.example.awesomehabit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.database.HabitListAdapter;
import com.example.awesomehabit.database.HabitViewModel;
import com.example.awesomehabit.statistic.MainActivity2;
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
        final HabitListAdapter habitListAdapter=new HabitListAdapter(getApplicationContext());
        rvHabit.setAdapter(habitListAdapter);
        rvHabit.setLayoutManager(new LinearLayoutManager(this));

        //View Model and update
        HabitViewModel mHabitViewModel = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(HabitViewModel.class);
        mHabitViewModel.allHabits.observe(this, new Observer<List<Habit>>() {
            @Override
            public void onChanged(List<Habit> words) {
                // Update the cached copy of the words in the adapter.
                //habitListAdapter.submitList(words);
                habitListAdapter.setData(words);
                Log.d("Observer","Word size:"+ String.valueOf(words.size()));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_statistic:
                startActivity(new Intent(this, MainActivity2.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void foo(String day) {
        //scrollView.fullScroll(ScrollView.FOCUS_UP);
    }
}