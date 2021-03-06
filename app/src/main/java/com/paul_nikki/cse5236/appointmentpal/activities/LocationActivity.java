package com.paul_nikki.cse5236.appointmentpal.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.paul_nikki.cse5236.appointmentpal.AppConfig;
import com.paul_nikki.cse5236.appointmentpal.Controllers.AppController;
import com.paul_nikki.cse5236.appointmentpal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class LocationActivity extends AppCompatActivity implements View.OnClickListener{

    String TAG = "LocationsActivity";
    Button btnMap;
    TextView headerText;
    TextView address;
    TextView cityStateZip;
    ListView doctorsList;
    TextView officeName;
    ArrayList<String> doctors;
    String locationName;
    String fullAddress;
    JSONArray ja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        btnMap = (Button)findViewById(R.id.btn_mapLoc);
        btnMap.setOnClickListener(this);
        headerText = (TextView)findViewById(R.id.lbl_locationHeader);
        address = (TextView)findViewById(R.id.lbl_address);
        officeName = (TextView)findViewById(R.id.lbl_officeName);
        doctorsList = (ListView)findViewById(R.id.doctorsList);
        Intent intent = getIntent();
        locationName = intent.getStringExtra("LocationName");
        GenerateDoctorsList();
        GenerateOfficeInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    public void GenerateOfficeInfo(){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_LOCATIONS, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG+"Location", response.toString());
                try {
                    ja = response.getJSONArray("Doctors");
                    int s = ja.length();
                    for (int i = 0; i < s; i++) {
                        JSONObject jsonobject = ja.getJSONObject(i);
                        String practicename = jsonobject.getString("practicename");
                        String addressDB = jsonobject.getString("address");
                        if(practicename.equals(locationName)) {
                            fullAddress = addressDB;
                            address.setText(fullAddress);
                            officeName.setText(practicename);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString() );

            }
        });

        // Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    public void GenerateDoctorsList(){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_LOCATIONS, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG+"Doctors", response.toString());
                try {
                    ja = response.getJSONArray("Doctors");
                    int s = ja.length();
                    doctors = new ArrayList<>();
                    for (int i = 0; i < s; i++) {
                        JSONObject jsonobject = ja.getJSONObject(i);
                        String doctorname = jsonobject.getString("doctorname");
                        String practicename = jsonobject.getString("practicename");
                        if(practicename.equals(locationName) && doctorname != null) {
                            doctors.add(doctorname);
                        }
                    }
                    Collections.sort(doctors, String.CASE_INSENSITIVE_ORDER);
                    ArrayAdapter<String> adapter;
                    adapter = new ArrayAdapter<>( getApplicationContext(), android.R.layout.simple_list_item_1, doctors);
                    doctorsList.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString() );

            }
        });

        // Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.btn_mapLoc:
                intent = new Intent(this, MapActivity.class);
                intent.putExtra("Location", fullAddress);
                intent.putExtra("OfficeName", locationName);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
}
