package com.example.mca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

public class SearchMembersActivity extends AppCompatActivity implements MemberRecyclerViewAdapter.ItemClickListener {


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
    private static final String KEY_BY_CITY = "by_city";
    private static final String KEY_BY_YEAR = "by_year";
    private static final String KEY_BY_ZIP = "by_zip";
    private static final String KEY_EMPTY = "";
    private static final String KEY_SEARCH_TEXT = "search_string";


    private static final String TAG = "SearchMembersActivity";


    MemberRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    EditText search_et;
    String search_text;

    RelativeLayout RelativeLayout1;
    ImageButton imgbtnFilter;

    int admin_lvl;

    String by_city;
    String by_year;
    String by_zip;

    Boolean new_search_params;

    TextView tvFilters;

    private String search_url = "https://1c68af7e.ngrok.io/mca_members/login/search.php";
    private String advanced_search_url = "https://1c68af7e.ngrok.io/mca_members/login/advanced_search.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_members);

        // get member info
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            admin_lvl = bundle.getInt("admin_lvl");
        }


        by_city = "";
        by_year = "";
        by_zip = "";
        new_search_params = false;
        tvFilters = findViewById(R.id.tv_filters);
        //tvFilters.setVisibility(View.GONE);

        RelativeLayout1 = (RelativeLayout) findViewById(R.id.RelativeLayout1);


        search_et = findViewById(R.id.et_search);
        search_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                search_text = search_et.getText().toString().toLowerCase().trim();
                if (!search_text.equals(""))
                    get_search();
                return false;
            }
        });

        search_et.addTextChangedListener(filterTextWatcher);

        imgbtnFilter = findViewById(R.id.imgbtn_filter);
        imgbtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_search_filters_popup(by_year, by_city, by_zip);

                /*
                if (new_search_params)
                {
                   // search
                    Log.d(TAG, "New search parameters, sending new search now.");
                    new_search_params = false;
                }*/
            }
        });





        // data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");

        // set up the RecyclerView
        recyclerView = findViewById(R.id.rv_members);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //adapter = new MemberRecyclerViewAdapter(this, animalNames);
        //adapter.setClickListener(this);
        //recyclerView.setAdapter(adapter);


    }


    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            search_text = search_et.getText().toString().toLowerCase().trim();
            if (!search_text.equals(""))
                get_search();
            /*
            int radius = 0;
            radius = Integer.valueof(s.toString);
            double area = Math.PI * radius * radius;*/
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onItemClick(View view, int position) {
        String member_selected = adapter.getItem(position);
        Toast.makeText(this, "Viewing full profile of " + member_selected, Toast.LENGTH_SHORT).show();
        //get_member_popup(member_selected);
        load_member_profile(member_selected);
    }


    private void load_member_profile(String member_id)
    {
        Intent i = new Intent(getApplicationContext(), AdminsMemberViewActivity.class);
        i.putExtra("member_id", member_id);
        i.putExtra("admin_lvl", admin_lvl);
        startActivity(i);
        //finish();
    }



    private void get_search() {

        search_text = search_et.getText().toString().toLowerCase().trim();
        if (!search_text.equals(KEY_EMPTY))
            search_text += "*";

        Log.d(TAG, "Searching for this string: " + search_text);
        display_filters();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_BY_YEAR, by_year);
            request.put(KEY_BY_CITY, by_city);
            request.put(KEY_BY_ZIP, by_zip);
            request.put(KEY_SEARCH_TEXT, search_text);
            Log.d(TAG, "Populated parameters for json request: " + request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, advanced_search_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response Received: " + response.toString());

                        try {
                            //Check if user got logged in successfully

                            JSONArray member_arr = response.getJSONArray("data");

                            ArrayList<JSONObject> search_results = new ArrayList<>();
                            for (int i = 0; i < member_arr.length(); i++)
                            {
                                JSONObject cur= member_arr.getJSONObject(i);
                                /*String member_id = cur.getString("member_id");
                                String first_name = cur.getString("first_name");
                                String mid_name = cur.getString("mid_name");
                                String last_name = cur.getString("last_name");

                                String display = get_name(first_name,mid_name,last_name) +
                                                    "\n" + member_id;

                                search_results.add(display);*/
                                search_results.add(cur);
                            }

                            adapter = new MemberRecyclerViewAdapter(SearchMembersActivity.this,search_results);
                            adapter.setClickListener(SearchMembersActivity.this);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

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

    private String get_name(String first, String mid, String last)
    {
        String name;
        if (mid.equals(""))
        {
            name = first + " " + last;
        }
        else
        {
            name = first + " " + mid + " " + last;
        }
        return name;
    }

    /*
    Button closePopupBtn;
    PopupWindow popupWindow;

    private TextView tvFullName;
    private TextView tvMemberID;
    private TextView tvAddress;
    private TextView tvEmail;
    private TextView tvPhoneNum;

    private String read_member_url = "http://192.168.64.2/mca_members/login/read_member.php";

    private void get_member_popup(String member_id)
    {
        //instantiate the popup.xml layout file
        LayoutInflater layoutInflater = (LayoutInflater) SearchMembersActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_member_profile,null);

        closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);

        tvFullName = customView.findViewById(R.id.tv_full_name);
        tvMemberID = customView.findViewById(R.id.tv_membership_id);
        tvAddress = customView.findViewById(R.id.tv_m_address);
        tvEmail = customView.findViewById(R.id.tv_m_email);
        tvPhoneNum = customView.findViewById(R.id.tv_m_phone);

        read_member_data(member_id);

        //tvPhoneNum.setText("HELLO");

        //instantiate popup window
        popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        //display the popup window
        popupWindow.showAtLocation(RelativeLayout1, Gravity.CENTER, 0, 0);

        //close popup window if touch outside of it
        popupWindow.setFocusable(true);

        //close the popup window on button click
        closePopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }*/

    /*

    private void read_member_data(String member_id) {
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
    }*/

    /*
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

    }*/


    private String get_address(String adr, String adr_pt2, String city, String zip)
    {
        String address = adr + "\n";
        if (adr_pt2 != "" && adr_pt2 != null)
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

    PopupWindow popupWindow;
    Spinner spinnerYears;
    Spinner spinnerCity;
    Spinner spinnerZipcode;
    private void get_search_filters_popup(final String old_year, final String old_city, final String old_zip)
    {
        //instantiate the popup.xml layout file
        LayoutInflater layoutInflater = (LayoutInflater) SearchMembersActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_search_filters,null);

        Button btnGo = customView.findViewById(R.id.btn_go);

        // BY YEAR FILTERING
        spinnerYears = customView.findViewById(R.id.spinner_years);
        ArrayList<String> years_list = new ArrayList<String>(
                Arrays.asList(
                        "Any",
                        "2019/2020",
                        "2018/2019",
                        "2017/2018",
                        "2016/2017",
                        "2015/2016",
                        "2016/2015")
        );

        ArrayAdapter<String> adapter_years = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years_list);
        spinnerYears.setAdapter(adapter_years);
        if (!by_year.equals(KEY_EMPTY)) {
            spinnerYears.setSelection(years_list.indexOf(by_year));
        }

        // BY CITY FILTERING
        spinnerCity = customView.findViewById(R.id.spinner_city);

        ArrayList<String> city_list = new ArrayList<String>(
                Arrays.asList(
                        "Any",
                        "Santa Clara",
                        "Sunnyvale",
                        "Fremont",
                        "San Jose")
        );

        ArrayAdapter<String> adapter_cities = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, city_list);
        spinnerCity.setAdapter(adapter_cities);
        if (by_city.equals(KEY_EMPTY)) {
            spinnerCity.setSelection(0);
        } else {
            spinnerCity.setSelection(city_list.indexOf(by_city));
        }

        // BY ZIPCODE FILTERING
        spinnerZipcode = customView.findViewById(R.id.spinner_zipcode);

        ArrayList<String> zip_list = new ArrayList<String>(
                Arrays.asList(
                        "Any",
                        "95051",
                        "95050",
                        "95052")
        );
        ArrayAdapter<String> adapter_zip = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, zip_list);
        spinnerZipcode.setAdapter(adapter_zip);
        if (!by_zip.equals(KEY_EMPTY)) {
            spinnerZipcode.setSelection(city_list.indexOf(by_zip));
        }


        //instantiate popup window
        popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //display the popup window
        popupWindow.showAtLocation(RelativeLayout1, Gravity.CENTER, 0, 0);

        //close popup window if touch outside of it
        popupWindow.setFocusable(true);

        //close the popup window on button click
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                by_year = spinnerYears.getItemAtPosition(spinnerYears.getSelectedItemPosition()).toString();
                by_city = spinnerCity.getItemAtPosition(spinnerCity.getSelectedItemPosition()).toString();
                by_zip = spinnerZipcode.getItemAtPosition(spinnerZipcode.getSelectedItemPosition()).toString();
                if (by_year.equals("Any"))
                    by_year = "";
                if (by_city.equals("Any"))
                    by_city = "";
                if (by_zip.equals("Any"))
                    by_zip = "";

                popupWindow.dismiss();



                if (!by_year.equals(old_year) || !by_city.equals(old_city) || !old_zip.equals(by_zip))
                    get_search();
                    //new_search_params = true;
            }
        });
    }

    private String formfilteringlist()
    {
        String ret = "";
        if (!by_year.equals(KEY_EMPTY)){
            ret += (" Year: " + by_year + " ");
        }
        if (!by_city.equals(KEY_EMPTY)){
            ret += (" City: " + by_city + " ");
        }
        if (!by_zip.equals(KEY_EMPTY)){
            ret += (" Zipcode: " + by_zip + " ");
        }

        return ret;
    }

    private Boolean filters_selected(){
        if (by_year.equals(KEY_EMPTY) && by_city.equals(KEY_EMPTY) && by_zip.equals(KEY_EMPTY))
            return false;
        return true;
    }

    private void display_filters()
    {
        if (filters_selected())
        {
            //tvFilters.setVisibility(View.VISIBLE);
            tvFilters.setText(formfilteringlist());
        }
        else
        {
            //tvFilters.setVisibility(View.GONE);
            tvFilters.setText("");
        }
    }

}
