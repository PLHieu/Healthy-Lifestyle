package com.example.awesomehabit.doctor;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.awesomehabit.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String DOMAIN;
    private static final String TAG = "RegisterFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.registerName) EditText _emailText;
    @BindView(R.id.registerPassword) EditText _passwordText;
    @BindView(R.id.registerButton) Button _loginButton;
    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view=inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.goToSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(RegisterFragment.this).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });

        _emailText=view.findViewById(R.id.registerName);
        _passwordText=view.findViewById(R.id.registerPassword);
        _loginButton=view.findViewById(R.id.registerButton);

        _loginButton.setOnClickListener(v -> {
            try {
                signup();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void signup() throws JSONException {
        DOMAIN=getString(R.string.server_domain);
        if (!validate()) {
            onSignupFailed();
            return;
        }
        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", name);
        jsonObject.put("password",password);
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,DOMAIN + "myuser/signup/doctor/",jsonObject, r -> {
            SharedPreferences preferences = this.getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
            try {
                preferences.edit().putString("username", name).apply();
                preferences.edit().putString("refresh_token", r.getString("refresh_token")).apply();
                preferences.edit().putString("access_token", r.getString("access_token")).apply();
                preferences.edit().putLong("access_expires", Long.parseLong(r.getString("access_expires"))).apply();
                preferences.edit().putLong("refresh_expires", Long.parseLong(r.getString("refresh_expires"))).apply();
                preferences.edit().putLong("lastloggedin", Long.parseLong(String.valueOf(System.currentTimeMillis()/1000))).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            onSignupSuccess();
        }, e-> {
            Log.d(TAG, e.toString());
            onSignupFailed();
        });
        queue.add(jsonObjectRequest);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
//                        onSignupSuccess();
                        // onSignupFailed();
                        if(progressDialog!=null)
                            progressDialog.dismiss();
                    }
                }, 3000);
    }

    private void onSignupSuccess() {
        _emailText.setEnabled(true);
        this.getActivity().setResult(RESULT_OK, null);
        Intent intent = new Intent(this.getContext(), MainActivity.class);
        startActivity(intent);
    }

    public boolean validate() {
        return  true;
    }
    public void onSignupFailed() {
        Toast.makeText(this.getContext(), "Sign up failed", Toast.LENGTH_LONG).show();
        _emailText.setEnabled(true);
    }
}