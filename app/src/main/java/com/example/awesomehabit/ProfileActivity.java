package com.example.awesomehabit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    final ArrayList<String> temp = new ArrayList<>(Arrays.asList("Male", "Female", "Other"));
    Spinner spinner;
    TextView txtViewBirthday;
    ImageView imgViewIcon;
    TextView txtViewChangePass;
    TextView txtViewUser;
    TextView txtViewUserName;
    EditText edtName;
    EditText edtMail;
    EditText edtAddress;

    TextView txtViewSave;
    TextView txtViewExit;

    public static class Profile{
        public static String userName = "";
        public static String name = "";
        public static String mail = "";
        public static String address = "";
        public static int gender = 0;
        public static String birthday = "";
        public static String avatar = "";
    }

    final int RESULT_LOAD_IMG = 1000;
    final int RESULT_CHANGE_PASS = 1001;

    SharedPreferences preferences;

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        spinner = findViewById(R.id.spinnerUserSex);
        txtViewBirthday = findViewById(R.id.txtViewUserBirthday);
        imgViewIcon = findViewById(R.id.imgViewUserIcon);
        txtViewChangePass = findViewById(R.id.txtViewChangePass);
        txtViewUserName = findViewById(R.id.txtViewUserName);
        txtViewUser = findViewById(R.id.txtViewUser);
        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtUserAddress);
        edtMail = findViewById(R.id.edtUserMail);
        txtViewSave = findViewById(R.id.txtViewSave);
        txtViewExit = findViewById(R.id.txtViewExit);

        initSpinner();
        txtViewBirthday.setOnClickListener(v -> setActionForTxtCalendar());
        imgViewIcon.setOnClickListener(v -> setImage());
        txtViewChangePass.setOnClickListener(v -> changePassword());
        txtViewSave.setOnClickListener(v -> {
            updateData();
            onBackPressed();
        });
        txtViewExit.setOnClickListener(v -> onBackPressed());

        preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        getData();
    }

    private void updateData() {
        preferences.edit().putString("name", edtName.getText().toString()).apply();
        preferences.edit().putString("email", edtMail.getText().toString()).apply();
        preferences.edit().putString("ngaysinh", txtViewBirthday.getText().toString()).apply();
        preferences.edit().putInt("gioitinh", Profile.gender).apply();
        preferences.edit().putString("diachi", edtAddress.getText().toString()).apply();
        preferences.edit().putString("avatar", Profile.avatar).apply();
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void getData() {
        Profile.userName = preferences.getString("username", "guest");
        Profile.name = preferences.getString("name", "guest");
        Profile.mail = preferences.getString("email", "");
        Profile.address = preferences.getString("diachi", "");
        Profile.gender = preferences.getInt("gioitinh", 0);
        Profile.birthday = preferences.getString("ngaysinh", "");
        Profile.avatar = preferences.getString("avatar", null);

        txtViewUser.setText(Profile.userName);
        txtViewUserName.setText(Profile.name);
        txtViewBirthday.setText(Profile.birthday);
        edtName.setText(Profile.name, TextView.BufferType.EDITABLE);
        edtMail.setText(Profile.mail, TextView.BufferType.EDITABLE);
        edtAddress.setText(Profile.address, TextView.BufferType.EDITABLE);
        spinner.setSelection(Profile.gender);
        if(Profile.avatar != null)
            imgViewIcon.setImageBitmap(StringToBitMap(Profile.avatar));
    }

    private void changePassword() {
        Intent intent = new Intent(ProfileActivity.this, ChangePassActivity.class);
        startActivityForResult(intent, RESULT_CHANGE_PASS);
    }

    private void setImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == RESULT_LOAD_IMG)
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    Profile.avatar = BitMapToString(selectedImage);
                    imgViewIcon.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(ProfileActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        if (reqCode == RESULT_CHANGE_PASS) {
        }
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
                Profile.gender = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(0);
    }
}