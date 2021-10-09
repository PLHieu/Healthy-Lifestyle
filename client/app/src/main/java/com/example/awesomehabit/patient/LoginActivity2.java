package com.example.awesomehabit.patient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.Goal;
import com.example.awesomehabit.database.GoalDao;
import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.database.custom.CustomHabit;
import com.example.awesomehabit.database.custom.CustomHabitDao;
import com.example.awesomehabit.database.custom.DailyCustomHabit;
import com.example.awesomehabit.database.custom.DailyCustomHabitDao;
import com.example.awesomehabit.database.meal.DailyMeal;
import com.example.awesomehabit.database.meal.DailyMealDao;
import com.example.awesomehabit.database.running.Run;
import com.example.awesomehabit.database.running.RunDao;
import com.example.awesomehabit.database.sleeping.SleepDatabaseDao;
import com.example.awesomehabit.database.sleeping.SleepNight;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity2 extends AppCompatActivity {
    private String DOMAIN;

    SharedPreferences preferences;
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        DOMAIN = getApplicationContext().getResources().getString(R.string.server_domain);

        preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        String userName1 = preferences.getString("username",null);
        if(userName1 != null) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

        Button button = findViewById(R.id.btnLogin);
        button.setOnClickListener(v -> {
            try {
                setActionForLoginButton();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        button.setOnClickListener(v -> {
            try {
                setActionForLoginButton();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void setActionForLoginButton() throws JSONException {
        EditText edtUserName = findViewById(R.id.edtUserName);
        EditText edtPassWord = findViewById(R.id.edtPassword);

        String userName = edtUserName.getText().toString();
        String passWord = edtPassWord.getText().toString();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", userName);
        jsonObject.put("password", passWord);
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        preferences.edit().putString("password", passWord).apply();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,DOMAIN +  "myuser/signin/",jsonObject, r -> {
            try {
                preferences.edit().putString("refresh_token", r.getString("refresh_token")).apply();
                preferences.edit().putString("access_token", r.getString("access_token")).apply();
                preferences.edit().putLong("access_expires", Long.parseLong(r.getString("access_expires"))).apply();
                preferences.edit().putLong("refresh_expires", Long.parseLong(r.getString("refresh_expires"))).apply();
                preferences.edit().putLong("lastloggedin", Long.parseLong(String.valueOf(System.currentTimeMillis()/1000))).apply();
                preferences.edit().putString("username", r.getString("username")).apply();
                preferences.edit().putString("email", r.getString("email")).apply();
                preferences.edit().putString("name", r.getString("name")).apply();
                preferences.edit().putInt("tuoi", Integer.parseInt(r.getString("tuoi"))).apply();
                preferences.edit().putString("diachi", r.getString("diachi")).apply();
                preferences.edit().putInt("gioitinh", Integer.parseInt(r.getString("gioitinh"))).apply();
                preferences.edit().putString("ngaysinh", r.getString("ngaysinh")).apply();
                try {
                    preferences.edit().putString("avatar", r.getString("profile_pic")).apply();
                } catch (JSONException ignored) { }
//                Toast.makeText(getBaseContext(), "Login success", Toast.LENGTH_LONG).show();

                pullDB(preferences.getString("username", null));



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, e-> {
             Log.d("sync", e.toString());
//            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        });
        queue.add(jsonObjectRequest);
    }


    private void pullDB(String username) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,   DOMAIN + "sync/pull/", jsonObject,
                response -> {
                    Log.d("sync", "Hello: " + response);

                    // Lay cac visible roi update vao cac bien
                    try {
                        Habit.RUN_AVAILABLE =  Integer.parseInt( response.getString("visiRun"));
                        Habit.SLEEP_AVAILABLE =  Integer.parseInt( response.getString("visiSleep"));
                        Habit.MEAL_AVAILABLE =  Integer.parseInt( response.getString("visiMeal"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // truoc khi pull ve phai clear het table
                    RunDao runDao = AppDatabase.getDatabase(getApplicationContext()).runDao();
                    DailyMealDao mealDao = AppDatabase.getDatabase(getApplicationContext()).dailyMealDao();
                    SleepDatabaseDao sleepDatabaseDao = AppDatabase.getDatabase(getApplicationContext()).sleepDao();
                    CustomHabitDao customHabitDao = AppDatabase.getDatabase(getApplicationContext()).customHabitDao();
                    DailyCustomHabitDao dailyCustomHabitDao = AppDatabase.getDatabase(getApplicationContext()).dailyCustomHabitDao();
                    GoalDao goalDao = AppDatabase.getDatabase(getApplicationContext()).goalDao();

                    runDao.deleteTable();
                    mealDao.deleteTable();
                    sleepDatabaseDao.deleteTable();
                    customHabitDao.deleteTable();
                    dailyCustomHabitDao.deleteTable();
                    goalDao.deleteTable();
                    Log.d("sync", "Da delete");

                    String runjson = null;
                    String sleepjson = null;
                    String dailymealjson = null;
                    String customhbjson = null;
                    String dlcustomhbjson = null;
                    String goaljson = null;


                    try {
                        runjson = response.getString("run");
                        sleepjson = response.getString("sleep");
                        dailymealjson = response.getString("dailymeal");
                        customhbjson = response.getString("customhb");
                        dlcustomhbjson = response.getString("DLcustomhb");
                        goaljson = response.getString("goal");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Gson gson = new Gson();
                    Type typeRun = new TypeToken<List<Run>>(){}.getType();
                    Type typeSleep = new TypeToken<List<SleepNight>>(){}.getType();
                    Type typeDailyMeal = new TypeToken<List<DailyMeal>>(){}.getType();
                    Type typeCustomHB = new TypeToken<List<CustomHabit>>(){}.getType();
                    Type typeDailyCustomHB = new TypeToken<List<DailyCustomHabit>>(){}.getType();
                    Type typeGoal = new TypeToken<List<Goal>>(){}.getType();

                    Log.d("sync", "Da lay danh sach");

                    List<Run> runs = gson.fromJson(runjson, typeRun);
                    List<SleepNight> sleepNights = gson.fromJson(sleepjson, typeSleep);
                    List<DailyMeal> meals = gson.fromJson(dailymealjson, typeDailyMeal);
                    List<CustomHabit> customHabits = gson.fromJson(customhbjson, typeCustomHB);
                    List<DailyCustomHabit> dailyCustomHabits = gson.fromJson(dlcustomhbjson, typeDailyCustomHB);
                    List<Goal> goals = gson.fromJson(goaljson, typeGoal);

                    updateRun(runs);
                    updateSleep(sleepNights);
                    updateMeal(meals);
                    updateCTHB(customHabits);
                    updateDLCTHB(dailyCustomHabits);
                    updateGoal(goals);

//                    Toast.makeText(this, "Pull Sucessfully", Toast.LENGTH_LONG).show();
                    Log.d("sync", "Pull Sucessfully");

                    Log.d("Login", response.toString());
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                },
                error -> Log.d("sync", error.toString())){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + preferences.getString("access_token","null"));
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> mParams = new HashMap<String, String>();
                mParams.put("username", username);
                return mParams;
            }
        };
        queue.add(jsonObjectRequest);
    }



    private void updateRun(List<Run> runs){
        RunDao runDao = AppDatabase.getDatabase(getApplicationContext()).runDao();
        for(Run run : runs){
            runDao.insertRun(run);
        }
    }

    private void updateSleep(List<SleepNight> sleepNights){
        SleepDatabaseDao sleepDatabaseDao = AppDatabase.getDatabase(getApplicationContext()).sleepDao();
        for(SleepNight sleepNight : sleepNights){
            sleepDatabaseDao.insert(sleepNight);
        }
    }

    private void updateMeal(List<DailyMeal> meals){
        DailyMealDao mealDao = AppDatabase.getDatabase(getApplicationContext()).dailyMealDao();
        for(DailyMeal meal : meals){
            mealDao.insert(meal);
        }
    }

    private void updateCTHB(List<CustomHabit> customHabits){
        CustomHabitDao customHabitDao = AppDatabase.getDatabase(getApplicationContext()).customHabitDao();
        for(CustomHabit habit : customHabits){
            customHabitDao.insert(habit);
        }
    }

    private void updateDLCTHB(List<DailyCustomHabit> dailyCustomHabits){
        DailyCustomHabitDao dailyCustomHabitDao = AppDatabase.getDatabase(getApplicationContext()).dailyCustomHabitDao();
        for(DailyCustomHabit dlhabit : dailyCustomHabits){
            dailyCustomHabitDao.insert(dlhabit);
        }
    }

    private void updateGoal(List<Goal> goals){
        GoalDao goalDao = AppDatabase.getDatabase(getApplicationContext()).goalDao();
        for(Goal goal : goals){
            goalDao.insert(goal);
        }
    }


    public static class ProfileActivity extends AppCompatActivity {

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


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", preferences.getString("username","null"));
                jsonObject.put("password", preferences.getString("password","null"));
                Log.d("PROFILE", preferences.getString("username","null") + " " + preferences.getString("password","null"));
                jsonObject.put("name", preferences.getString("name","null"));
                jsonObject.put("email", preferences.getString("email","null"));
                jsonObject.put("ngaysinh", preferences.getString("ngaysinh","null"));
                jsonObject.put("gioitinh", preferences.getInt("gioitinh",0));
                jsonObject.put("profile_pic", preferences.getString("avatar","null"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getApplicationContext().getString(R.string.server_domain) +  "myuser/update/patient/",jsonObject, r -> {

            }, e-> {

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + preferences.getString("access_token","null"));
                    return params;
                }
            };
            queue.add(jsonObjectRequest);

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
}