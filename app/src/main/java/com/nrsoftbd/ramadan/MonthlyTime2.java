package com.nrsoftbd.ramadan;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MonthlyTime2 extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "https://ultrateenpatti.com/api/ramadan.json";

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
            pDialog = new ProgressDialog(MonthlyTime2.this);
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

                        String date_for = c.getString("date_for");
                        String fajr = c.getString("fajr");
                        String shurooq = c.getString("shurooq");
                        String dhuhr = c.getString("dhuhr");
                        String asr = c.getString("asr");
                        String maghrib = c.getString("maghrib");
                        String isha = c.getString("isha");


                        // tmp hash map for single contact
                        HashMap<String, String> prayertime = new HashMap<>();

                        // adding each child node to HashMap key => value
                        prayertime.put("date_for", "তারিখঃ " + date_for);
                        prayertime.put("fajr", "ফজরঃ" + fajr);
                        prayertime.put("shurooq", "সূর্যোদয়ঃ " + shurooq);
                        prayertime.put("dhuhr", "যোহরঃ " + dhuhr);
                        prayertime.put("asr", "আসরঃ " + asr);
                        prayertime.put("maghrib", "মাগরিবঃ " + maghrib);
                        prayertime.put("isha", "ইশাঃ " + isha);

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
                    MonthlyTime2.this, prayertimes,
                    R.layout.ramadan,

                    new String[]{"date_for",
                            "fajr", "shurooq", "dhuhr",
                            "asr", "maghrib", "isha"},

                    new int[]{R.id.date_for,
                            R.id.fajr, R.id.shurooq, R.id.dhuhr,
                            R.id.asr, R.id.maghrib, R.id.isha});

            lv.setAdapter(adapter);
        }
    }
}
