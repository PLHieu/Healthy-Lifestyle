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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity2 extends AppCompatActivity {
    private final String DOMAIN = getString(R.string.server_domain);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Button button = findViewById(R.id.btnLogin);
        button.setOnClickListener(v -> {
            try {
                setActionForLoginButton();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        button.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            EditText edtUserName = findViewById(R.id.edtUserName);
            EditText edtPassWord = findViewById(R.id.edtPassword);

            String userName = edtUserName.getText().toString();
            String passWord = edtPassWord.getText().toString();
            returnIntent.putExtra("userName", userName);
            returnIntent.putExtra("passWord", passWord);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Login", r.toString());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("userName", userName);
            returnIntent.putExtra("passWord", passWord);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        }, e-> {
            // Log.d(TAG, e.toString());
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        });
        queue.add(jsonObjectRequest);
    }



}