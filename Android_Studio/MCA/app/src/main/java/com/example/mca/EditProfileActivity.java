package com.example.mca;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileActivity extends AppCompatActivity {

    private static final String KEY_MEMBER_ID = "member_id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_MID_NAME = "mid_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_STREET_ADR = "street_address";
    private static final String KEY_ADR_PT2 = "address_pt2";
    private static final String KEY_CITY = "city";
    private static final String KEY_ZIP_CODE = "zip_code";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_HOME_NUM = "home_num";
    private static final String KEY_CELL_NUM = "cell_num";

    private EditText etStreetAdr;
    private EditText etStreetAdrPt2;
    private EditText etCity;
    private EditText etZipCode;
    private EditText etEmail;
    private EditText etHomeNum;
    private EditText etCellNum;

    private String street_address;
    private String address_pt2;
    private String city;
    private String zip_code;
    private String email;
    private String home_num;
    private String cell_num;

    private static final String TAG = "EditProfileActivity";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    private ProgressDialog pDialog;

    //private JSONObject member_info;
    private String member_id;

    private TextView tvFullName;
    private TextView tvMemberID;
    private String update_url = "https://1c68af7e.ngrok.io/mca_members/login/update.php";
    private String read_member_url = "https://1c68af7e.ngrok.io/mca_members/login/read_member.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // get member info
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            member_id = bundle.getString(KEY_MEMBER_ID);
            /*
            try {
                member_info = new JSONObject(response);
                Log.d(TAG, "Received: " + member_info.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }*/

        }

        tvFullName = findViewById(R.id.tv_full_name);
        tvMemberID = findViewById(R.id.tv_membership_id);

        etStreetAdr = findViewById(R.id.et_street_adr);
        etStreetAdrPt2 = findViewById(R.id.et_street_adr_pt2);
        etCity = findViewById(R.id.et_city);
        etZipCode = findViewById(R.id.et_zip_code);
        etEmail = findViewById(R.id.et_email);
        etHomeNum = findViewById(R.id.et_home_num);
        etCellNum = findViewById(R.id.et_cell_num);

        read_member_data();


        Button save_btn = findViewById(R.id.btn_save);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                street_address = etStreetAdr.getText().toString().trim();
                address_pt2 = etStreetAdrPt2.getText().toString().trim();
                city = etCity.getText().toString().trim();
                zip_code = etZipCode.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                home_num = etHomeNum.getText().toString().trim();
                cell_num = etCellNum.getText().toString().trim();

                update_info();
                finish();
            }
        });


    }

    private void displayLoader() {
        pDialog = new ProgressDialog(EditProfileActivity.this);
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void update_info()
    {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, member_id);
            request.put(KEY_STREET_ADR, street_address);
            request.put(KEY_ADR_PT2, address_pt2);
            request.put(KEY_CITY, city);
            request.put(KEY_ZIP_CODE, zip_code);
            request.put(KEY_EMAIL, email);
            request.put(KEY_HOME_NUM, home_num);
            request.put(KEY_CELL_NUM, cell_num);
            Log.d(TAG, "Populated parameters for json request.");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, update_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response Received: " + response.toString());
                        pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully

                            if (response.getInt(KEY_STATUS) == 0) {
                                Log.d(TAG, "REACHED HERE");
                                //session.loginUser(username,response.getString(KEY_FULL_NAME));
                                //loadDashboard();

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

    private void set_member_info(JSONObject member_info)
    {
        String full_name, member_id, address, email, home_num, cell_num;

        try {
            full_name = member_info.getString(KEY_FIRST_NAME) + " "
                    + member_info.getString(KEY_MID_NAME) + " "
                    + member_info.getString(KEY_LAST_NAME);

            member_id = member_info.getString(KEY_MEMBER_ID);

            tvFullName.setText(full_name);
            tvMemberID.setText(member_id);

            etStreetAdr.setText(member_info.getString(KEY_STREET_ADR));
            etStreetAdrPt2.setText(member_info.getString(KEY_ADR_PT2));
            etCity.setText(member_info.getString(KEY_CITY));
            etZipCode.setText(member_info.getString(KEY_ZIP_CODE));
            etEmail.setText(member_info.getString(KEY_EMAIL));
            etHomeNum.setText(member_info.getString(KEY_HOME_NUM));
            etCellNum.setText(member_info.getString(KEY_CELL_NUM));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void read_member_data() {
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put("username", member_id);
            request.put("password", "nothanks");
            Log.d(TAG, "Populated parameters for json request.");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, read_member_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response Received: " + response.toString());
                        try {
                            //Check if user got logged in successfully

                            if (response.getInt(KEY_STATUS) == 0) {
                                Log.d(TAG, "REACHED HERE");
                                Log.d(TAG, "Response received: " + response.toString());
                                //session.loginUser(username,response.getString(KEY_FULL_NAME));
                                set_member_info(response);
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

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }


}
