package com.example.awesomehabit.doctor;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.user.User;
import com.google.android.material.snackbar.Snackbar;

public class AddUserActivity extends AppCompatActivity {
    ImageView ivAvatar;
    EditText editTextName;
    EditText editTextAddress;
    Button buttonAdd;
    Bitmap imageBitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ivAvatar.setImageBitmap(imageBitmap);
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 111;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        ivAvatar=findViewById(R.id.ivAvatar);
ivAvatar.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
        editTextName=findViewById(R.id.editTextTextPersonName);
        editTextAddress=findViewById(R.id.editTextTextPersonName2);
        imageBitmap=null;
        ivAvatar.setOnClickListener(new View.OnClickListener() {
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

        buttonAdd=findViewById(R.id.btnCreatePatient);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageBitmap==null){
                    Snackbar.make(v, "Please take a picture of your patient", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    User user=new User(editTextName.getText().toString(),editTextAddress.getText().toString(), Ults.getStringFromBitmap(imageBitmap));
                    AppDatabase.getDatabase(AddUserActivity.this).userDao().insert(user);

                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }
}
