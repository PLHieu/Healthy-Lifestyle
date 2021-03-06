package com.example.awesomehabit.doctor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.user.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private static final int REQUEST_SIGNUP = 0;
    private  String DOMAIN;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    ProgressDialog progressDialog;
    @BindView(R.id.loginName) EditText _emailText;
    @BindView(R.id.loginPassword) EditText _passwordText;
    @BindView(R.id.LoginButton) Button _loginButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this,view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.goToSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

        _emailText=view.findViewById(R.id.loginName);
        _passwordText=view.findViewById(R.id.loginPassword);
        _loginButton=view.findViewById(R.id.LoginButton);

        _loginButton.setOnClickListener(v -> {
            try {
                login();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }

    private void login() throws JSONException{
        DOMAIN=getString(R.string.server_domain) ;

        progressDialog = new ProgressDialog(this.getContext());
        if (!validate()) {
            onLoginFailed(progressDialog);
            return;
        }

        _loginButton.setEnabled(false);


        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", email);
        jsonObject.put("password", password);
        SharedPreferences preferences = this.getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
        preferences.edit().putString("password", password).apply();
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,DOMAIN +  "myuser/signin/",jsonObject, r -> {
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
                try {
                    preferences.edit().putString("avatar", r.getString("profile_pic")).apply();
                } catch (JSONException ignored) { }

                // dang nhap thanh cong thi xoa db user roi  pull ve danh sach benh nhan
                AppDatabase.getDatabase(getContext()).userDao().deleteAll();
                pulldanhsachbenhnhan();

                onLoginSuccess();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, r.toString());

        }, e-> {
            Log.d(TAG, e.toString());
            onLoginFailed(progressDialog);
        });
        queue.add(jsonObjectRequest);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(progressDialog!=null)
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    private void updatePatient(List<User> patients) {
        for(User u : patients){
            AppDatabase.getDatabase(getContext()).userDao().insert(u);
        }
    }

    private void pulldanhsachbenhnhan() {
        // Gui request them benh nhan
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,   DOMAIN + "sync/listpatient/", null,
                response -> {

                    Log.d("sync", "Response is: " + response);

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
                SharedPreferences preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + preferences.getString("access_token","null"));
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null)
        progressDialog.dismiss();
    }

    private void onLoginSuccess() {
        _loginButton.setEnabled(true);

        Intent returnIntent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }

    public void onLoginFailed(ProgressDialog progressDialog) {
        Toast.makeText(this.getContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    private boolean validate() {
        return true;
    }
}