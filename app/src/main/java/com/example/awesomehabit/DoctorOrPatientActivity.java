package com.example.awesomehabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awesomehabit.doctor.login.LoginActivity;

public class DoctorOrPatientActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_patient);

        findViewById(R.id.iamdoctor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DoctorOrPatientActivity.this, LoginActivity.class);
startActivity(i);
            }
        });
        findViewById(R.id.iampatient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DoctorOrPatientActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
