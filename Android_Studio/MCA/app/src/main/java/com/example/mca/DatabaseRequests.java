package com.example.mca;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseRequests
{

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
    private static final String KEY_ADMIN_LVL = "admin_lvl";

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";

    private static final String TAG = "DatabaseRequests";

    //private static final String server_url = "http://192.168.64.2/";
    private static final String server_url = "https://1c68af7e.ngrok.io/";


    private String read_member_url = server_url + "mca_members/login/read_member.php";
    private String delete_member_url = server_url + "mca_members/login/delete_member.php";
    private String login_url = server_url + "mca_members/login/login.php";
    private String register_url = server_url + "mca_members/login/register.php";

    private String ret_data;


    public String read_member_data(String member_id, final Context cntxt) {
        ret_data = "";
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
                                Log.d(TAG, "Response received by Database Requests Class: " + response.toString());
                                //session.loginUser(username,response.getString(KEY_FULL_NAME));
                                //set_member_info(response);
                                ret_data = response.toString();
                            }else{
                                Toast.makeText(cntxt,
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
                        Toast.makeText(cntxt,
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(cntxt).addToRequestQueue(jsArrayRequest);
        return ret_data;
    }


    public void delete_member(String member_id, final Context cntxt)
    {
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put("username", member_id);
            Log.d(TAG, "Populated parameters for json request.");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, delete_member_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response Received: " + response.toString());
                        try {
                            //Check if user got logged in successfully

                            if (response.getInt(KEY_STATUS) == 0) {
                                Log.d(TAG, "REACHED HERE");
                                Log.d(TAG, "Response received by Database Requests Class: " + response.toString());
                                //session.loginUser(username,response.getString(KEY_FULL_NAME));

                                //set_member_info(response);
                            }else{
                                Toast.makeText(cntxt,
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
                        Toast.makeText(cntxt,
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(cntxt).addToRequestQueue(jsArrayRequest);
    }


    public void login(String username, String password, final Context cntxt) {
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
                        try {
                            //Check if user got logged in successfully

                            if (response.getInt(KEY_STATUS) == 0) {
                                Log.d(TAG, "REACHED HERE");
                                Log.d(TAG, "Response received: " + response.toString());
                                //session.loginUser(username);
                                //loadProfile(response);

                            }else{
                                Toast.makeText(cntxt,
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
                        Toast.makeText(cntxt,
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(cntxt).addToRequestQueue(jsArrayRequest);
    }

    private void register_member(String signUp_info, final Context cntxt) {
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
                                Toast.makeText(cntxt,
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
                        //Display error message whenever an error occurs
                        Toast.makeText(cntxt,
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                        String errMsg = error.getMessage();
                        if (errMsg != null)
                            Log.d(TAG, errMsg);
                        else
                            Log.d(TAG, "Error: ");


                    }
                });


        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(cntxt).addToRequestQueue(jsArrayRequest);
    }

    public int get_admin_lvl(String member_id, Context cntxt)
    {
        int ret = 0;
        String data = read_member_data(member_id, cntxt);
        JSONObject member_data;
        try {
            member_data = new JSONObject(data);
            ret = member_data.getInt(KEY_ADMIN_LVL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }


}
