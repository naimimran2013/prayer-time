package com.nrsoftbd.ramadan;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PrayerTime extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    Context context = this;
    private static final String TAG = "tag";
    String url;

    String tag_json_obj = "json_obj_req";
    ProgressDialog pDialog;

    TextView fazarTm, dhuhrTm, asarTm, magribTm, ishaTm, mlocation, mdate;
    EditText locationEt;
    Button searchBtn;
    String city;
    String country;
    String searchLocation, finalLocation;


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prayer_time);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fetchLocation();


        fazarTm = (TextView) findViewById(R.id.fazarTm);
        dhuhrTm = (TextView) findViewById(R.id.dhuhrTm);
        asarTm = (TextView) findViewById(R.id.asarTm);
        magribTm = (TextView) findViewById(R.id.magribTm);
        ishaTm = (TextView) findViewById(R.id.ishaTm);
        mlocation = (TextView) findViewById(R.id.mlocation);
        mdate = (TextView) findViewById(R.id.mdate);
        searchBtn = (Button) findViewById(R.id.searchBtn);


        // getCurrentLoaction();
        //defaultTime();


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* String location = locationEt.getText().toString().trim();
                if (location.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter your Location", Toast.LENGTH_LONG).show();
                } else {*/


                url = "https://muslimsalat.com/" + finalLocation + ".json?key=a207cd17473c3b323447d41a5f471601";

                jsonData();
                //}
            }
        });
    }


    private void fetchLocation() {


        if (ContextCompat.checkSelfPermission(PrayerTime.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(PrayerTime.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to acess this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(PrayerTime.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(PrayerTime.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


            }
        } else {
            // Permission has already been granted
            getLocation();

            if (haveNetworkConnection()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getupdateTime();
                    }
                }, 3000);

            } else {
                Toast.makeText(getApplicationContext(), "Please connect to Internet", Toast.LENGTH_LONG).show();
            }


        }

    }

    private void getupdateTime() {

        url = "https://muslimsalat.com/" + finalLocation + ".json?key=a207cd17473c3b323447d41a5f471601";

        jsonData();

    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Double latittude = location.getLatitude();
                            Double longitude = location.getLongitude();

                            try {
                                Geocoder geocoder = new Geocoder(getApplicationContext());
                                List<Address> addresses = null;
                                addresses = geocoder.getFromLocation(latittude, longitude, 1);
                                String country = addresses.get(0).getCountryName();
                                String city = addresses.get(0).getLocality();

                                String city2 = addresses.get(0).getSubAdminArea();
                                String state = addresses.get(0).getAdminArea();
                                String divisiton = addresses.get(0).getSubThoroughfare();



                                if (city2 !=null){
                                    finalLocation = city2;
                                }
                                else if (city != null) {
                                    finalLocation = city;
                                } else if (country != null) {
                                    finalLocation = country;
                                }
                                //finalLocation = city + ", " + country;


                                mlocation.setText(city2);

                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLocation();
                // getupdateTime();
                Toast.makeText(getApplicationContext(), "Permission Grant", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(getApplicationContext(), "Permission Not Grant", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void defaultTime() {

        url = "https://muslimsalat.com/kolkata.json?key=a207cd17473c3b323447d41a5f471601";

        jsonData();
    }


    private void jsonData() {


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {

                            //get location
                            country = response.get("country").toString();
                            // String state = response.get("state").toString();
                            String city = response.get("city").toString();

                            String location = city + ", " + country;

                            //get Date
                            String date = response.getJSONArray("items").getJSONObject(0).get("date_for").toString();

                            //get Salat Time
                            String fazarTime = response.getJSONArray("items").getJSONObject(0).get("fajr").toString();
                            String dhuhrTime = response.getJSONArray("items").getJSONObject(0).get("dhuhr").toString();
                            String asarTime = response.getJSONArray("items").getJSONObject(0).get("asr").toString();
                            String maghribTime = response.getJSONArray("items").getJSONObject(0).get("maghrib").toString();
                            String ishaTime = response.getJSONArray("items").getJSONObject(0).get("isha").toString();

                            fazarTm.setText(fazarTime);
                            dhuhrTm.setText(dhuhrTime);
                            asarTm.setText(asarTime);
                            magribTm.setText(maghribTime);
                            ishaTm.setText(ishaTime);
                            mdate.setText(date);
                            mlocation.setText(finalLocation);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                Toast.makeText(PrayerTime.this, "Please Connect Internet", Toast.LENGTH_LONG).show();
                pDialog.hide();
            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
