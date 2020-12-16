package com.example.awesomehabit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.Goal;
import com.example.awesomehabit.database.Habit;

public class SetGoal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        AppDatabase db=AppDatabase.getDatabase(getParent());

        EditText distanceGoal= findViewById(R.id.distanceGoal);
        EditText WaterGoal=findViewById(R.id.waterGoal);
        EditText sleepGoal=findViewById(R.id.sleepGoal);
        Button ok=findViewById(R.id.setGoalOk);

        ok.setOnClickListener(v -> {

            String regex = "[0-9]+";

            String run = distanceGoal.getText().toString();
            String sleep = sleepGoal.getText().toString();
            String water = WaterGoal.getText().toString();

            if( !run.matches(regex) | !water.matches(regex) | !sleep.matches(regex)){
                new AlertDialog.Builder(this)
                        .setMessage("Value Invalid, Please Try Again !")
                        .setPositiveButton("Yes", null )
                        .show();
            }else{
                Goal runGoal=db.goalDao().getGoal(Habit.TYPE_RUN);
                runGoal.target=Integer.parseInt(run);
                db.goalDao().update(runGoal);

                Goal sleepGoal_=db.goalDao().getGoal(Habit.TYPE_SLEEP);
                sleepGoal_.target=Integer.parseInt(sleep);
                db.goalDao().update(sleepGoal_);

                Goal waterGoal=db.goalDao().getGoal(Habit.TYPE_COUNT);
                waterGoal.target=Integer.parseInt(water);
                db.goalDao().update(waterGoal);
            }
        });
    }
}