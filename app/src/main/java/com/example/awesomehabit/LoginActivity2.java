package com.example.awesomehabit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Button button = findViewById(R.id.btnLogin);
        button.setOnClickListener(v -> setActionForLoginButton());
    }

    private void setActionForLoginButton() {
        EditText edtUserName = findViewById(R.id.edtUserName);
        EditText edtPassWord = findViewById(R.id.edtPassword);

        String userName = edtUserName.getText().toString();
        String passWord = edtPassWord.getText().toString();

        if(checkUserNameAndPassword(userName, passWord)){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("userName", userName);
            returnIntent.putExtra("passWord", passWord);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else
            Toast.makeText(getBaseContext(), "Wrong username or password", Toast.LENGTH_LONG);
    }

    private boolean checkUserNameAndPassword(String userName, String passWord) {
        return true;
    }
}