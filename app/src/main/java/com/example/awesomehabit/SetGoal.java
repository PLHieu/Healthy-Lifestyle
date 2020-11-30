package com.example.awesomehabit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.Goal;
import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.generated.callback.OnClickListener;

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

        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Goal runGoal=db.goalDao().getGoal(Habit.TYPE_RUN);
                runGoal.target=Integer.parseInt(distanceGoal.getText().toString());
                db.goalDao().update(runGoal);

                Goal sleepGoal_=db.goalDao().getGoal(Habit.TYPE_SLEEP);
                sleepGoal_.target=Integer.parseInt(sleepGoal.getText().toString());
                db.goalDao().update(sleepGoal_);

                Goal waterGoal=db.goalDao().getGoal(Habit.TYPE_COUNT);
                waterGoal.target=Integer.parseInt(WaterGoal.getText().toString());
                db.goalDao().update(waterGoal);
            }
        });
    }
}