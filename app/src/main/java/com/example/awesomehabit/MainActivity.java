package com.example.awesomehabit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.mapboxsdk.Mapbox;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        private NavigationView navigationView;
//    private static final String DOMAIN = "http://192.168.178.35:8000/";
    private static final String DOMAIN = "https://sheltered-castle-82570.herokuapp.com/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main_navigation);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.action_home);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (navigationView.getCheckedItem().getItemId() != R.id.action_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.action_statistic:
                return false;
            case R.id.action_set_goal:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SetGoalFragment()).commit();
                break;
            case R.id.action_sync:
                startActivity(new Intent(this, test_sync_data.class));
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
}
