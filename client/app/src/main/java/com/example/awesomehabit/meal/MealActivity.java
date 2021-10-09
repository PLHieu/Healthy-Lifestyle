package com.example.awesomehabit.meal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomehabit.CustomCalendarView;
import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.meal.DailyMeal;

import java.util.ArrayList;

public class MealActivity extends AppCompatActivity implements MealAdapter.OnMealListener, RemoveMealDialog.RemoveMealDialogListener {
    static final int MEAL_CONFIRM_REQUEST = 888;
    AppDatabase db;
    RecyclerView rvMeal;
    ArrayList<Meal> meals;
    Button btnAddMeal;
    MealAdapter mealAdapter;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MEAL_CONFIRM_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Meal meal=(Meal) extras.get("meal");

            LiveData<DailyMeal> dailyMealLiveData = db.dailyMealDao().getHabitFrom(CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
            dailyMealLiveData.observe(this, new Observer<DailyMeal>() {
                @Override
                public void onChanged(DailyMeal dailyMeal) {
                    dailyMealLiveData.removeObserver(this);
                    if (dailyMeal != null) {
                        dailyMeal.mealList.add(meal);
                        db.dailyMealDao().update(dailyMeal);
                    } else {
                        DailyMeal dailyMeal1 = new DailyMeal(CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);
                        dailyMeal1.mealList = new ArrayList<>();
                        dailyMeal1.mealList.add(meal);
                        db.dailyMealDao().insert(dailyMeal1);
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        rvMeal= (RecyclerView) findViewById(R.id.rvMeals);
        btnAddMeal=(Button) findViewById(R.id.addNewMeal);

        meals=new ArrayList<>();
        mealAdapter=new MealAdapter(meals,this);//Empty
        rvMeal.setAdapter(mealAdapter);
        rvMeal.setLayoutManager(new LinearLayoutManager(this));

        db=AppDatabase.getDatabase(this);
        db.dailyMealDao().getHabitFrom(CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year).observe(this, new Observer<DailyMeal>() {
            @Override
            public void onChanged(DailyMeal dailyMeal_) {
                if(dailyMeal_!=null)
                {
                    mealAdapter.setMeals(dailyMeal_.mealList);
                }
            }
        });


        btnAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mealConfirmIntent=new Intent(MealActivity.this,MealConfirmActivity.class);
               startActivityForResult(mealConfirmIntent, MEAL_CONFIRM_REQUEST);
            }
        });
    }

    @Override
    public void onMealClick(int position) {
        openDialog(position);
    }

    private void openDialog(int position) {
        RemoveMealDialog removeMealDialog=new RemoveMealDialog(position);
        removeMealDialog.show(getSupportFragmentManager(),"remove a meal");
    }

    @Override
    public void confirmMealRemove(int position) {
        LiveData<DailyMeal> dailyMealLiveData =
                db.dailyMealDao().getHabitFrom(CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year);

        dailyMealLiveData.observe(this, new Observer<DailyMeal>() {
            @Override
            public void onChanged(DailyMeal dailyMeal_) {
                dailyMealLiveData.removeObserver(this);
                if(dailyMeal_!=null)
                {
                    dailyMeal_.mealList.remove(position);
                    db.dailyMealDao().update(dailyMeal_);
                }
            }
        });
    }
}
