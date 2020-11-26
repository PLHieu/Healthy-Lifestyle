package com.example.awesomehabit.meal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomehabit.R;

import java.util.ArrayList;

public class MealActivity extends AppCompatActivity {
    RecyclerView rvMeal;
    ArrayList<Meal> meals;
    Button btnAddMeal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        rvMeal= (RecyclerView) findViewById(R.id.rvMeals);
        btnAddMeal=(Button) findViewById(R.id.addNewMeal);
        meals=new ArrayList<>();
        meals.add(new Meal("Bun dau",300));
        meals.add(new Meal("Com tam",350));
        meals.add(new Meal("Bun dau",300));
        MealAdapter mealAdapter=new MealAdapter(meals);

        rvMeal.setAdapter(mealAdapter);
        rvMeal.setLayoutManager(new LinearLayoutManager(this));

        btnAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }
}
