package com.nrsoftbd.ramadan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MonthlyTime extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "https://ultrateenpatti.com/api/dhaka.json";

    ArrayList<HashMap<String, String>> prayertimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_time);

        prayertimes = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetPrayerTimes().execute();

    }

    private class GetPrayerTimes extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(MonthlyTime.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray items = jsonObj.getJSONArray("items");

                    // looping through All Contacts
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);

                        String eng_date = c.getString("eng_date");
                        String arabic_date = c.getString("arabic_date");
                        String arabic_year_day = c.getString("arabic_year_day");
                        String seheri = c.getString("seheri");
                        String iftar = c.getString("iftar");
                        String sunrise = c.getString("sunrise");
                        String sunset = c.getString("sunset");
                        String fazar = c.getString("fazar");
                        String juhur = c.getString("juhur");
                        String asar = c.getString("asar");
                        String maghrib = c.getString("maghrib");
                        String isha = c.getString("isha");


                        // tmp hash map for single contact
                        HashMap<String, String> prayertime = new HashMap<>();

                        // adding each child node to HashMap key => value
                        prayertime.put("eng_date", eng_date);
                        prayertime.put("arabic_date", arabic_date);
                        prayertime.put("arabic_year_day", arabic_year_day);
                        prayertime.put("seheri","সেহেরীর শেষ সময়ঃ " + seheri);
                        prayertime.put("iftar","ইফতারের সময়ঃ " + iftar);
                        prayertime.put("sunrise","সূর্যোদয়ঃ " + sunrise);
                        prayertime.put("sunset","সূর্যাস্তঃ " + sunset);
                        prayertime.put("fazar","ফজরঃ " + fazar);
                        prayertime.put("juhur","যোহরঃ " + juhur);
                        prayertime.put("asar","আসরঃ " + asar);
                        prayertime.put("maghrib","মাগরিবঃ " + maghrib);
                        prayertime.put("isha","ইশাঃ " + isha);

                        // adding prayertime to prayertime list
                        prayertimes.add(prayertime);
                    }

                } catch (final JSONException e) {


                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MonthlyTime.this, prayertimes,
                    R.layout.ramadan,

                    new String[]{"eng_date",
                            "arabic_date", "arabic_year_day", "seheri",
                            "iftar", "sunrise", "sunset", "fazar",
                            "juhur", "asar", "maghrib", "isha"},

                    new int[]{R.id.engDate, R.id.arabicDate, R.id.arabicYearDay,
                            R.id.seheri, R.id.iftar, R.id.sunrise, R.id.sunset,
                            R.id.fajr, R.id.juhur, R.id.asar,
                            R.id.maghrib, R.id.isha, });

            lv.setAdapter(adapter);
        }
    }
}
