package com.example.awesomehabit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class AddHabitActivity extends AppCompatActivity {

    static int GALLERY_REQUEST = 3004;
    ArrayList<Integer> listIcon = new ArrayList<>(Arrays.asList(R.drawable.sleep, R.drawable.run, R.drawable.water, R.drawable.waterbottle, R.drawable.bed));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        ImageButton imageButton = (ImageButton) findViewById(R.id.imgButtonIcon);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setActionForImageChoosing();
                showGridToChooseIcon();
            }
        });

        Button button = (Button)findViewById(R.id.btnAddHabit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActionForBtnAdd();
            }
        });

        Button buttonCount = (Button)findViewById(R.id.btnCountType);
        buttonCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActionForHabitTypeButton(buttonCount);
            }
        });

        Button buttonTime = (Button)findViewById(R.id.btnTimeType);
        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActionForHabitTypeButton(buttonTime);
            }
        });

        Button buttonTick = (Button)findViewById(R.id.btnTickType);
        buttonTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActionForHabitTypeButton(buttonTick);
            }
        });

        createGridLayoutIcon();
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
        ViewGroup.LayoutParams layoutParams = new AbsListView.LayoutParams(300,300);
        imageButton.setLayoutParams(layoutParams);
        imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton imageButton1 = findViewById(R.id.imgButtonIcon);
                imageButton1.setImageResource(id);
                gridLayout.setVisibility(View.GONE);
            }
        });
        gridLayout.addView(imageButton);
    }

    public void setActionForHabitTypeButton(Button button) {
        Button buttonCount = (Button)findViewById(R.id.btnCountType);
        Button buttonTime = (Button)findViewById(R.id.btnTimeType);
        Button buttonTick = (Button)findViewById(R.id.btnTickType);

        buttonCount.setBackgroundResource(R.color.teal_700);
        buttonTick.setBackgroundResource(R.color.teal_700);
        buttonTime.setBackgroundResource(R.color.teal_700);

        button.setBackgroundResource(R.color.teal_200);
    }


    private void setActionForBtnAdd() {
        EditText editText = (EditText) findViewById(R.id.edtHabitName);
        String habitName = editText.getText().toString();
        if (habitName.matches("")) {
            Toast.makeText(this, "You did not enter a name for your habit.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void setActionForImageChoosing() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST)
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ((ImageButton) findViewById(R.id.imgButtonIcon)).setBackground(new BitmapDrawable(getResources(), selectedImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}