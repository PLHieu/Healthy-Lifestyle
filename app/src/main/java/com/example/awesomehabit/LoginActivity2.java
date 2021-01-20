package com.example.awesomehabit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.Goal;
import com.example.awesomehabit.database.GoalDao;
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
import com.example.awesomehabit.doctor.MainActivityDoctor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
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
        DOMAIN = getString(R.string.server_domain);

        DOMAIN = getString(R.string.server_domain);

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
//            Intent returnIntent = new Intent();
//            EditText edtUserName = findViewById(R.id.edtUserName);
//            EditText edtPassWord = findViewById(R.id.edtPassword);
//
//            String userName = edtUserName.getText().toString();
//            String passWord = edtPassWord.getText().toString();
//
//            //test
//            preferences.edit().putString("username", userName).apply();
//            preferences.edit().putString("email","hello@gmail.com").apply();
//            preferences.edit().putString("name", "Tran Tuan Dat").apply();
//            preferences.edit().putString("diachi", "227 Nguyen Van Cu, District 5, HCMC").apply();
//            preferences.edit().putInt("gioitinh", 0).apply();
//            preferences.edit().putString("ngaysinh", "09/08/2000").apply();
//            preferences.edit().putString("avatar", null);
//            //test
//            setResult(Activity.RESULT_OK, returnIntent);
//            finish();
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
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,DOMAIN +  "myuser/signin/",jsonObject, r -> {
            SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
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
                Toast.makeText(getBaseContext(), "Login success", Toast.LENGTH_LONG).show();

                pullDB(preferences.getString("username", null));



            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Login", r.toString());
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        }, e-> {
            // Log.d(TAG, e.toString());
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        });
        queue.add(jsonObjectRequest);
    }


    private void pullDB(String username) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,   DOMAIN + "sync/pull/", jsonObject,
                response -> {
                    Log.d("sync", "Response is: " + response);

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

                    // khoi dong main activity sau khi update xong
                    Intent i = new Intent(getApplicationContext(), MainActivityDoctor.class);
                    startActivity(i);


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


}