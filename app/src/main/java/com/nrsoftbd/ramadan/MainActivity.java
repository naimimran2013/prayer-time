package com.nrsoftbd.ramadan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.time.chrono.HijrahDate.from;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView currentCity, noInternet, tv_hour, hour, tv_minute, minute, tv_second, second, salatName,
            todayDate, fazarTm, fazarTmEnd, dhuhrTm, dhuhrTmEnd, asarTm, asarTmEnd, magribTm, magribTmEnd, ishaTm, ishaTmEnd, todayArabicDate,
            tv_fajr, tv_dhuhr, tv_asar, tv_magrib, tv_isha, ifterTime, seheriTime, sunriseTm, sunsetTm, tmRemaining;

    private LinearLayout fajarLayout, dhuhrLayout, asarLayout, magribLayout, ishaLayout;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;

    private Double latitude;
    private Double longitude;
    private String city, bnCity, city2;
    private String country, bnCountry, locCountry;
    private String searchLocation, location;
    private String actionbarName, currentTime, ctm;
    private SharedPreferences mPrefs;
    private static final String PREFS_NAME = "PrefsFile";
    private SharedPreferences.Editor editor;
    private static final String TAG = "tag";
    String url;
    String tag_json_obj = "json_obj_req";
    ProgressDialog pDialog;
    private Handler handler = new Handler();
    private Runnable runnable;
    int fazarDate;
    Date futureDate;
    String district;
    String appUrl;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    String bnDate, bnMonth, bnYear, en2bnDate, en2bnMonth, en2bnYear, en2bnDay;

    String bnHours = " ঘণ্টা";
    String bnMinte = " মিনিট";
    String bnSecond = " সেকেন্ড";
    String fazarTime, dhuhrTime, asarTime, magribTime, ishaTime, sunriseTime, time, seheriTm, asarEndTime, ishaEndTime;

    private ImageButton fajrAlarm, dhuhrAlarm, asarAlarm, magribAlarm, ishaAlarm;


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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ads();


        currentCity = (TextView) findViewById(R.id.currentCity);
        tv_hour = (TextView) findViewById(R.id.tv_hour);
        tv_minute = (TextView) findViewById(R.id.tv_minute);
        tv_second = (TextView) findViewById(R.id.tv_second);

        hour = (TextView) findViewById(R.id.hour);
        minute = (TextView) findViewById(R.id.minute);
        second = (TextView) findViewById(R.id.second);

        todayDate = (TextView) findViewById(R.id.todayDate);
        fazarTm = (TextView) findViewById(R.id.fazarTm);
        fazarTmEnd = (TextView) findViewById(R.id.fazarTmEnd);
        dhuhrTm = (TextView) findViewById(R.id.dhuhrTm);
        dhuhrTmEnd = (TextView) findViewById(R.id.dhuhrTmEnd);
        asarTm = (TextView) findViewById(R.id.asarTm);
        asarTmEnd = (TextView) findViewById(R.id.asarTmEnd);
        magribTm = (TextView) findViewById(R.id.magribTm);
        magribTmEnd = (TextView) findViewById(R.id.magribTmEnd);
        ishaTmEnd = (TextView) findViewById(R.id.ishaTmEnd);
        ishaTm = (TextView) findViewById(R.id.ishaTm);
        noInternet = (TextView) findViewById(R.id.noInternet);
        salatName = (TextView) findViewById(R.id.salatName);
        todayArabicDate = (TextView) findViewById(R.id.todayArabicDate);
        tv_fajr = (TextView) findViewById(R.id.tv_fajr);
        tv_dhuhr = (TextView) findViewById(R.id.tv_dhuhr);
        tv_asar = (TextView) findViewById(R.id.tv_asar);
        tv_magrib = (TextView) findViewById(R.id.tv_magrib);
        tv_isha = (TextView) findViewById(R.id.tv_isha);
        ifterTime = (TextView) findViewById(R.id.ifterTime);
        seheriTime = (TextView) findViewById(R.id.seheriTime);
        sunriseTm = (TextView) findViewById(R.id.sunriseTm);
        sunsetTm = (TextView) findViewById(R.id.sunsetTm);
        tmRemaining = (TextView) findViewById(R.id.tmRemaining);

        fajarLayout = (LinearLayout) findViewById(R.id.fajarLayout);
        dhuhrLayout = (LinearLayout) findViewById(R.id.dhuhrLayout);
        asarLayout = (LinearLayout) findViewById(R.id.asarLayout);
        magribLayout = (LinearLayout) findViewById(R.id.magribLayout);
        ishaLayout = (LinearLayout) findViewById(R.id.ishaLayout);


        fajrAlarm = (ImageButton) findViewById(R.id.fajarAlarm);
        dhuhrAlarm = (ImageButton) findViewById(R.id.dhuhrAlarm);
        asarAlarm = (ImageButton) findViewById(R.id.asarAlarm);
        magribAlarm = (ImageButton) findViewById(R.id.magribAlarm);
        ishaAlarm = (ImageButton) findViewById(R.id.ishaAlarm);

        alarmControl();


        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        currentTime = sdf.format(new Date());

        SimpleDateFormat sdfws = new SimpleDateFormat("h:mm:ss a");
        ctm = sdfws.format(new Date());


        getTodayDate();
        Countdown();

        banglatxt();
       /* DefaultLocation();
        getCurrentLoaction();*/

        fetchLocation();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (haveNetworkConnection()) {

            noInternet.setVisibility(View.GONE);

        } else {


            getSavedPrayerTime();
            noInternet.setVisibility(View.VISIBLE);
            noInternet.setText(getString(R.string.noInternet));
        }
    }

    private void ads() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


    }

    private void alarmControl() {

        fajrAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                i.putExtra(AlarmClock.EXTRA_MESSAGE, "ফজরের ওয়াক্ত শুরু হয়েছে!");
                i.putExtra(AlarmClock.EXTRA_HOUR, 4);
                i.putExtra(AlarmClock.EXTRA_MINUTES, 0);
                i.putExtra(AlarmClock.EXTRA_IS_PM, false);
                startActivity(i);
            }


        });
        dhuhrAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                i.putExtra(AlarmClock.EXTRA_MESSAGE, "যোহরের ওয়াক্ত শুরু হয়েছে!");
                i.putExtra(AlarmClock.EXTRA_HOUR, 12);
                i.putExtra(AlarmClock.EXTRA_MINUTES, 0);
                i.putExtra(AlarmClock.EXTRA_IS_PM, true);
                startActivity(i);
            }


        });
        asarAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                i.putExtra(AlarmClock.EXTRA_MESSAGE, "আসরের ওয়াক্ত শুরু হয়েছে!");
                i.putExtra(AlarmClock.EXTRA_HOUR, 4);
                i.putExtra(AlarmClock.EXTRA_MINUTES, 40);
                i.putExtra(AlarmClock.EXTRA_IS_PM, true);
                startActivity(i);
            }

        });

        magribAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                    Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                    i.putExtra(AlarmClock.EXTRA_MESSAGE, "মাগরিবের ওয়াক্ত শুরু হয়েছে!");
                    i.putExtra(AlarmClock.EXTRA_HOUR, 6);
                    i.putExtra(AlarmClock.EXTRA_MINUTES, 40);
                    i.putExtra(AlarmClock.EXTRA_IS_PM, true);
                    startActivity(i);


            }
        });
        ishaAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                    i.putExtra(AlarmClock.EXTRA_MESSAGE, "ঈশার ওয়াক্ত শুরু হয়েছে!");
                    i.putExtra(AlarmClock.EXTRA_HOUR, 8);
                    i.putExtra(AlarmClock.EXTRA_MINUTES, 0);
                    i.putExtra(AlarmClock.EXTRA_IS_PM, true);
                    startActivity(i);


            }
        });


    }

    private void banglatxt() {

        bnCountry = "বাংলাদেশ";

        tv_fajr.setText("ফজর :");

        if (en2bnDay == "শুক্রবার") {
            tv_dhuhr.setText("জুম'আ :");
        } else {
            tv_dhuhr.setText("যোহর :");
        }
        tv_asar.setText("আসর :");
        tv_magrib.setText("মাগরিব :");
        tv_isha.setText("ইশা :");
        // tv_sunrise.setText("সূর্যোদয় :");
        tmRemaining.setText("সময় বাকী আছে");
        tv_hour.setText(bnHours);
        tv_minute.setText(bnMinte);
        tv_second.setText(bnSecond);

        todayArabicDate.setText(bnDate + " " + bnMonth + " " + bnYear + ", " + en2bnDay);
        todayDate.setText(en2bnDate + " " + en2bnMonth + " " + en2bnYear);


    }

    private void fetchLocation() {


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to acess this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
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
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


            }
        } else {
            // Permission has already been granted
            getCurrentLoaction();

            if (haveNetworkConnection()) {
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("নামাজের সময়সূচী লোড হচ্ছে......");
                pDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getUpdateTime();
                        pDialog.dismiss();
                    }
                }, 3000);

            } else {
                Toast.makeText(getApplicationContext(), "সঠিক সময়সূচী দেখতে ইন্টারনেট সংযোগ দিন", Toast.LENGTH_LONG).show();
            }


        }
    }


    private void Countdown() {

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "dd-MM-yyyy hh:mm a");

                    SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
                    String currentMonthDate = sdf.format(new Date());

                    SimpleDateFormat sdfdate = new SimpleDateFormat("dd");
                    String onlydate = sdfdate.format(new Date());


                    SimpleDateFormat parser = new SimpleDateFormat("h:mm a");

                    Date userDate = parser.parse(currentTime);

                    if (userDate.after(parser.parse(ishaTime))) {

                        futureDate = dateFormat.parse(fazarDate + "-" + currentMonthDate + " " + time);

                    } else {

                        futureDate = dateFormat.parse(onlydate + "-" + currentMonthDate + " " + time);
                    }

                    //handle Fazar time

                    Date currentDate = new Date();

                    if (!currentDate.after(futureDate)) {

                        long diff = futureDate.getTime() - currentDate.getTime();

                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;


                        hour.setText("" + String.format("%02d", hours));
                        minute.setText("" + String.format("%02d", minutes));
                        second.setText("" + String.format("%02d", seconds));


                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        };
        handler.postDelayed(runnable, 1 * 1000);

    }

    private void getSavedPrayerTime() {

        Toast.makeText(getApplicationContext(), "Please connect the internet connection for update Prayer Time", Toast.LENGTH_LONG);

        mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        getPreferenceData();
        salatNameControl();


    }

    private void getPreferenceData() {


        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sp.contains("Fazar")) {
            fazarTime = sp.getString("Fazar", "Not Found");
            fazarTm.setText(fazarTime + " থেকে");


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

                LocalTime datetime = LocalTime.parse(fazarTime.toUpperCase(), formatter);
                datetime = datetime.minusMinutes(2);
                seheriTm = datetime.format(formatter);


                seheriTime.setText("সেহেরীর শেষ সময় \n" + seheriTm);
                ishaTmEnd.setText(seheriTm);
            } else {

                seheriTime.setText("সেহেরীর শেষ সময় \n" + fazarTime);
                ishaTmEnd.setText(fazarTime);

            }

        }
        if (sp.contains("Dhuhr")) {
            dhuhrTime = sp.getString("Dhuhr", "Not Found");
            dhuhrTm.setText(dhuhrTime + " থেকে");
        }
        if (sp.contains("Asar")) {
            asarTime = sp.getString("Asar", "Not Found");
            asarTm.setText(asarTime + " থেকে");
            dhuhrTmEnd.setText(asarTime);


        }
        if (sp.contains("Magrib")) {
            magribTime = sp.getString("Magrib", "Not Found");
            magribTm.setText(magribTime + " থেকে");
            ifterTime.setText("ইফতারের সময় \n" + magribTime);
            sunsetTm.setText("সূর্যাস্ত : " + magribTime);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

                LocalTime timemagrib = LocalTime.parse(magribTime.toUpperCase(), formatter);
                timemagrib = timemagrib.minusMinutes(1);
                asarEndTime = timemagrib.format(formatter);
                asarTmEnd.setText(asarEndTime);
            } else {
                asarTmEnd.setText(magribTime);
            }
        }
        if (sp.contains("Isha")) {
            ishaTime = sp.getString("Isha", "Not Found");
            ishaTm.setText(ishaTime + " থেকে");
            magribTmEnd.setText(ishaTime);
        }
        if (sp.contains("Location")) {
            location = sp.getString("Location", "Not Found");


        }
        if (sp.contains("Sunrise")) {
            sunriseTime = sp.getString("Sunrise", "Not Found");
            sunriseTm.setText("সূর্যোদয় : " + sunriseTime);
            fazarTmEnd.setText(sunriseTime);
        }
        if (sp.contains("City")) {
            city = sp.getString("City", "Not Found");

        }
        if (sp.contains("City2")) {
            city2 = sp.getString("City2", "Not Found");
        }

        salatNameControl();


        if (sp.contains("Country")) {
            country = sp.getString("Country", "Not Found");

            bnCountry = "বাংলাদেশ";
            citycheck();
            currentCity.setText("নামাজের সময়সূচী- " + bnCity + ", " + bnCountry);
            tmRemaining.setText("সময় বাকী আছে");
            tv_fajr.setText("ফজর :");
            tv_dhuhr.setText("যোহর :");
            tv_asar.setText("আসর :");
            tv_magrib.setText("মাগরিব :");
            tv_isha.setText("ইশা :");
            // tv_sunrise.setText("সূর্যোদয় :");

            tv_hour.setText(bnHours);
            tv_minute.setText(bnMinte);
            tv_second.setText(bnSecond);
            todayArabicDate.setText(bnDate + " " + bnMonth + " " + bnYear + ", " + en2bnDay);
            todayDate.setText(en2bnDate + " " + en2bnMonth + " " + en2bnYear);


        }


    }


    private void getTodayDate() {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat sd = new SimpleDateFormat("dd MMMM yyyy, EEEE");
        String today = sd.format(c.getTime());

        SimpleDateFormat enDD = new SimpleDateFormat("dd");
        SimpleDateFormat enMM = new SimpleDateFormat("MMMM");
        SimpleDateFormat enYY = new SimpleDateFormat("yyyy");
        SimpleDateFormat enDay = new SimpleDateFormat("EE");

        en2bnDate = enDD.format(c.getTime());
        en2bnMonth = enMM.format(c.getTime());
        en2bnYear = enYY.format(c.getTime());
        en2bnDay = enDay.format(c.getTime());

        en2bnDateConvert();
        en2bnMonthConvert();
        en2bnYearConvert();
        en2bnDayConvert();

        todayDate.setText(today);


        //convert hijri date
        SimpleDateFormat dd = new SimpleDateFormat("dd");
        String date = dd.format(new Date());

        SimpleDateFormat mm = new SimpleDateFormat("MM");
        String month = mm.format(new Date());

        SimpleDateFormat yy = new SimpleDateFormat("yyyy");
        String year = yy.format(new Date());


        try {

            int yearY = Integer.parseInt(year);
            int monthOfYear = Integer.parseInt(month);
            int dayOfMonth = Integer.parseInt(date);

            LocalDate dt = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dt = LocalDate.of(yearY, monthOfYear, dayOfMonth);
                HijrahDate hijrahDate = from(dt);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                String formatted = formatter.format(hijrahDate);

                DateTimeFormatter formatdate = DateTimeFormatter.ofPattern("dd");
                DateTimeFormatter formatmonth = DateTimeFormatter.ofPattern("MMMM");
                DateTimeFormatter formatyear = DateTimeFormatter.ofPattern("yyyy");

                bnDate = formatdate.format(hijrahDate);
                bnMonth = formatmonth.format(hijrahDate);
                bnYear = formatyear.format(hijrahDate);

                monthConvert();
                dateConvert();
                yearConvert();


                todayArabicDate.setText(formatted);


            }
        } catch (Exception e) {

        }

        // convert to hijri

    }


    private void yearConvert() {

        if (bnYear.equals("1440")) {
            bnYear = "১৪৪০";
        } else if (bnYear.equals("1441")) {
            bnYear = "১৪৪১";
        } else if (bnYear.equals("1442")) {
            bnYear = "১৪৪২";
        } else if (bnYear.equals("1443")) {
            bnYear = "১৪৪৩";
        } else if (bnYear.equals("1444")) {
            bnYear = "১৪৪৪";
        } else if (bnYear.equals("1445")) {
            bnYear = "১৪৪৫";
        } else if (bnYear.equals("1446")) {
            bnYear = "১৪৪৬";
        } else if (bnYear.equals("1447")) {
            bnYear = "১৪৪৭";
        } else if (bnYear.equals("1448")) {
            bnYear = "১৪৪৮";
        } else if (bnYear.equals("1449")) {
            bnYear = "১৪৪৯";
        } else if (bnYear.equals("1450")) {
            bnYear = "১৪৫০";
        } else if (bnYear.equals("1451")) {
            bnYear = "১৪৫১";
        } else if (bnYear.equals("1452")) {
            bnYear = "১৪৫২";
        } else if (bnYear.equals("1453")) {
            bnYear = "১৪৫৩";
        } else if (bnYear.equals("1454")) {
            bnYear = "১৪৫৪";
        } else if (bnYear.equals("1455")) {
            bnYear = "১৪৫৫";
        } else if (bnYear.equals("1456")) {
            bnYear = "১৪৫৬";
        } else if (bnYear.equals("1457")) {
            bnYear = "১৪৫৭";
        } else if (bnYear.equals("1458")) {
            bnYear = "১৪৫৮";
        } else if (bnYear.equals("1459")) {
            bnYear = "১৪৫৯";
        } else if (bnYear.equals("1460")) {
            bnYear = "১৪৬০";
        }
    }

    private void dateConvert() {

        if (bnDate.equals("01")) {
            bnDate = "০১";
        } else if (bnDate.equals("02")) {
            bnDate = "০২";

        } else if (bnDate.equals("03")) {
            bnDate = "০৩";

        } else if (bnDate.equals("04")) {
            bnDate = "০৪";

        } else if (bnDate.equals("05")) {
            bnDate = "০৫";

        } else if (bnDate.equals("06")) {
            bnDate = "০৬";

        } else if (bnDate.equals("07")) {
            bnDate = "০৭";

        } else if (bnDate.equals("08")) {
            bnDate = "০৮";

        } else if (bnDate.equals("09")) {
            bnDate = "০৯";

        } else if (bnDate.equals("10")) {
            bnDate = "১০";

        } else if (bnDate.equals("11")) {
            bnDate = "১১";

        } else if (bnDate.equals("12")) {
            bnDate = "১২";

        } else if (bnDate.equals("13")) {
            bnDate = "১৩";

        } else if (bnDate.equals("14")) {
            bnDate = "১৪";

        } else if (bnDate.equals("15")) {
            bnDate = "১৫";

        } else if (bnDate.equals("16")) {
            bnDate = "১৬";

        } else if (bnDate.equals("17")) {
            bnDate = "১৭";

        } else if (bnDate.equals("18")) {
            bnDate = "১৮";

        } else if (bnDate.equals("19")) {
            bnDate = "১৯";

        } else if (bnDate.equals("20")) {
            bnDate = "২০";

        } else if (bnDate.equals("21")) {
            bnDate = "২১";

        } else if (bnDate.equals("22")) {
            bnDate = "২২";

        } else if (bnDate.equals("23")) {
            bnDate = "২৩";

        } else if (bnDate.equals("24")) {
            bnDate = "২৪";

        } else if (bnDate.equals("25")) {
            bnDate = "২৫";

        } else if (bnDate.equals("26")) {
            bnDate = "২৬";

        } else if (bnDate.equals("27")) {
            bnDate = "২৭";

        } else if (bnDate.equals("28")) {
            bnDate = "২৮";

        } else if (bnDate.equals("29")) {
            bnDate = "২৯";

        } else if (bnDate.equals("30")) {
            bnDate = "৩০";

        } else if (bnDate.equals("31")) {
            bnDate = "৩১";

        }


    }


    private void monthConvert() {
        if (bnMonth.equals("Jumada I")) {
            bnMonth = "জুমাদিউল আউয়াল";

        } else if (bnMonth.equals("Jumada II")) {
            bnMonth = "জুমাদিউল সানী";

        } else if (bnMonth.equals("Rajab")) {
            bnMonth = "রজব";

        } else if (bnMonth.equals("Shaʻban")) {
            bnMonth = "শা'বান";

        } else if (bnMonth.equals("Ramadan")) {
            bnMonth = "রমাদান";

        } else if (bnMonth.equals("Shawwal")) {
            bnMonth = "শাওয়াল";
        } else if (bnMonth.equals("Dhuʻl-Qiʻdah")) {
            bnMonth = "জ্বিলক্বদ";
        } else if (bnMonth.equals("Dhuʻl-Hijjah")) {
            bnMonth = "জ্বিলহজ্জ";
        } else if (bnMonth.equals("Muharram")) {
            bnMonth = "মুহাররম";

        } else if (bnMonth.equals("Safar")) {
            bnMonth = "সফর";

        } else if (bnMonth.equals("Rabiʻ I")) {
            bnMonth = "রবিউল আউয়াল";

        } else if (bnMonth.equals("Rabiʻ II")) {
            bnMonth = "রবিউস সানী";
        }

    }


    //hijri conver end


    //eng to bangla convert

    private void en2bnDayConvert() {

        if (en2bnDay.equals("Sat")) {
            en2bnDay = "শনিবার";
        } else if (en2bnDay.equals("Sun")) {
            en2bnDay = "রবিবার";
        } else if (en2bnDay.equals("Mon")) {
            en2bnDay = "সোমবার";
        } else if (en2bnDay.equals("Tue")) {
            en2bnDay = "মঙ্গলবার";
        } else if (en2bnDay.equals("Wed")) {
            en2bnDay = "বুধবার";
        } else if (en2bnDay.equals("Thu")) {
            en2bnDay = "বৃহস্পতিবার";
        } else if (en2bnDay.equals("Fri")) {
            en2bnDay = "শুক্রবার";
        }

    }

    private void en2bnYearConvert() {

        if (en2bnYear.equals("2019")) {
            en2bnYear = "২০১৯";
        } else if (en2bnYear.equals("2020")) {
            en2bnYear = "২০২০";
        } else if (en2bnYear.equals("2021")) {
            en2bnYear = "২০২১";
        } else if (en2bnYear.equals("2022")) {
            en2bnYear = "২০২২";
        } else if (en2bnYear.equals("2023")) {
            en2bnYear = "২০২৩";
        } else if (en2bnYear.equals("2024")) {
            en2bnYear = "২০২৪";
        } else if (en2bnYear.equals("2025")) {
            en2bnYear = "২০২৫";
        } else if (en2bnYear.equals("2026")) {
            en2bnYear = "২০২৬";
        } else if (en2bnYear.equals("2027")) {
            en2bnYear = "২০২৭";
        } else if (en2bnYear.equals("2028")) {
            en2bnYear = "২০২৮";
        } else if (en2bnYear.equals("2029")) {
            en2bnYear = "২০২৯";
        } else if (en2bnYear.equals("2030")) {
            en2bnYear = "২০৩০";
        }

    }

    private void en2bnMonthConvert() {

        if (en2bnMonth.equals("January")) {
            en2bnMonth = "জানুয়ারি";

        } else if (en2bnMonth.equals("February")) {
            en2bnMonth = "ফেব্রুয়ারি";

        } else if (en2bnMonth.equals("March")) {
            en2bnMonth = "মার্চ";

        } else if (en2bnMonth.equals("April")) {
            en2bnMonth = "এপ্রিল";

        } else if (en2bnMonth.equals("May")) {
            en2bnMonth = "মে";

        } else if (en2bnMonth.equals("June")) {
            en2bnMonth = "জুন";

        } else if (en2bnMonth.equals("July")) {
            en2bnMonth = "জুলাই";

        } else if (en2bnMonth.equals("August")) {
            en2bnMonth = "আগস্ট";

        } else if (en2bnMonth.equals("September")) {
            en2bnMonth = "সেপ্টেম্বর";

        } else if (en2bnMonth.equals("October")) {
            en2bnMonth = "অক্টোবর";

        } else if (en2bnMonth.equals("November")) {
            en2bnMonth = "নভেম্বর";

        } else if (en2bnMonth.equals("December")) {
            en2bnMonth = "ডিসেম্বর";

        }

    }

    private void en2bnDateConvert() {

        if (en2bnDate.equals("01")) {
            en2bnDate = "০১";
        } else if (en2bnDate.equals("02")) {
            en2bnDate = "০২";

        } else if (en2bnDate.equals("03")) {
            en2bnDate = "০৩";

        } else if (en2bnDate.equals("04")) {
            en2bnDate = "০৪";

        } else if (en2bnDate.equals("05")) {
            en2bnDate = "০৫";

        } else if (en2bnDate.equals("06")) {
            en2bnDate = "০৬";

        } else if (en2bnDate.equals("07")) {
            en2bnDate = "০৭";

        } else if (en2bnDate.equals("08")) {
            en2bnDate = "০৮";

        } else if (en2bnDate.equals("09")) {
            en2bnDate = "০৯";

        } else if (en2bnDate.equals("10")) {
            en2bnDate = "১০";

        } else if (en2bnDate.equals("11")) {
            en2bnDate = "১১";

        } else if (en2bnDate.equals("12")) {
            en2bnDate = "১২";

        } else if (en2bnDate.equals("13")) {
            en2bnDate = "১৩";

        } else if (en2bnDate.equals("14")) {
            en2bnDate = "১৪";

        } else if (en2bnDate.equals("15")) {
            en2bnDate = "১৫";

        } else if (en2bnDate.equals("16")) {
            en2bnDate = "১৬";

        } else if (en2bnDate.equals("17")) {
            en2bnDate = "১৭";

        } else if (en2bnDate.equals("18")) {
            en2bnDate = "১৮";

        } else if (en2bnDate.equals("19")) {
            en2bnDate = "১৯";

        } else if (en2bnDate.equals("20")) {
            en2bnDate = "২০";

        } else if (en2bnDate.equals("21")) {
            en2bnDate = "২১";

        } else if (en2bnDate.equals("22")) {
            en2bnDate = "২২";

        } else if (en2bnDate.equals("23")) {
            en2bnDate = "২৩";

        } else if (en2bnDate.equals("24")) {
            en2bnDate = "২৪";

        } else if (en2bnDate.equals("25")) {
            en2bnDate = "২৫";

        } else if (en2bnDate.equals("26")) {
            en2bnDate = "২৬";

        } else if (en2bnDate.equals("27")) {
            en2bnDate = "২৭";

        } else if (en2bnDate.equals("28")) {
            en2bnDate = "২৮";

        } else if (en2bnDate.equals("29")) {
            en2bnDate = "২৯";

        } else if (en2bnDate.equals("30")) {
            en2bnDate = "৩০";

        } else if (en2bnDate.equals("31")) {
            en2bnDate = "৩১";

        }


    }

    //eng to bangla convert end

    private void getCurrentLoaction() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            Double latitude = location.getLatitude();
                            Double longitude = location.getLongitude();

                            //  Toast.makeText(getApplicationContext(), "Latitude: " + latitude + "\n" + "Longitude :" + longitude, Toast.LENGTH_SHORT).show();

                            try {
                                Geocoder geocoder = new Geocoder(getApplicationContext());
                                List<Address> addresses = null;
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                country = addresses.get(0).getCountryName();
                                city = addresses.get(0).getLocality();
                                district = addresses.get(0).getSubAdminArea();
                                String state = addresses.get(0).getAdminArea();


                                //searchLocation = city + ", " + country;


                                if (district != null) {

                                    checkDistrict();

                                }

                                if (city2 != null) {
                                    searchLocation = city2;
                                } else if (country != null) {
                                    searchLocation = country;
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                                //Toast.makeText(getApplicationContext(), "Error" + e, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });


    }

    private void checkDistrict() {

        if (district.equals("Barguna District")) {
            city2 = "Barguna";
            bnCity = "বরগুনা";
        } else if (district.equals("Barisal District")) {
            city2 = "Barisal";
            bnCity = "বরিশাল";
        } else if (district.equals("Bhola District")) {
            city2 = "Bhola";
            bnCity = "ভোলা";
        } else if (district.equals("Jhalokati District")) {
            city2 = "Jhalokati";
            bnCity = "ঝালকাঠি";
        } else if (district.equals("Patuakhali District")) {
            city2 = "Patuakhali";
            bnCity = "পটুয়াখালি";
        } else if (district.equals("Pirojpur District")) {
            city2 = "Pirojpur";
            bnCity = "পিরোজপুর";
        } else if (district.equals("Bandarban District")) {
            city2 = "Bandarban";
            bnCity = "বান্দরবান";
        } else if (district.equals("Brahmanbaria District")) {
            city2 = "Brahmanbaria";
            bnCity = "ব্রাহ্মণবাড়িয়া";
        } else if (district.equals("Chandpur District")) {
            city2 = "Chandpur";
            bnCity = "চাঁদপুর";
        } else if (district.equals("Chittagong District")) {
            city2 = "Chittagong";
            bnCity = "চট্টগ্রাম";
        } else if (district.equals("Comilla District")) {
            city2 = "Comilla";
            bnCity = "কুমিল্লা";
        } else if (district.equals("Cox's Bazar District")) {
            city2 = "Cox's Bazar";
            bnCity = "কক্সবাজার";
        } else if (district.equals("Feni District")) {
            city2 = "Feni";
            bnCity = "ফেনী";
        } else if (district.equals("Khagrachhari District")) {
            city2 = "Khagrachhari";
            bnCity = "খাগড়াছড়ি";
        } else if (district.equals("Lakshmipur District")) {
            city2 = "Lakshmipur";
            bnCity = "লক্ষ্মীপুর";
        } else if (district.equals("Noakhali District")) {
            city2 = "Noakhali";
            bnCity = "নোয়াখালি";
        } else if (district.equals("Rangamati District")) {
            city2 = "Rangamati";
            bnCity = "রাঙ্গামাটি";
        } else if (district.equals("Dhaka District")) {
            city2 = "Dhaka";
            bnCity = "ঢাকা";
        } else if (district.equals("Faridpur District")) {
            city2 = "Faridpur";
            bnCity = "ফরিদপুর";
        } else if (district.equals("Gazipur District")) {
            city2 = "Gazipur";
            bnCity = "গাজীপুর";
        } else if (district.equals("Gopalganj District")) {
            city2 = "Gopalganj";
            bnCity = "গোপালগঞ্জ";
        } else if (district.equals("Kishoreganj District")) {
            city2 = "Kishoreganj";
            bnCity = "কিশোরগঞ্জ";
        } else if (district.equals("Madaripur District")) {
            city2 = "Madaripur";
            bnCity = "মাদারীপুর";
        } else if (district.equals("Manikganj District")) {
            city2 = "Manikganj";
            bnCity = "মানিকগঞ্জ";
        } else if (district.equals("Munshiganj District")) {
            city2 = "Munshiganj";
            bnCity = "মুন্সিগঞ্জ";
        } else if (district.equals("Narayanganj District")) {
            city2 = "Narayanganj";
            bnCity = "নারায়ণগঞ্জ";
        } else if (district.equals("Narsingdi District")) {
            city2 = "Narsingdi";
            bnCity = "নরসিংদি";
        } else if (district.equals("Rajbari District")) {
            city2 = "Rajbari";
            bnCity = "রাজবাড়ি";
        } else if (district.equals("Shariatpur District")) {
            city2 = "Shariatpur";
            bnCity = "শরিয়তপুর";
        } else if (district.equals("Tangail District")) {
            city2 = "Tangail";
            bnCity = "টাঙ্গাইল";
        } else if (district.equals("Bagerhat District")) {
            city2 = "Bagerhat";
            bnCity = "বাগেরহাট";
        } else if (district.equals("Chuadanga District")) {
            city2 = "Chuadanga";
            bnCity = "চুয়াডাঙ্গা";
        } else if (district.equals("Jessore District")) {
            city2 = "Jessore";
            bnCity = "যশোর";
        } else if (district.equals("Jhenaidah District")) {
            city2 = "Jhenaidah";
            bnCity = "ঝিনাইদহ";
        } else if (district.equals("Khulna District")) {
            city2 = "Khulna";
            bnCity = "খুলনা";
        } else if (district.equals("Kushtia District")) {
            city2 = "Kushtia";
            bnCity = "কুষ্টিয়া";
        } else if (district.equals("Magura District")) {
            city2 = "Magura";
            bnCity = "মাগুরা";
        } else if (district.equals("Meherpur District")) {
            city2 = "Meherpur";
            bnCity = "মেহেরপুর";
        } else if (district.equals("Narail District")) {
            city2 = "Narail";
            bnCity = "নড়াইল";
        } else if (district.equals("Satkhira District")) {
            city2 = "Satkhira";
            bnCity = "সাতক্ষীরা";
        } else if (district.equals("Jamalpur District")) {
            city2 = "Jamalpur";
            bnCity = "জামালপুর";
        } else if (district.equals("Mymensingh District")) {
            city2 = "Mymensingh";
            bnCity = "ময়মনসিংহ";
        } else if (district.equals("Netrokona District")) {
            city2 = "Netrokona";
            bnCity = "নেত্রকোনা";
        } else if (district.equals("Sherpur District")) {
            city2 = "Sherpur";
            bnCity = "শেরপুর";
        } else if (district.equals("Bogra District")) {
            city2 = "Bogra";
            bnCity = "বগুড়া";
        } else if (district.equals("Joypurhat District")) {
            city2 = "Joypurhat";
            bnCity = "জয়পুরহাট";
        } else if (district.equals("Naogaon District")) {
            city2 = "Naogaon";
            bnCity = "নওগাঁ";
        } else if (district.equals("Natore District")) {
            city2 = "Natore";
            bnCity = "নাটোর";
        } else if (district.equals("Chapainawabganj District")) {
            city2 = "Chapainawabganj";
            bnCity = "চাঁপাইনবাবগঞ্জ";
        } else if (district.equals("Pabna District")) {
            city2 = "Pabna";
            bnCity = "পাবনা";
        } else if (district.equals("Rajshahi District")) {
            city2 = "Rajshahi";
            bnCity = "রাজশাহী";
        } else if (district.equals("Sirajganj District")) {
            city2 = "Sirajganj";
            bnCity = "সিরাজগঞ্জ";
        } else if (district.equals("Dinajpur District")) {
            city2 = "Dinajpur";
            bnCity = "দিনাজপুর";
        } else if (district.equals("Gaibandha District")) {
            city2 = "Gaibandha";
            bnCity = "গাইবান্ধা";
        } else if (district.equals("Kurigram District")) {
            city2 = "Kurigram";
            bnCity = "কুড়িগ্রাম";
        } else if (district.equals("Lalmonirhat District")) {
            city2 = "Lalmonirhat";
            bnCity = "লালমনিরহাট";
        } else if (district.equals("Nilphamari District")) {
            city2 = "Nilphamari";
            bnCity = "নীলফামারী";
        } else if (district.equals("Panchagarh District")) {
            city2 = "Panchagarh";
            bnCity = "পঞ্চগড়";
        } else if (district.equals("Rangpur District")) {
            city2 = "Rangpur";
            bnCity = "রংপুর";
        } else if (district.equals("Thakurgaon District")) {
            city2 = "Thakurgaon";
            bnCity = "ঠাকুরগাঁও";
        } else if (district.equals("Habiganj District")) {
            city2 = "Habiganj";
            bnCity = "হবিগঞ্জ";
        } else if (district.equals("Moulvibazar District")) {
            city2 = "Moulvibazar";
            bnCity = "মৌলভীবাজার";
        } else if (district.equals("Sunamganj District")) {
            city2 = "Sunamganj";
            bnCity = "সুনামগঞ্জ";
        } else if (district.equals("Sylhet District")) {
            city2 = "Sylhet";
            bnCity = "সিলেট";
        }

    }


    private void jsonData() {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            //get location
                            country = response.get("country").toString();
                            // String countryCode = response.get("country_code").toString();
                            city = response.get("city").toString();
                            String location = city + ", " + country;

                            String appNeme = getString(R.string.app_name);

                            //get Date
                            String date = response.getJSONArray("items").getJSONObject(0).get("date_for").toString();

                            //get Salat Time
                            fazarTime = response.getJSONArray("items").getJSONObject(0).get("fajr").toString();
                            dhuhrTime = response.getJSONArray("items").getJSONObject(0).get("dhuhr").toString();
                            asarTime = response.getJSONArray("items").getJSONObject(0).get("asr").toString();
                            magribTime = response.getJSONArray("items").getJSONObject(0).get("maghrib").toString();
                            ishaTime = response.getJSONArray("items").getJSONObject(0).get("isha").toString();
                            sunriseTime = response.getJSONArray("items").getJSONObject(0).get("shurooq").toString();

                            mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("Fazar", response.getJSONArray("items").getJSONObject(0).get("fajr").toString());
                            editor.putString("Dhuhr", response.getJSONArray("items").getJSONObject(0).get("dhuhr").toString());
                            editor.putString("Asar", response.getJSONArray("items").getJSONObject(0).get("asr").toString());
                            editor.putString("Magrib", response.getJSONArray("items").getJSONObject(0).get("maghrib").toString());
                            editor.putString("Isha", response.getJSONArray("items").getJSONObject(0).get("isha").toString());
                            editor.putString("Sunrise", response.getJSONArray("items").getJSONObject(0).get("shurooq").toString());
                            editor.putString("Location", location);
                            editor.putString("Country", response.get("country").toString());
                            editor.putString("City", response.get("city").toString());
                            editor.putString("City2", city2);


                            editor.apply();

                            citycheck();


                            currentCity.setText("নামাজের সময়সূচী- " + bnCity + ", " + bnCountry);


                            fazarTm.setText(fazarTime + " থেকে");
                            fazarTmEnd.setText(sunriseTime);

                            dhuhrTm.setText(dhuhrTime + " থেকে");
                            dhuhrTmEnd.setText(asarTime);

                            asarTm.setText(asarTime + " থেকে");


                            magribTm.setText(magribTime + " থেকে");
                            magribTmEnd.setText(ishaTime);

                            ishaTm.setText(ishaTime + " থেকে");


                            sunsetTm.setText("সূর্যাস্ত : " + magribTime);
                            sunriseTm.setText("সূর্যোদয় : " + sunriseTime);
                            ifterTime.setText("ইফতারের সময় \n" + magribTime);


                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

                                LocalTime datetime = LocalTime.parse(fazarTime.toUpperCase(), formatter);
                                datetime = datetime.minusMinutes(2);
                                seheriTm = datetime.format(formatter);


                                LocalTime timemagrib = LocalTime.parse(magribTime.toUpperCase(), formatter);
                                timemagrib = timemagrib.minusMinutes(1);
                                asarEndTime = timemagrib.format(formatter);

                                LocalTime timefajr = LocalTime.parse(fazarTime.toUpperCase(), formatter);
                                timefajr = timefajr.minusMinutes(2);
                                ishaEndTime = timefajr.format(formatter);


                                asarTmEnd.setText(asarEndTime);
                                ishaTmEnd.setText(ishaEndTime);
                                seheriTime.setText("সেহেরীর শেষ সময় \n" + seheriTm);
                            } else {

                                seheriTime.setText("সেহেরীর শেষ সময় \n" + fazarTime);
                                asarTmEnd.setText(magribTime);
                                ishaTmEnd.setText(fazarTime);
                            }


                            salatNameControl();


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        // pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                // pDialog.hide();
                // hide the progress dialog
                //  Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void citycheck() {

        if (city != null) {

            if (city.equals("Dhaka")) {
                bnCity = "ঢাকা";
            } else if (city.equals("Barisal")) {
                bnCity = "বরিশাল";
            } else if (city.equals("Khulna")) {
                bnCity = "খুলনা";
            } else if (city.equals("Rajshahi")) {
                bnCity = "রাজশাহী";
            } else if (city.equals("Rangpur")) {
                bnCity = "রংপুর";
            } else if (city.equals("Chittagong")) {
                bnCity = "চট্টগ্রাম";
            } else if (city.equals("Sylhet")) {
                bnCity = "সিলেট";
            } else if (city.equals("Mymensingh")) {
                bnCity = "ময়মনসিংহ";
            } else if (city.equals("Comilla")) {
                bnCity = "কুমিল্লা";
            } else if (city.equals("Feni")) {
                bnCity = "ফেনী";
            } else if (city.equals("Brahmanbaria")) {
                bnCity = "ব্রাহ্মানবারিয়া";
            } else if (city.equals("Rangamati")) {
                bnCity = "রাঙ্গামাটি";
            } else if (city.equals("Noakhali")) {
                bnCity = "নোয়াখালী";
            } else if (city.equals("Chandpur")) {
                bnCity = "চাঁদপুর";
            } else if (city.equals("Lakshmipur")) {
                bnCity = "লক্ষ্মীপুর";
            } else if (city.equals("Cox's Bazar")) {
                bnCity = "কক্সবাজার";
            } else if (city.equals("khagrachari")) {
                bnCity = "খাগড়াছড়ি";
            } else if (city.equals("Bandarban")) {
                bnCity = "বান্দরবান";
            } else if (city.equals("Sirajganj")) {
                bnCity = "সিরাজগঞ্জ";
            } else if (city.equals("Pabna")) {
                bnCity = "পাবনা";
            } else if (city.equals("Bogra")) {
                bnCity = "বগুড়া";
            } else if (city.equals("Natore")) {
                bnCity = "নাটোর";
            } else if (city.equals("Joypurhat")) {
                bnCity = "জয়পুরহাট";
            } else if (city.equals("Chapai Nawabganj")) {
                bnCity = "চাঁপাই নবাবগঞ্জ";
            } else if (city.equals("Naogaon")) {
                bnCity = "নওগাঁ";
            } else if (city.equals("Jessore")) {
                bnCity = "যশোর";
            } else if (city.equals("Satkhira")) {
                bnCity = "সাতক্ষীরা";
            } else if (city.equals("Meherpur")) {
                bnCity = "মেহেরপুর";
            } else if (city.equals("Narail")) {
                bnCity = "নড়াইল";
            } else if (city.equals("Chuadanga")) {
                bnCity = "চুয়াডাঙ্গা";
            } else if (city.equals("Kushtia")) {
                bnCity = "কুষ্টিয়া";
            } else if (city.equals("Magura")) {
                bnCity = "মাগুরা";
            } else if (city.equals("Bagerhat")) {
                bnCity = "বাগেরহাট";
            } else if (city.equals("Jhenaidah")) {
                bnCity = "ঝিনাইদাহ";
            } else if (city.equals("Jhalokati")) {
                bnCity = "ঝালকাঠি";
            } else if (city.equals("Patuakhali")) {
                bnCity = "পটুয়াখালী";
            } else if (city.equals("Pirojpur")) {
                bnCity = "পিরোজপুর";
            } else if (city.equals("Bhola")) {
                bnCity = "ভোলা";
            } else if (city.equals("Barguna")) {
                bnCity = "বরগুনা";
            } else if (city.equals("Moulvibazar")) {
                bnCity = "মৌলভীবাজার";
            } else if (city.equals("Habiganj")) {
                bnCity = "হবিগঞ্জ";
            } else if (city.equals("Sunamganj")) {
                bnCity = "সুনামগঞ্জ";
            } else if (city.equals("Narsingdi")) {
                bnCity = "নরসিংদী";
            } else if (city.equals("Gazipur")) {
                bnCity = "গাজীপুর";
            } else if (city.equals("Shariatpur")) {
                bnCity = "শরিয়াতপুর";
            } else if (city.equals("Narayanganj")) {
                bnCity = "নারায়ণগঞ্জ";
            } else if (city.equals("Tangail")) {
                bnCity = "টাঙ্গাইল";
            } else if (city.equals("Kishoreganj")) {
                bnCity = "কিশোরগঞ্জ";
            } else if (city.equals("Manikganj")) {
                bnCity = "মানিকগঞ্জ";
            } else if (city.equals("Munshiganj")) {
                bnCity = "মুন্সিগঞ্জ";
            } else if (city.equals("Rajbari")) {
                bnCity = "রাজবাড়ী";
            } else if (city.equals("Madaripur")) {
                bnCity = "মাদারীপুর";
            } else if (city.equals("Gopalganj")) {
                bnCity = "গোপালগঞ্জ";
            } else if (city.equals("Faridpur")) {
                bnCity = "ফরিদপুর";
            } else if (city.equals("Panchagarh")) {
                bnCity = "পঞ্চগড়";
            } else if (city.equals("Dinajpur")) {
                bnCity = "দিনাজপুর";
            } else if (city.equals("Lalmonirhat")) {
                bnCity = "লালমনিরহাট";
            } else if (city.equals("Nilphamari")) {
                bnCity = "নীলফামারী";
            } else if (city.equals("Gaibandha")) {
                bnCity = "গাইবান্ধা";
            } else if (city.equals("Thakurgaon")) {
                bnCity = "ঠাকুরগাঁও";
            } else if (city.equals("Kurigram")) {
                bnCity = "কুড়িগ্রাম";
            } else if (city.equals("Sherpur")) {
                bnCity = "শেরপুর";
            } else if (city.equals("Jamalpur")) {
                bnCity = "জামালপুর";
            } else if (city.equals("Netrokona")) {
                bnCity = "নেত্রকোনা";
            }

        }


    }


    private void salatNameControl() {


        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd");
            String cd = sdf.format(new Date());

            int currentDate = Integer.parseInt(cd);
            int finaldate = currentDate + 1;

            SimpleDateFormat parser = new SimpleDateFormat("h:mm a");

            Date userDate = parser.parse(currentTime);


            if (userDate.after(parser.parse(fazarTime))) {
                salatName.setText("যোহর");
                time = dhuhrTime;

                dhuhrLayout.setBackgroundColor(getResources().getColor(R.color.salatNameColor));
                asarLayout.setBackgroundColor(getResources().getColor(R.color.white));
                magribLayout.setBackgroundColor(getResources().getColor(R.color.white));
                ishaLayout.setBackgroundColor(getResources().getColor(R.color.white));
                fajarLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }

            if (userDate.after(parser.parse(dhuhrTime))) {

                salatName.setText("আসর");
                time = asarTime;

                dhuhrLayout.setBackgroundColor(getResources().getColor(R.color.white));
                asarLayout.setBackgroundColor(getResources().getColor(R.color.salatNameColor));
                magribLayout.setBackgroundColor(getResources().getColor(R.color.white));
                ishaLayout.setBackgroundColor(getResources().getColor(R.color.white));
                fajarLayout.setBackgroundColor(getResources().getColor(R.color.white));

            }
            if (userDate.after(parser.parse(asarTime))) {

                salatName.setText("মাগরিব");
                time = magribTime;

                dhuhrLayout.setBackgroundColor(getResources().getColor(R.color.white));
                asarLayout.setBackgroundColor(getResources().getColor(R.color.white));
                magribLayout.setBackgroundColor(getResources().getColor(R.color.salatNameColor));
                ishaLayout.setBackgroundColor(getResources().getColor(R.color.white));
                fajarLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }

            if (userDate.after(parser.parse(magribTime))) {

                salatName.setText("ইশা");
                time = ishaTime;

                dhuhrLayout.setBackgroundColor(getResources().getColor(R.color.white));
                asarLayout.setBackgroundColor(getResources().getColor(R.color.white));
                magribLayout.setBackgroundColor(getResources().getColor(R.color.white));
                ishaLayout.setBackgroundColor(getResources().getColor(R.color.salatNameColor));
                fajarLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }

            if (userDate.after(parser.parse(ishaTime)) || userDate.before(parser.parse(fazarTime))) {


                salatName.setText("ফজর");
                time = fazarTime;
                fazarDate = finaldate;

                dhuhrLayout.setBackgroundColor(getResources().getColor(R.color.white));
                asarLayout.setBackgroundColor(getResources().getColor(R.color.white));
                magribLayout.setBackgroundColor(getResources().getColor(R.color.white));
                ishaLayout.setBackgroundColor(getResources().getColor(R.color.white));
                fajarLayout.setBackgroundColor(getResources().getColor(R.color.salatNameColor));

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            shoAlert();

            // super.onBackPressed();

        }
    }

    private void shoAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_alert)
                //set title
                .setTitle("আপনি কি বের হতে চান ?")
                //set message
                .setMessage(getString(R.string.dialog))
                //set positive button
                .setPositiveButton("বাদ দিন", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //set what would happen when positive button is clicked
                        dialog.cancel();
                    }
                })
                //set negative button
                .setNegativeButton("বাহির", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //set what should happen when negative button is clicked
                        MainActivity.super.onBackPressed();
                    }
                })

                .setNeutralButton("রেটিং দিন", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));

                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.gps) {

            //  getUpdateTime();

            if (haveNetworkConnection()) {

                pDialog = new ProgressDialog(this);
                pDialog.setMessage("নামাজের সময়সূচী লোড হচ্ছে......");
                pDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getUpdateTime();
                        pDialog.dismiss();
                    }
                }, 2000);


                noInternet.setVisibility(View.GONE);
            } else {

                noInternet.setVisibility(View.VISIBLE);
                noInternet.setText(getString(R.string.noInternet));
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUpdateTime() {


        url = "https://muslimsalat.com/" + searchLocation + ".json?key=a207cd17473c3b323447d41a5f471601";

        jsonData();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            //  startActivity(new Intent(MainActivity.this, PrayerTime.class));
        } else if (id == R.id.ramadanCalender) {

           // Toast.makeText(getApplicationContext(), "পরবর্তী আপডেটের জন্য অপেক্ষা করুন", Toast.LENGTH_LONG).show();

            if (haveNetworkConnection()) {

                startActivity(new Intent(getApplicationContext(), MonthlyTime.class));
            } else {

                alret();
            }


        } else if (id == R.id.qibla_compass) {
            startActivity(new Intent(getApplicationContext(), Compass.class));

        } else if (id == R.id.tasbih) {
            startActivity(new Intent(getApplicationContext(), Tasbih.class));

        } else if (id == R.id.nav_share) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, appUrl);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "শেয়ার করুন"));


        } else if (id == R.id.rate) {

            appUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(appUrl));
            startActivity(i);

        } else if (id == R.id.quran) {

            appUrl = "https://play.google.com/store/apps/details?id=" + "com.nrsoftbd.quran";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(appUrl));
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void alret() {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_alert)
                //set title
                //.setTitle("ইন্টারনেট সংযোগ পাওয়া যা)
                //set message
                .setMessage(getString(R.string.noInternet2))
                //set positive button
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //set what would happen when positive button is clicked
                        dialog.cancel();
                    }
                })

                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getCurrentLoaction();
                if (haveNetworkConnection()) {
                    pDialog = new ProgressDialog(this);
                    pDialog.setMessage("নামাজের সময়সূচী লোড হচ্ছে......");
                    pDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getUpdateTime();
                            pDialog.dismiss();
                        }
                    }, 3000);

                } else {
                    Toast.makeText(getApplicationContext(), "Please connect to Internet", Toast.LENGTH_LONG).show();
                }

            } else {

                Toast.makeText(getApplicationContext(), "Permission Not Grant", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
