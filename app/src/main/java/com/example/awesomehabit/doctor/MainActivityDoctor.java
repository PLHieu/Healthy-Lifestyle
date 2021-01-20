package com.example.awesomehabit.doctor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.awesomehabit.AccountInfo;
import com.example.awesomehabit.LoginActivity;
import com.example.awesomehabit.LoginActivity2;
import com.example.awesomehabit.ProfileActivity;
import com.example.awesomehabit.R;
import com.example.awesomehabit.SetGoalFragment;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.Goal;
import com.example.awesomehabit.database.Habit;
import com.example.awesomehabit.database.custom.CustomHabit;
import com.example.awesomehabit.database.custom.DailyCustomHabit;
import com.example.awesomehabit.database.meal.DailyMeal;
import com.example.awesomehabit.database.running.Run;
import com.example.awesomehabit.database.sleeping.SleepNight;
import com.example.awesomehabit.test_sync_data;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.mapboxsdk.Mapbox;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivityDoctor extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private DrawerLayout drawer;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        private NavigationView navigationView;
//    private static final String DOMAIN = "http://192.168.178.35:8000/";
    private String DOMAIN ;

    String userName = "";
    String password = "";
    Context _context;

    static int LOGIN_CODE = 1;
    Bundle bundle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;

        DOMAIN  = getString(R.string.server_domain);

        _context = getBaseContext();
        init();
        //Intent intent = new Intent(this, LoginActivity2.class);
        //startActivityForResult(intent, LOGIN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LOGIN_CODE){
            if(resultCode == Activity.RESULT_OK){
                userName = data.getStringExtra("userName");
                password = data.getStringExtra("passWord");
                init();
            }
        }
    }

    private void init() {
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main_navigation_doctor);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment_Doctor()).commit();
            navigationView.setCheckedItem(R.id.action_home);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (navigationView.getCheckedItem().getItemId() != R.id.action_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment_Doctor()).commit();
            navigationView.setCheckedItem(R.id.action_home);
        }
        else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment_Doctor()).commit();
                break;
            case R.id.action_statistic:
                return false;
            case R.id.action_set_goal:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SetGoalFragment()).commit();
                break;
            case R.id.action_sync:
                try {
                    doctorpushDB();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.action_login:
                SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                String token = preferences.getString("access_token",null);
                long access_expires = preferences.getLong("access_expires",0);
                long refresh_expires = preferences.getLong("refresh_expires",0);
                long lastloggedin = preferences.getLong("lastloggedin",0);
                long currentime = System.currentTimeMillis()/1000;

                if(token == null ||token.equals("null")){
                    startActivity(new Intent(this, LoginActivity.class));
                }else if( access_expires + lastloggedin > currentime ){
                    startActivity(new Intent(this, AccountInfo.class));

                }else if(refresh_expires + lastloggedin > currentime ){

                    startActivity(new Intent(this, AccountInfo.class));
                    RequestQueue queue = Volley.newRequestQueue(this);
                    JSONObject object = new JSONObject();
                    try {
                        object.put("refresh", preferences.getString("refresh_token", "null"));
                    } catch (JSONException e) { e.printStackTrace(); }

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,DOMAIN +  "api/token/refresh/",object, r -> {
                        Log.d("MainActivity", r.toString());
                        try {
                            preferences.edit().putString("access_token", r.getString("access")).apply(); } catch (JSONException e) { e.printStackTrace(); }
                    }, e-> {
                        Log.d("MainActivity", e.toString());
                    });

                    queue.add(request);


                }else if (refresh_expires + lastloggedin <= currentime ){
                    // todo khoi dong login nhung ban qua account voi mot flag
                    Intent intent = new Intent(this, AccountInfo.class);
                    intent.putExtra("expired", true);
                    startActivity(intent);

                }

                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imgViewUserIcon){
            startActivity(new Intent(_context, ProfileActivity.class));
        }
    }

    private void doctorpushDB() throws JSONException {

        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String username = preferences.getString("onOpeningPatient", null);

        if(username == null){
            Toast.makeText(this, "Ban chua cho mot benh nhan cu the", Toast.LENGTH_LONG).show();
        }

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
        jsonObject.put("username", username);
        jsonObject.put("visiRun", Habit.RUN_AVAILABLE);
        jsonObject.put("visiSleep", Habit.SLEEP_AVAILABLE);
        jsonObject.put("visiMeal", Habit.MEAL_AVAILABLE);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,   DOMAIN + "sync/doctorpush/",jsonObject,
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


}
