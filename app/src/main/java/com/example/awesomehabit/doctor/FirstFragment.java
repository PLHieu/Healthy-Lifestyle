package com.example.awesomehabit.doctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.awesomehabit.R;
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
import com.example.awesomehabit.database.user.User;
import com.example.awesomehabit.doctor.user.UserAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class FirstFragment extends Fragment implements UserAdapter.UserInterface {

    private static final String DOMAIN = "http://10.0.2.2:8000/";

    List<User> users=new ArrayList<>();
    UserAdapter userAdapter=new UserAdapter(users,this);

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv=view.findViewById(R.id.rvUserList);

        AppDatabase db=AppDatabase.getDatabase(this.getContext());
        db.userDao().getAllPerson().observe(this.getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> people) {
                users=people;
                userAdapter.setUsers(people);
            }
        });

        rv.setAdapter(userAdapter);

        rv.setLayoutManager(new LinearLayoutManager(this.getContext()));

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NavHostFragment.findNavController(FirstFragment.this)
                 //       .navigate(R.id.action_loginFragment_to_registerFragment);
//                Intent i = new Intent(getContext(),MainActivityDoctor.class);
//                startActivity(i);
            }
        });


    }

    @Override
    public void onUserClick(int position) {
        Log.d("sync", "Vao Click");

        String username  = users.get(position).username;
        SharedPreferences preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        preferences.edit().putString("onOpeningPatient", username).apply();

        try {
            Log.d("sync", "Vao pull");
            pullDB(username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void pullDB(String username) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,   DOMAIN + "sync/pull/", jsonObject,
                response -> {
                    Log.d("sync", "Response is: " + response);

                    // truoc khi pull ve phai clear het table
                    RunDao runDao = AppDatabase.getDatabase(getContext()).runDao();
                    DailyMealDao mealDao = AppDatabase.getDatabase(getContext()).dailyMealDao();
                    SleepDatabaseDao sleepDatabaseDao = AppDatabase.getDatabase(getContext()).sleepDao();
                    CustomHabitDao customHabitDao = AppDatabase.getDatabase(getContext()).customHabitDao();
                    DailyCustomHabitDao dailyCustomHabitDao = AppDatabase.getDatabase(getContext()).dailyCustomHabitDao();
                    GoalDao goalDao = AppDatabase.getDatabase(getContext()).goalDao();

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
                    Intent i = new Intent(getContext(),MainActivityDoctor.class);
                    startActivity(i);


                },
                error -> Log.d("sync", error.toString())){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
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
        RunDao runDao = AppDatabase.getDatabase(getContext()).runDao();
        for(Run run : runs){
            runDao.insertRun(run);
        }
    }

    private void updateSleep(List<SleepNight> sleepNights){
        SleepDatabaseDao sleepDatabaseDao = AppDatabase.getDatabase(getContext()).sleepDao();
        for(SleepNight sleepNight : sleepNights){
            sleepDatabaseDao.insert(sleepNight);
        }
    }

    private void updateMeal(List<DailyMeal> meals){
        DailyMealDao mealDao = AppDatabase.getDatabase(getContext()).dailyMealDao();
        for(DailyMeal meal : meals){
            mealDao.insert(meal);
        }
    }

    private void updateCTHB(List<CustomHabit> customHabits){
        CustomHabitDao customHabitDao = AppDatabase.getDatabase(getContext()).customHabitDao();
        for(CustomHabit habit : customHabits){
            customHabitDao.insert(habit);
        }
    }

    private void updateDLCTHB(List<DailyCustomHabit> dailyCustomHabits){
        DailyCustomHabitDao dailyCustomHabitDao = AppDatabase.getDatabase(getContext()).dailyCustomHabitDao();
        for(DailyCustomHabit dlhabit : dailyCustomHabits){
            dailyCustomHabitDao.insert(dlhabit);
        }
    }

    private void updateGoal(List<Goal> goals){
        GoalDao goalDao = AppDatabase.getDatabase(getContext()).goalDao();
        for(Goal goal : goals){
            goalDao.insert(goal);
        }
    }

}