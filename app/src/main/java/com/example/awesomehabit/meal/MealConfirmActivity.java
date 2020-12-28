package com.example.awesomehabit.meal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.awesomehabit.CustomCalendarView;
import com.example.awesomehabit.R;
import com.example.awesomehabit.database.meal.DailyMeal;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MealConfirmActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 111;
    ImageView imageViewFood;
    Button btnOk;
    Button btnCancel;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Meal meal;
            if(imageBitmap!=null) {
                meal = new Meal("Bun dau", 69);
                meal.setBitmap(imageBitmap);
                imageViewFood.setImageBitmap(imageBitmap);

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("meal",meal);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                });
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED){
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_confirm);
        imageViewFood = (ImageView) findViewById(R.id.imageViewMealConfirm);
        btnOk=findViewById(R.id.mealConfirmOk);

        btnCancel=findViewById(R.id.mealConfirmCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
}
