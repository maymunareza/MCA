package com.example.mca;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PayActivity extends AppCompatActivity {

    private static final String TAG = "PayActivity";

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    private ProgressDialog pDialog;
    private String register_url = "https://1c68af7e.ngrok.io/mca_members/login/register.php";
    private String signUp_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        // get member info
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            signUp_info = bundle.getString("response");
        }

        Button register_btn = findViewById(R.id.btn_register);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
                loadMainActivity();

            }
        });

    }

    private void loadMainActivity() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(PayActivity.this);
        pDialog.setMessage("Signing Up.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }


    private void registerUser() {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request = new JSONObject(signUp_info);

            Log.d(TAG, "populated request parameters: " + request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, register_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d(TAG, "Response returned: " + response.toString());
                        try {
                            //Check if user got registered successfully
                            if (response.getInt(KEY_STATUS) == 0) {
                                //Set the user session
                                //session.loginUser(username,fullName);
                                //loadDashboard();
                                Log.d(TAG, "Successfully registered");


                            }else if(response.getInt(KEY_STATUS) == 1){
                                //Display error message if username is already existsing
                                Log.d(TAG, "user exists");

                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "idk");


                            }
                        } catch (JSONException e) {
                            Log.d(TAG, "HERE");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                        String errMsg = error.getMessage();
                        if (errMsg != null)
                            Log.d(TAG, errMsg);
                        else
                            Log.d(TAG, "Error: ");


                    }
                });


        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
}
