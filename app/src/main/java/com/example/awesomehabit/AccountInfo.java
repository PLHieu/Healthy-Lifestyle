package com.example.awesomehabit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AccountInfo extends AppCompatActivity {

    TextView name, email;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        initView();

        logout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
            preferences.edit().putString("access_token", "null").apply();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });


        Boolean expired = getIntent().getBooleanExtra("expired", false);
        if (expired){
            new AlertDialog.Builder(this)
                    .setMessage("Phien dang nhap het hieu luc !!")
                    .setPositiveButton("OK", (dialog, which) -> startActivity(new Intent(this, LoginActivity.class)))
                    .setNegativeButton("Cancel", (dialog, which) -> startActivity(new Intent(this, LoginActivity.class)))
                    .show();
        }

    }

    private void initView() {
        name = findViewById(R.id.info_name);
        email = findViewById(R.id.info_email);
        logout = findViewById(R.id.info_logout);

        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String sp_name = preferences.getString("name","Guest");
        String sp_email = preferences.getString("email","Guest@email.com");

        name.setText(sp_name);
        email.setText(sp_email);
    }


}