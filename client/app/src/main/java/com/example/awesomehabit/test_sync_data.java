package com.example.awesomehabit;

import androidx.appcompat.app.AppCompatActivity;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import com.example.awesomehabit.meal.Meal;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class test_sync_data extends AppCompatActivity {

    Button  btnPush, btnPull;
    private final String TAG = "test_sync_data";
//    private static final String DOMAIN = "https://sheltered-castle-82570.herokuapp.com/";
    private final String DOMAIN = getString(R.string.server_domain);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sync_data);

        btnPull = findViewById(R.id.btn_pull);
        btnPush = findViewById(R.id.btn_push);

        btnPull.setOnClickListener(v -> {
            try {
                pullDB("patient4");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        btnPush.setOnClickListener(v -> {
            try {

                pushDB();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }


    private void pushDB() throws JSONException {
        List<Run> runs = AppDatabase.getDatabase(getApplicationContext()).runDao().getOutdated();
        List<SleepNight> sleepNights = AppDatabase.getDatabase(getApplicationContext()).sleepDao().getOutdated();
        List<DailyMeal> meals = AppDatabase.getDatabase(getApplicationContext()).dailyMealDao().getOutdated();
        List<CustomHabit> customHabits = AppDatabase.getDatabase(getApplicationContext()).customHabitDao().getOutdated();
        List<DailyCustomHabit> dailyCustomHabits = AppDatabase.getDatabase(getApplicationContext()).dailyCustomHabitDao().getOutdated();
        List<Goal> goals = AppDatabase.getDatabase(getApplicationContext()).goalDao().getAllGoal();

        Gson gson = new Gson();
        Type typeRun = new TypeToken<List<Run>>(){}.getType();
        Type typeSleep = new TypeToken<List<SleepNight>>(){}.getType();
        Type typeDailyMeal = new TypeToken<List<DailyMeal>>(){}.getType();
        Type typeCustomHB = new TypeToken<List<CustomHabit>>(){}.getType();
        Type typeDailyCustomHB = new TypeToken<List<DailyCustomHabit>>(){}.getType();
        Type typeGoal = new TypeToken<List<Goal>>(){}.getType();

        String runjson = gson.toJson(runs, typeRun);
        String sleepjson = gson.toJson(sleepNights, typeSleep);
        String dailymealjson = gson.toJson(meals, typeDailyMeal);
        String customhbjson = gson.toJson(customHabits, typeCustomHB);
        String dlcustomhbjson = gson.toJson(dailyCustomHabits, typeDailyCustomHB);
        String goaljson = gson.toJson(goals, typeGoal);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("run", runjson);
        jsonObject.put("sleep", sleepjson);
        jsonObject.put("dailymeal", dailymealjson);
        jsonObject.put("customHB", customhbjson);
        jsonObject.put("dailycustomHB", dlcustomhbjson);
        jsonObject.put("goal", goaljson);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,   DOMAIN + "sync/push/",jsonObject,
            response -> {

                // push thanh cong thi bat cac truong da push len 1
                AppDatabase.getDatabase(getApplicationContext()).runDao().updateAll();
                AppDatabase.getDatabase(getApplicationContext()).sleepDao().updateAll();
                AppDatabase.getDatabase(getApplicationContext()).dailyMealDao().updateAll();
                AppDatabase.getDatabase(getApplicationContext()).customHabitDao().updateAll();
                AppDatabase.getDatabase(getApplicationContext()).dailyCustomHabitDao().updateAll();

                Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show();
                Log.d("sync", "Response is: " + response);
            },
            error -> {
                Log.d("sync", error.toString());
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + preferences.getString("access_token","null"));
                return params;
            }
        };
        queue.add(request);
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