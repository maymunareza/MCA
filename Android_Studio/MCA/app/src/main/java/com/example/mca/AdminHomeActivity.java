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

import static android.view.View.GONE;

public class AdminHomeActivity extends AppCompatActivity {

    private int admin_lvl;
    private String member_id;
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        session = new SessionHandler(getApplicationContext());


        // get member info
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            member_id = bundle.getString("member_id");
            admin_lvl = bundle.getInt("admin_lvl");
        }


        Button btnSearch = findViewById(R.id.btn_search);
        Button btnCreate = findViewById(R.id.btn_create);

        Button btnLogout = findViewById(R.id.btn_logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
                loadMainActivity();
            }
        });

        //Launch Registration screen when Register Button is clicked
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSearchActivity();
            }
        });

        if (admin_lvl == 1)
        {
            btnCreate.setVisibility(View.GONE);
        }
        else
        {

        }

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCreateActivity();
            }
        });
    }

    private void loadSearchActivity()
    {
        Intent i = new Intent(getApplicationContext(), SearchMembersActivity.class);
        i.putExtra("admin_lvl", admin_lvl);
        startActivity(i);
    }

    private void loadCreateActivity()
    {

    }

    private void loadMainActivity()
    {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }


}
