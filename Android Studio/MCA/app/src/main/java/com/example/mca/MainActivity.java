package com.example.mca;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    // for debugging
    private static final String TAG = "MainActivity";

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";


    private EditText etUsername;
    private EditText etPassword;
    private String username;
    private String password;
    private ProgressDialog pDialog;
    private String login_url = "https://1c68af7e.ngrok.io/mca_members/login/login.php";
    private SessionHandler session;

    //http://192.168.43.72:8888/api/member/login.php

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionHandler(getApplicationContext());

        final DatabaseRequests myRequests = new DatabaseRequests();

        if(session.isLoggedIn()){
            String id = session.getUserDetails().get_member_id();
            int admin_lvl = myRequests.get_admin_lvl(id, getApplicationContext());
            if (admin_lvl == 0)
            {
                loadProfile(id);
                // load profile
            }
            else
            {
                loadAdminHomeActivity(id, admin_lvl);
            }



            //loadProfile();
            //loadDashboard();
        }
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.username_et);
        etPassword = findViewById(R.id.password_et);

        Button register = findViewById(R.id.register_btn);
        Button login = findViewById(R.id.login_btn);


        //Launch Registration screen when Register Button is clicked
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRegistration();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrieve the data entered in the edit texts
                username = etUsername.getText().toString().toLowerCase().trim();
                password = etPassword.getText().toString().trim();
                if (validateInputs()) {
                    login();
                    //myRequests.login(username, password, getApplicationContext());
                }
            }
        });
    }

    private void loadRegistration()
    {
        Intent i = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    private void loadProfile(String member_id) {
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        i.putExtra("member_id", member_id);
        startActivity(i);
        finish();
    }

    /**
     * Display Progress bar while Logging in
     */

    private void displayLoader() {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void login() {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, username);
            request.put(KEY_PASSWORD, password);
            Log.d(TAG, "Populated parameters for json request.");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response Received: " + response.toString());
                        pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully

                            if (response.getInt(KEY_STATUS) == 0) {
                                Log.d(TAG, "REACHED HERE");
                                Log.d(TAG, "Response received: " + response.toString());
                                session.loginUser(username);

                                int admin_lvl = response.getInt("admin_lvl");
                                Log.d(TAG, "Admin Level: " + admin_lvl);

                                if (admin_lvl != 0)
                                    loadAdminHomeActivity(username,admin_lvl);
                                else
                                    loadProfile(username);

                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    /**
     * Validates inputs and shows error if any
     * @return
     */
    private boolean validateInputs() {
        if(KEY_EMPTY.equals(username)){
            etUsername.setError("Username cannot be empty");
            etUsername.requestFocus();
            return false;
        }
        if(KEY_EMPTY.equals(password)){
            etPassword.setError("Password cannot be empty");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void loadAdminHomeActivity(String member_id, int admin_lvl)
    {
        Intent i = new Intent(getApplicationContext(), AdminHomeActivity.class);
        i.putExtra("admin_lvl", admin_lvl);
        i.putExtra("member_id", member_id);
        startActivity(i);
        finish();
    }

}
