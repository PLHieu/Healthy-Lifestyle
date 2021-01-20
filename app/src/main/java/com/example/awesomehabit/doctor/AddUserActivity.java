package com.example.awesomehabit.doctor;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.user.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddUserActivity extends AppCompatActivity {
    ImageView ivAvatar;
    EditText editTextName;
    EditText editTextAddress;
    Button buttonAdd;
    Bitmap imageBitmap;
    private  String DOMAIN ;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ivAvatar.setImageBitmap(imageBitmap);
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 111;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        DOMAIN = getApplicationContext().getString(R.string.server_domain);

        ivAvatar=findViewById(R.id.ivAvatar);
        ivAvatar.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
        editTextName=findViewById(R.id.editTextTextPersonName);
        editTextAddress=findViewById(R.id.editTextTextPersonName2);
        imageBitmap=null;
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }
            }
        });

        buttonAdd=findViewById(R.id.btnCreatePatient);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageBitmap==null){
                    Snackbar.make(v, "Please take a picture of your patient", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    // Gui request them benh nhan
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username" , editTextName.getText().toString());
                        jsonObject.put("password", editTextAddress.getText().toString());
                        jsonObject.put("profile_pic", getStringFromBitmap(imageBitmap));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,   DOMAIN + "myuser/signup/patient/", jsonObject,
                            response -> {

                                Log.d("sync", "Response is: " + response);
                                Toast.makeText(getApplicationContext(), "Pull Sucessfully", Toast.LENGTH_LONG).show();

                                // lay respone va dong bo
                                String patientsjson = null;
                                try {
                                    patientsjson = response.getString("data" );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Gson gson = new Gson();
                                Type typeRun = new TypeToken<List<User>>(){}.getType();
                                List<User> patients = gson.fromJson(patientsjson, typeRun);
                                updatePatient(patients);

                            },
                            error -> Log.d("sync", error.toString())){

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", "Bearer " + preferences.getString("access_token","null"));
                            return params;
                        }
                    };
                    queue.add(jsonObjectRequest);

                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private void updatePatient(List<User> patients) {
        for(User u : patients){
            AppDatabase.getDatabase(AddUserActivity.this).userDao().insert(u);
        }
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }
}
