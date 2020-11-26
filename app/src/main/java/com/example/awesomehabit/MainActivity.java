package com.example.awesomehabit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.database.HabitListAdapter;
import com.example.awesomehabit.database.HabitViewModel;
import com.example.awesomehabit.statistic.MainActivity2;
import com.mapbox.mapboxsdk.Mapbox;

public class MainActivity extends AppCompatActivity implements CustomCalendarView.CustomCalendarViewInterface {
    CustomCalendarView customCalendarView;
    ActionBar actionBar;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main);

        actionBar=getSupportActionBar();
        actionBar.setElevation(0);

        customCalendarView=(CustomCalendarView) findViewById(R.id.customCalendar);
        customCalendarView.setResponder(this);//For onclick

        viewPager=(ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(new CustomPageAdapter(this));
        viewPager.setCurrentItem(500);
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
        //Create new running Habit
        /*rvHabit=findViewById(R.id.rvHabits);
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
        });*/

    }

    @Override
    public void onDaySelected(int position) {
        viewPager.setCurrentItem(position, true);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_statistic:
                //startActivity(new Intent(this, MainActivity2.class));
                customCalendarView.smoothScrollTo(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
                viewPager.setCurrentItem(CustomCalendarView.NUMBER_OF_DAY_BUTTONS/2);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}