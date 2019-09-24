package com.example.mca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminsMemberViewActivity extends AppCompatActivity {

    private static final String TAG = "AdminsMemberViewAct";

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
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
    private static final String KEY_EMPTY = "";

    private String read_member_url = "https://1c68af7e.ngrok.io/mca_members/login/read_member.php";
    private String read_member_history_url = "https://1c68af7e.ngrok.io/mca_members/login/read_history.php";


    private String member_id;

    private TextView tvFullName;
    private TextView tvMemberID;
    private TextView tvAddress;
    private TextView tvEmail;
    private TextView tvPhoneNum;

    private Button btnDelete;
    private Button btnEdit;

    private ListView lvHistory;

    int admin_lvl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins_member_view);

        // get member id
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            member_id = bundle.getString("member_id");
            admin_lvl = bundle.getInt("admin_lvl");
            Log.d(TAG, "Preparing to load admin view of profile: " + member_id);
        }

        // fill member data
        tvFullName = findViewById(R.id.tv_full_name);
        tvMemberID = findViewById(R.id.tv_membership_id);
        tvAddress = findViewById(R.id.tv_m_address);
        tvEmail = findViewById(R.id.tv_m_email);
        tvPhoneNum = findViewById(R.id.tv_m_phone);

        btnDelete = findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseRequests db_request = new DatabaseRequests();
                db_request.delete_member(member_id, getApplicationContext());
            }
        });

        if (admin_lvl == 1)
        {
            btnDelete.setVisibility(View.GONE);
        }

        btnEdit = findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEditProfileActivity();
            }
        });



        read_member_data();

        // get history list
        lvHistory = findViewById(R.id.lv_history);
        read_member_history();


    }

    private void loadEditProfileActivity()
    {
        Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
        i.putExtra(KEY_MEMBER_ID, member_id);
        startActivity(i);
    }

    private void set_member_history(ArrayList<String> hist)
    {
        // Initializing a new String Array
        String[] fruits = new String[] {
                "Cape Gooseberry",
                "Capuli cherry"
        };

        Log.d(TAG, "Displaying history Results");

        // Create a List from String Array elements
        final List<String> fruits_list = new ArrayList<String>(Arrays.asList(fruits));

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, hist);

        // DataBind ListView with items from ArrayAdapter
        lvHistory.setAdapter(arrayAdapter);

    }

    private void read_member_history()
    {
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put("member_id", member_id);
            Log.d(TAG, "Populated parameters for json request.");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, read_member_history_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response Received: " + response.toString());
                        try {
                            //Check if user got logged in successfully
                            if (response.getInt(KEY_STATUS) == 0) {

                                JSONArray history_arr = response.getJSONArray("data");

                                ArrayList<String> history_readable = new ArrayList<>();
                                for (int i = 0; i < history_arr.length(); i++)
                                {
                                    JSONObject cur = history_arr.getJSONObject(i);

                                    String hijri_year = cur.getString("hijri_year");
                                    String registration_date = cur.getString("membership_date");
                                    int prepaid = cur.getInt("prepaid");

                                    String display = "Hijri Year: " + hijri_year + "\n"
                                                    +"Membership Began: " + registration_date;
                                    if (prepaid == 1)
                                    {
                                        display += "\nPrepaid";
                                    }

                                    //String display = "Hijri Year: " + hijri_year + "\n"
                                    //        +"Registration Date: " + registration_date + "\n"
                                    //        +"Number of Years: " + num_years + "\n"
                                    //        +"IP Address: " + ip_address +"\n";

                                    history_readable.add(display);
                                }

                                set_member_history(history_readable);

                               // adapter = new MemberRecyclerViewAdapter(SearchMembersActivity.this,search_results);
                               // adapter.setClickListener(SearchMembersActivity.this);
                                //recyclerView.setAdapter(adapter);


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



    private void set_member_info(JSONObject member_info)
    {
        String full_name, member_id, address, email, phone_num;

        try {

            full_name = get_name(member_info.getString(KEY_FIRST_NAME),
                    member_info.getString(KEY_MID_NAME),
                    member_info.getString(KEY_LAST_NAME));

            member_id = member_info.getString(KEY_MEMBER_ID);

            address = get_address(member_info.getString(KEY_STREET_ADR),
                    member_info.getString(KEY_ADR_PT2),
                    member_info.getString(KEY_CITY),
                    member_info.getString(KEY_ZIP_CODE));

            email = member_info.getString(KEY_EMAIL);

            phone_num = get_phone(member_info.getString(KEY_HOME_NUM),
                    member_info.getString(KEY_CELL_NUM));

            tvFullName.setText(full_name);
            tvMemberID.setText(member_id);
            tvAddress.setText(address);
            tvEmail.setText(email);
            tvPhoneNum.setText(phone_num);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String get_name(String first, String mid, String last)
    {
        String name;
        if (mid.equals(KEY_EMPTY))
        {
            name = first + " " + last;
        }
        else
        {
            name = first + " " + mid + " " + last;
        }
        return name;
    }

    private String get_address(String adr, String adr_pt2, String city, String zip)
    {
        String address = adr + "\n";
        if (!adr_pt2.equals(KEY_EMPTY))
        {
            address += adr_pt2;
            address += "\n";
        }
        address += city;
        address += (", CA " + zip);
        return address;
    }

    private String get_phone(String home, String cell)
    {
        String phone = "";
        if (home != "" && home != null)
        {
            phone += ("Home: " + home);

            if (cell != "" && cell != null)
            {
                phone += ("\nCell: " + cell);
            }
        }
        else
        {
            phone += ("Cell: " + cell);
        }
        return phone;
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
