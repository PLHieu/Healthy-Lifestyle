package com.example.awesomehabit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.Goal;
import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.database.running.Run;

public class SetGoal extends AppCompatActivity{

    TimePicker timePicker ;
    Button  btnOk, back;
    ImageButton btnAddWater, btnDownWater,btnWaterBottle;
    EditText distanceGoal;
    TextView tvWaterGoal;
    AppDatabase db;
    int numWater;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setgoal2);
        db = AppDatabase.getDatabase(getParent());
        initView();
        getDataFromDB();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDataFromDB() {

        Goal runGoal=db.goalDao().getGoal(Habit.TYPE_RUN);
        if(runGoal == null){
            distanceGoal.setText("0");
        }else{
            distanceGoal.setText(String.valueOf(runGoal.target));
        }

        Goal sleepGoal=db.goalDao().getGoal(Habit.TYPE_SLEEP);
        if(sleepGoal == null) {
            timePicker.setHour(0);
            timePicker.setMinute(0);
        }else{
            timePicker.setHour(sleepGoal.target);
            timePicker.setMinute(0);
        }


        Goal water = db.goalDao().getGoal(Habit.TYPE_COUNT);
        if(water == null){
            tvWaterGoal.setText(String.valueOf(0));

        }else{
            tvWaterGoal.setText(String.valueOf(water.target));

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        distanceGoal= findViewById(R.id.distanceGoal);
        timePicker = findViewById(R.id.TP_setSleepgoal);
        btnAddWater = findViewById(R.id.btn_upwater);
        btnDownWater = findViewById(R.id.btn_Downwater);
        btnOk = findViewById(R.id.setGoal2Ok);
        btnWaterBottle = findViewById(R.id.waterchange);
        tvWaterGoal = findViewById(R.id.waterGoal);
        back = findViewById(R.id.setgoalback);

        // time picker
        timePicker.setIs24HourView(true);

        btnOk.setOnClickListener(v -> {
            String regex = "[0-9]+";

            if( !distanceGoal.getText().toString().matches(regex) ){
                new AlertDialog.Builder(this)
                        .setMessage("Value Invalid, Please Try Again !")
                        .setPositiveButton("Yes", null )
                        .show();
            }else {

                Goal runGoal = db.goalDao().getGoal(Habit.TYPE_RUN);
                if(runGoal == null){
                    runGoal = new Goal(Habit.TYPE_RUN,Integer.parseInt(distanceGoal.getText().toString()));
                    db.goalDao().insert(runGoal);
                }else{
                    runGoal.target = Integer.parseInt(distanceGoal.getText().toString());
                    db.goalDao().update(runGoal);
                }


                Goal sleepGoal_ = db.goalDao().getGoal(Habit.TYPE_SLEEP);
                if(sleepGoal_ == null){
                    sleepGoal_ = new Goal(Habit.TYPE_SLEEP,Integer.parseInt(String.valueOf(timePicker.getHour())));
                    db.goalDao().insert(sleepGoal_);
                }else{
                    sleepGoal_.target = Integer.parseInt(String.valueOf(timePicker.getHour()));
                    db.goalDao().update(sleepGoal_);
                }


                Goal waterGoal = db.goalDao().getGoal(Habit.TYPE_COUNT);
                if(waterGoal == null){
                    waterGoal = new Goal(Habit.TYPE_COUNT,Integer.parseInt(tvWaterGoal.getText().toString()));
                    db.goalDao().insert(waterGoal);
                }else{
                    waterGoal.target = Integer.parseInt(tvWaterGoal.getText().toString());
                    db.goalDao().update(waterGoal);
                }

                Toast.makeText(this, "Update goal Sucessfully", Toast.LENGTH_SHORT).show();
            }
        });


        btnDownWater.setOnClickListener(v -> {
            tvWaterGoal.setText(String.valueOf(--numWater));
        });


        btnAddWater.setOnClickListener(v -> tvWaterGoal.setText(String.valueOf(++numWater)));

        back.setOnClickListener(v -> {
            finish();
        });

    }

}