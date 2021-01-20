package com.example.awesomehabit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassActivity extends AppCompatActivity {
    String password = "";

    EditText edtCurPass;
    EditText edtNewPass;
    EditText edtReNewPass;

    Button btnUpdate;

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        _context = getBaseContext();

        setContentView(R.layout.activity_change_pass);

        edtCurPass = findViewById(R.id.edtCurPassword);
        edtNewPass = findViewById(R.id.edtNewPassword);
        edtReNewPass = findViewById(R.id.edtReNewPassword);
        btnUpdate = findViewById(R.id.btnUpdatePassword);

        btnUpdate.setEnabled(false);
        btnUpdate.setOnClickListener(v -> {
            password = edtNewPass.getText().toString();
            onBackPressed();
        });

        edtCurPass.setOnFocusChangeListener((v, hasFocus) -> checkValid(v, hasFocus));
        edtReNewPass.setOnFocusChangeListener((v, hasFocus) -> checkValid(v, hasFocus));
        edtNewPass.setOnFocusChangeListener((v, hasFocus) -> checkValid(v, hasFocus));
    }

    private void checkValid(View v, boolean hasFocus) {
        if(!hasFocus) {
            if (!checkCurPassword()) {
                if(v.getId() == edtCurPass.getId())
                    Toast.makeText(_context, "You enter wrong password!", Toast.LENGTH_LONG).show();
                btnUpdate.setEnabled(false);
                return;
            }
            if (!checkNewPassValid()){
                if(v.getId() == edtNewPass.getId())
                    Toast.makeText(_context, "Please enter new pass again ", Toast.LENGTH_LONG).show();
                if(v.getId() == edtReNewPass.getId())
                    Toast.makeText(_context, "You didn't enter right pass", Toast.LENGTH_LONG).show();
                btnUpdate.setEnabled(false);
                return;
            }
            btnUpdate.setEnabled(true);
        }
    }

    private boolean checkNewPassValid() {
        if(edtReNewPass.getText().toString().equals(edtNewPass.getText().toString()))
            return true;
        return false;
    }

    private boolean checkCurPassword() {
        return true;
//        if(edtCurPass.getText().toString().equals(password))
//            return true;
//        return false;
    }
}