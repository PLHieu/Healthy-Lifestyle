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
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.awesomehabit.patient.LoginActivity2;
import com.example.awesomehabit.statistic.StatisticFragment;
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
    Context _context;
    SharedPreferences preferences;
    static int LOGIN_CODE = 1;
    Bundle bundle;

    ImageView imageView;
    TextView txtViewName;
    TextView txtViewMail;

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

                init();
            }
        }
    }

    private void init() {
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main_navigation_doctor);

        preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view_doctor);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        imageView = view.findViewById(R.id.imgViewUserIcon);
        txtViewMail = view.findViewById(R.id.txtViewUserMail);
        txtViewName = view.findViewById(R.id.txtViewUserName);
        updateData();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment_Doctor()).commit();
            navigationView.setCheckedItem(R.id.action_home);
        }
    }

    private void updateData() {
        if(preferences.getString("avatar", null) != null)
            imageView.setImageBitmap(LoginActivity2.ProfileActivity.StringToBitMap(preferences.getString("avatar", null)));
        txtViewName.setText(preferences.getString("username", "guest"));
        txtViewMail.setText(preferences.getString("email", "guest@gmail.com"));
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StatisticFragment()).commit();
                break;
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

            case R.id.action_logout:
                deleteData();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imgViewUserIcon){
            startActivity(new Intent(_context, LoginActivity2.ProfileActivity.class));
        }
    }

    private void doctorpushDB() throws JSONException {

        preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
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

                    Toast.makeText(this, "Upload d??? li???u th??nh c??ng", Toast.LENGTH_LONG).show();
                    Log.d("sync", "Response is: " + response);
                },
                error -> {
                    Log.d("sync", error.toString());
                    Toast.makeText(this, "???? c?? l???i x???y ra", Toast.LENGTH_LONG).show();
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

    private void deleteData() {

        preferences.edit().clear().commit();
    }
}
