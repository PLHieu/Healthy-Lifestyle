package com.example.awesomehabit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.example.awesomehabit.database.custom.CustomHabit;
import com.example.awesomehabit.database.custom.DailyCustomHabit;
import com.example.awesomehabit.database.meal.DailyMeal;
import com.example.awesomehabit.database.running.Run;
import com.example.awesomehabit.database.sleeping.SleepNight;
import com.example.awesomehabit.meal.Meal;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test_sync_data extends AppCompatActivity {

    Button btn;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    static int RC_SIGN_IN = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sync_data);

        btn = findViewById(R.id.sync);
        signInButton = findViewById(R.id.sign_in_button);

        btn.setOnClickListener(v -> {
            pushAllSleep();

        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!= null){

        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

    }

    private void signin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("sync", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    private void pushRun() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);

        Run run = AppDatabase.getDatabase(this).runDao().gettestRun();
        String urlRunTemplate ="http://192.168.178.35:8000/run/add/?userid=%s&"
                + "starttime=%s&"
                + "distance=%d&"
                +"runningtime=%d&"
                +"routeID=%s&"
                +"goal=%s&";
        String urlRun = String.format(urlRunTemplate, "1", run.timeStart, run.distance, run.runningTime, run.routeID, run.target);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,  "http://192.168.178.35:8000/run/add/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sync", "Response is: " + response);
            }
        }, error -> Log.d("sync", error.toString())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", "1");
                params.put("starttime", run.timeStart);
                params.put("distance", String.valueOf(run.distance) );
                params.put("routeID", String.valueOf(run.routeID) );
                params.put("goal", String.valueOf(run.target) );

                return params;
            }
        };

        queue.add(stringRequest);
    }
    private void pushAllRun(){
        RequestQueue queue = Volley.newRequestQueue(this);

        List<Run> runs = AppDatabase.getDatabase(this).runDao().getAllRun();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,  "http://192.168.178.35:8000/run/push/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sync", "Response is: " + response);
            }
        }, error -> Log.d("sync", error.toString())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>(); //todo them user id
                params.put("userid", "1");

                Map<String, String> data = new HashMap<String, String>();

                try{
                    JSONArray jsonArray = new JSONArray() ;
                    for(int i = 0; i< runs.size(); i++){
                        jsonArray.put(runtoJSON(runs.get(i)));
                    }


                    params.put("data", jsonArray.toString());

                }catch (JSONException e) {
                    Log.d("sync", e.getMessage());
                }
                return params;
            }
        };

        queue.add(stringRequest);
    }
    private void pushAllSleep(){
        RequestQueue queue = Volley.newRequestQueue(this);

        List<SleepNight> sleeps = AppDatabase.getDatabase(this).sleepDao().getAllNightsNonLive();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,  "http://192.168.178.35:8000/sleep/push/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sync", "Response is: " + response);
            }
        }, error -> Log.d("sync", error.toString())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", "1");

                Map<String, String> data = new HashMap<String, String>();

                try{
                    JSONArray jsonArray = new JSONArray() ;
                    for(int i = 0; i< sleeps.size(); i++){
                        jsonArray.put(sleeptoJSON(sleeps.get(i)));
                    }


                    params.put("data", jsonArray.toString());

                }catch (JSONException e) {
                    Log.d("sync", e.getMessage());
                }
                return params;
            }
        };

        queue.add(stringRequest);
    }
    private void pushCustomHabit(){
        RequestQueue queue = Volley.newRequestQueue(this);

        List<CustomHabit> customHabits = AppDatabase.getDatabase(this).customHabitDao().getAllNone();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,  "http://192.168.178.35:8000/customhabit/pushhb/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sync", "Response is: " + response);
            }
        }, error -> Log.d("sync", error.toString())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", "1");

                Map<String, String> data = new HashMap<String, String>();

                try{
                    JSONArray jsonArray = new JSONArray() ;
                    for(int i = 0; i< customHabits.size(); i++){
                        jsonArray.put(customHabittoJSON(customHabits.get(i)));
                    }

                    params.put("data", jsonArray.toString());

                }catch (JSONException e) {
                    Log.d("sync", e.getMessage());
                }
                return params;
            }
        };

        queue.add(stringRequest);
    }
    private void pushdailyCustomHabit(){
        RequestQueue queue = Volley.newRequestQueue(this);
        List<DailyCustomHabit> dailycustomHabits = AppDatabase.getDatabase(this).dailyCustomHabitDao().getAllHabitNone();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,  "http://192.168.178.35:8000/customhabit/pushhbtrack/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sync", "Response is: " + response);
            }
        }, error -> Log.d("sync", error.toString())){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", "1");

                Map<String, String> data = new HashMap<String, String>();

                try{
                    JSONArray jsonArray = new JSONArray() ;
                    for(int i = 0; i< dailycustomHabits.size(); i++){
                        jsonArray.put(dailycustomHabittoJSON(dailycustomHabits.get(i)));
                    }

                    params.put("data", jsonArray.toString());

                }catch (JSONException e) {
                    Log.d("sync", e.getMessage());
                }
                return params;
            }
        };

        queue.add(stringRequest);
    }


    public JSONObject runtoJSON(Run run) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("starttime", run.timeStart);
        jo.put("distance", String.valueOf(run.distance) );
        jo.put("runningtime", String.valueOf(run.runningTime) );
        jo.put("routeID", String.valueOf(run.routeID) );
        jo.put("goal", String.valueOf(run.target) );
        return jo;
    }
    public JSONObject sleeptoJSON(SleepNight sleep) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("starttime", sleep.getStartTimeMilli());
        jo.put("endtime", String.valueOf(sleep.getEndTimeMilli()) );
        jo.put("quality", String.valueOf(sleep.getSleepQuality()) );
        jo.put("goal", String.valueOf(sleep.target) );
        return jo;
    }
    public JSONObject customHabittoJSON(CustomHabit customHabit) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("habitID", customHabit.HabitID);
        jo.put("name", customHabit.name );
        jo.put("type", customHabit.type );
        return jo;
    }
    public JSONObject dailycustomHabittoJSON(DailyCustomHabit dailyCustomHabit) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("habitID", dailyCustomHabit.HabitID);
        jo.put("target", dailyCustomHabit.target );
        jo.put("current", dailyCustomHabit.current );
        jo.put("time", String.valueOf(dailyCustomHabit.time.getTimeInMillis()));
        return jo;
    }
    //todo: xu li daily meal
        /*public JSONObject foodtoJSON(DailyMeal meal)  throws JSONException {
            JSONObject jo = new JSONObject();
            jo.put("starttime", meal.gettime());
            jo.put("calo", String.valueOf(meal.getCalories()) );
            return jo;
        }*/


    private void pullRun(){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, "http://192.168.178.35:8000/run/allrun/", null,
                response -> {
                    Log.d("sync", "Response is: " + response.toString());
                    try {
                        updateRunDataBase(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("sync", error.toString())
        );

        queue.add(jsonObjectRequest);
    }
    private void updateRunDataBase(JSONArray runJSResponse) throws JSONException {
        int n = runJSResponse.length();
        for (int i =0; i< n; i++){

            JSONObject jsrun = runJSResponse.getJSONObject(i);
            Run run = parseRunJS(jsrun);
            AppDatabase.getDatabase(this).runDao().update(run);

        }
    }
    private Run parseRunJS(JSONObject jsrun) throws JSONException {
        Long timeMillis = jsrun.getLong("timestart");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return new Run(jsrun.getInt("distance"),
                jsrun.getString("timestart"),
                calendar,
                jsrun.getLong("runningtime"),
                jsrun.getString("rouid")
                );
    }

    private void pullSleep(){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, "http://192.168.178.35:8000/sleep/allsleep", null,
                response -> {
                    Log.d("sync", "Response is: " + response.toString());
                    try {
                        updateSleepDataBase(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("sync", error.toString())
        );

        queue.add(jsonObjectRequest);
    }
    private void updateSleepDataBase(JSONArray sleepJSResponse) throws JSONException {
        int n = sleepJSResponse.length();
        for (int i =0; i< n; i++){
            JSONObject jssleep = sleepJSResponse.getJSONObject(i);
            SleepNight sleep = parseSleepJS(jssleep);
            AppDatabase.getDatabase(this).sleepDao().update(sleep);
        }
    }
    private SleepNight parseSleepJS(JSONObject jssleep) throws JSONException {
        Long timeMillis = jssleep.getLong("starttime");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return new SleepNight(calendar,
                timeMillis,
                jssleep.getLong("endtime"),
                jssleep.getInt("quality")
        );
    }



    //todo: xu li meal
    /*private void pullMeal(){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, "http://192.168.178.35:8000/food/allfood/", null,
                response -> {
                    Log.d("sync", "Response is: " + response.toString());
                    try {
                        updateMealDataBase(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("sync", error.toString())
        );

        queue.add(jsonObjectRequest);
    }
    private void updateMealDataBase(JSONArray mealJSResponse) throws JSONException {
        int n = mealJSResponse.length();
        for (int i =0; i< n; i++){

            JSONObject jsmeal = mealJSResponse.getJSONObject(i);
            Meal meal = parseRunJS(jsmeal);
            AppDatabase.getDatabase(this).mealDao().update(meal);

        }
    }
    private Run parseMealJS(JSONObject jsrun) throws JSONException {
        Long timeMillis = jsrun.getLong("timestart");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return new Run(jsrun.getInt("distance"),
                jsrun.getString("timestart"),
                calendar,
                jsrun.getLong("runningtime"),
                jsrun.getString("rouid")
        );
    }*/

    /*private CustomHabit parseCustomHabitJS(JSONObject jssleep) throws JSONException {
        Long timeMillis = jssleep.getLong("starttime");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return new CustomHabit(na,
                timeMillis,
                jssleep.getLong("endtime"),
                jssleep.getInt("quality")
        );
    }*/

}