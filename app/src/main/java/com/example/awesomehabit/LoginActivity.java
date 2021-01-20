package com.example.awesomehabit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String DOMAIN = "https://sheltered-castle-82570.herokuapp.com/";
//    private static final String DOMAIN = "http://192.168.178.35:8000/";



    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    Button _btn_guest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);

        initView();

        _loginButton.setOnClickListener(v -> {
            try {
                login();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        _signupLink.setOnClickListener(v -> {
            // Start the Signup activity
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
        });

        _btn_guest.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    private void initView() {
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);
        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _btn_guest = findViewById(R.id.btn_guest);
    }

    public void login() throws JSONException {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
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
                onLoginSuccess();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, r.toString());

        }, e-> {
            // Log.d(TAG, e.toString());
            onLoginFailed();
        });
        queue.add(jsonObjectRequest);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}
