package com.example.mca;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";

    private static final String KEY_EMPTY = "";

    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_MID_NAME = "mid_name";
    private static final String KEY_LAST_TIME = "last_name";
    private static final String KEY_STREET_ADR = "street_address";
    private static final String KEY_ADR_PT2 = "address_pt2";
    private static final String KEY_CITY = "city";
    private static final String KEY_ZIP_CODE = "zip_code";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_HOME_NUM = "home_num";
    private static final String KEY_CELL_NUM = "cell_num";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_YEARS = "num_years";

    private EditText etFirstName;
    private EditText etMidName;
    private EditText etLastName;
    private EditText etStreetAdr;
    private EditText etStreetAdrPt2;
    private EditText etCity;
    private EditText etZipCode;
    private EditText etEmail;
    private EditText etHomeNum;
    private EditText etCellNum;

    private String first_name;
    private String mid_name;
    private String last_name;
    private String street_address;
    private String address_pt2;
    private String city;
    private String zip_code;
    private String email;
    private String home_num;
    private String cell_num;
    private String gender;
    private int num_years;

    private RadioGroup rgGender;
    private RadioButton rbFemale;

    private Spinner spinnerYears;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.et_first_name);
        etMidName = findViewById(R.id.et_mid_name);
        etLastName = findViewById(R.id.et_last_name);
        etStreetAdr = findViewById(R.id.et_street_adr);
        etStreetAdrPt2 = findViewById(R.id.et_street_adr_pt2);
        etCity = findViewById(R.id.et_city);
        etZipCode = findViewById(R.id.et_zip_code);
        etEmail = findViewById(R.id.et_email);
        etHomeNum = findViewById(R.id.et_home_num);
        etCellNum = findViewById(R.id.et_cell_num);

        rgGender = findViewById(R.id.rg_gender);
        rbFemale = findViewById(R.id.rb_female);

        //get the spinner from the xml.
        spinnerYears = findViewById(R.id.spinner_years);

        //create a list of items for the spinner.
        String[] items = new String[]{"1 Year ($40)", "2 Years ($60)",  "3 Years ($90)",
                                        "4 Years ($120)", "5 Years (150)"};

        //create an adapter to describe how the items are displayed,
        // adapters are used in several places in android.
        // There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spinnerYears.setAdapter(adapter);


        Button next_btn = findViewById(R.id.btnRegister);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Retrieve the data entered in the edit texts
                first_name = etFirstName.getText().toString().trim();
                mid_name = etMidName.getText().toString().trim();
                last_name = etLastName.getText().toString().trim();
                street_address = etStreetAdr.getText().toString().trim();
                address_pt2 = etStreetAdrPt2.getText().toString().trim();
                city = etCity.getText().toString().trim();
                zip_code = etZipCode.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                home_num = etHomeNum.getText().toString().trim();
                cell_num = etCellNum.getText().toString().trim();
                gender = "";
                num_years = spinnerYears.getSelectedItemPosition() + 1;


                //Retrieve the data from radio group
                if (rgGender.getCheckedRadioButtonId() != -1)
                {
                    int selected_id = rgGender.getCheckedRadioButtonId();
                    RadioButton rb_selected = findViewById(selected_id);
                    gender = (String) rb_selected.getText();
                }

                if (validateInputs()) {
                    registerUser();
                }

            }
        });

    }

    private void loadPayActivity(JSONObject response)
    {
        Intent i = new Intent(getApplicationContext(), PayActivity.class);
        i.putExtra("response", response.toString());
        startActivity(i);
        finish();
    }


    private void registerUser() {
        JSONObject request = new JSONObject();
        try {
            // Store the request parameters
            request.put(KEY_FIRST_NAME, first_name);
            request.put(KEY_MID_NAME, mid_name);
            request.put(KEY_LAST_TIME, last_name);
            request.put(KEY_STREET_ADR, street_address);
            request.put(KEY_ADR_PT2, address_pt2);
            request.put(KEY_CITY, city);
            request.put(KEY_ZIP_CODE, zip_code);
            request.put(KEY_EMAIL, email);
            request.put(KEY_HOME_NUM, home_num);
            request.put(KEY_CELL_NUM, cell_num);
            request.put(KEY_GENDER, gender);
            request.put(KEY_YEARS,num_years);

            Log.d(TAG, "Stored parameters: " + request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        loadPayActivity(request);
    }

    /**
     * Validates inputs and shows error if any
     * @return
     */
    private boolean validateInputs() {

        if (KEY_EMPTY.equals(first_name)) {
            etFirstName.setError("First name cannot be empty");
            etFirstName.requestFocus();
            return false;

        }
        if (KEY_EMPTY.equals(last_name)) {
            etLastName.setError("Last name cannot be empty");
            etLastName.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(street_address)) {
            etStreetAdr.setError("Address cannot be empty");
            etStreetAdr.requestFocus();
            return false;
        }

        if (KEY_EMPTY.equals(city)) {
            etCity.setError("City cannot be empty");
            etCity.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(zip_code)) {
            etZipCode.setError("Zipcode cannot be empty");
            etZipCode.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(email)) {
            etEmail.setError("Email cannot be empty");
            etEmail.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(home_num) && KEY_EMPTY.equals(cell_num)) {
            etHomeNum.setError("One phone number is required");
            etHomeNum.requestFocus();
            etCellNum.setError("Ohe phone number is required");
            etCellNum.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(gender)) {
            rbFemale.setError("Select gender.");
            rbFemale.requestFocus();
            return false;
        }


        return true;
    }

    int get_num_years()
    {
        spinnerYears.getItemAtPosition(spinnerYears.getSelectedItemPosition()).toString();
        return 1;
    }
}