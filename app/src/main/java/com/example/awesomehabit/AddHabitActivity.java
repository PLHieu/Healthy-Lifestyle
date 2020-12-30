package com.example.awesomehabit;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.custom.CustomHabit;

import java.util.ArrayList;
import java.util.Arrays;

public class AddHabitActivity extends AppCompatActivity {

    ArrayList<Integer> listIcon = new ArrayList<>(Arrays.asList(R.drawable.sleep, R.drawable.run, R.drawable.water, R.drawable.waterbottle, R.drawable.bed));

    Dialog dialog;
    int iconID = 0;
    int habitType = CustomHabit.TYPE_COUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        setActionForButton();
        initHabitTypeSpinner();
        createGridLayoutIcon();
    }

    private void setActionForButton() {
        ImageButton imageButton = findViewById(R.id.imgButtonIcon);
        imageButton.setOnClickListener(v -> {
            showGridToChooseIcon();
        });

        Button button = findViewById(R.id.btnAddHabit);
        button.setOnClickListener(v -> {
            setActionForBtnAdd();
            finish();
        });

        button = findViewById(R.id.btnHabitTypeInfo);
        button.setOnClickListener(v -> setActionForBtnHabitTypeInfo());
    }

    private void setActionForBtnHabitTypeInfo() {
        switch (habitType){
            case CustomHabit.TYPE_COUNT:
                setDialog(getString(R.string.dialogCountType));
                break;
            case CustomHabit.TYPE_TICK:
                setDialog(getString(R.string.dialogTickType));
                break;
            case CustomHabit.TYPE_TIME:
                setDialog(getString(R.string.dialogTimeType));
                break;
        }
        dialog.show();
    }

    private void initHabitTypeSpinner() {
        Spinner spinner = findViewById(R.id.spinnerHabitType);
        String[] habitTypeArray = new String[]{"Count", "Time", "Tick"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, habitTypeArray);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                habitType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setDialog(String content) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_type, null);
        TextView textView = view.findViewById(R.id.txtViewContentType);
        textView.setText(content);

        dialog = new Dialog(this);
        dialog.setContentView(view);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        }
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().getAttributes().windowAnimations= R.style.animation;
    }

    private void showGridToChooseIcon() {
        GridLayout gridLayout = findViewById(R.id.gridLayoutIcon);
        gridLayout.setVisibility(View.VISIBLE);
    }

    private void createGridLayoutIcon() {
        for (int i = 0; i < listIcon.size(); i++) {
            addImageButtonWith(listIcon.get(i));
        }

        GridLayout gridLayout = findViewById(R.id.gridLayoutIcon);
        gridLayout.setVisibility(View.GONE);
    }

    private void addImageButtonWith( Integer id) {
        GridLayout gridLayout = findViewById(R.id.gridLayoutIcon);
        ImageButton imageButton = new ImageButton(this);
        imageButton.setImageResource(id);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        ViewGroup.LayoutParams layoutParams = new AbsListView.LayoutParams((int)(width / 3.5), (int)(width / 3.5));
        imageButton.setLayoutParams(layoutParams);
        imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageButton.setOnClickListener(v -> {
            ImageButton imageButton1 = findViewById(R.id.imgButtonIcon);
            imageButton1.setImageResource(id);
            gridLayout.setVisibility(View.GONE);
            iconID = id;
        });
        gridLayout.addView(imageButton);
    }

    private void setActionForBtnAdd() {
        EditText editText = findViewById(R.id.edtHabitName);
        String habitName = editText.getText().toString();
        if (habitName.matches("")) {
            Toast.makeText(this, R.string.WarningInAddHabit, Toast.LENGTH_SHORT).show();
            return;
        }
        AppDatabase appDatabase=AppDatabase.getDatabase(this);
        appDatabase.customHabitDao().insert(new CustomHabit(habitName, habitType, iconID));
    }
}