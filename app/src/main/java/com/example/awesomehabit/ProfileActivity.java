package com.example.awesomehabit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity{

    final ArrayList<String> temp = new ArrayList<>(Arrays.asList("Male", "Female", "Other"));
    Spinner spinner;
    TextView txtViewBirthday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        spinner = findViewById(R.id.spinnerUserSex);
        txtViewBirthday = findViewById(R.id.txtViewUserBirthday);

        initSpinner();
        txtViewBirthday.setOnClickListener(v -> setActionForTxtCalendar());
    }

    private void setActionForTxtCalendar() {
        final Calendar cldr = Calendar.getInstance();
        int day1 = cldr.get(Calendar.DAY_OF_MONTH);
        int month1 = cldr.get(Calendar.MONTH);
        int year1 = cldr.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(ProfileActivity.this,
                (view, year, month, dayOfMonth) -> {
                    String strYear = String.valueOf(year);
                    String strMonth = String.valueOf(month + 1);
                    String strDay = String.valueOf(dayOfMonth);
                    if (month < 10)
                        strMonth = "0" + strMonth;
                    if(dayOfMonth < 10)
                        strDay = "0" + strDay;
                    txtViewBirthday.setText(strDay + '/' + strMonth + '/' + strYear);
                }, year1, month1, day1);
        dialog.show();
    }

    private void initSpinner() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_text_view_item, temp);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_text_view_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(0);
    }
}