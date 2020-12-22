package com.example.awesomehabit.meal;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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

public class MealActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    AppDatabase db;
    RecyclerView rvMeal;
    ArrayList<Meal> meals;
    Button btnAddMeal;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if(imageBitmap!=null) {
                Meal meal = new Meal("bun dau", 69);

                meal.setBitmap(imageBitmap);
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
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        rvMeal= (RecyclerView) findViewById(R.id.rvMeals);
        btnAddMeal=(Button) findViewById(R.id.addNewMeal);

        meals=new ArrayList<>();
        MealAdapter mealAdapter=new MealAdapter(meals);
        rvMeal.setAdapter(mealAdapter);
        rvMeal.setLayoutManager(new LinearLayoutManager(this));


        db=AppDatabase.getDatabase(this);
        db.dailyMealDao().getHabitFrom(CustomCalendarView.currentDay_Day,CustomCalendarView.currentDay_Month,CustomCalendarView.currentDay_Year).observe(this, new Observer<DailyMeal>() {
            @Override
            public void onChanged(DailyMeal dailyMeal) {
                if(dailyMeal!=null)
                {
                    mealAdapter.setMeals(dailyMeal.mealList);
                }

            }
        });


        btnAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }
            }
        });
    }
}
