package com.nrsoftbd.ramadan;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Compass extends AppCompatActivity implements SensorEventListener{

    private float compCurrentDegree=0f;
    private float qiblaCurrentDegree = 0f;
    Spinner lang ;
    private ImageView compImg,qiblaImg ;
    private SensorManager sensor;
    double latitude,longitude;
    double meccaLatitude = 21.422483;
    double meccaLongitude = 39.826181;
    float qiblaAngle;
    float compDegree,qiblaDegree;
    private AdView mAdView;

    String testText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.qiblaCompass));

        ads();

        compImg = (ImageView)findViewById(R.id.compassImg);
        //qiblaImg =(ImageView)findViewById(R.id.qiblaImg);
        sensor = (SensorManager)getSystemService(SENSOR_SERVICE);

        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.getIsGPSTrackingEnabled()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            qiblaAngle = getQiblaAngle(latitude, longitude, meccaLatitude, meccaLongitude);


        }else {
           // gpsTracker.showSettingsAlert();
            //Toast.makeText(this, "toot", Toast.LENGTH_LONG).show();
        }

    }

    private void ads() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("DDDA271394B93B7E1D42B5E127C171A1").build();
        mAdView.loadAd(adRequest);
    }


    @Override
    protected void onPause(){

        super.onPause();
        sensor.unregisterListener((SensorEventListener) this);
    }
    @Override
    protected void onResume(){
        super.onResume();
        sensor.registerListener((SensorEventListener) this, sensor.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

    }

    //21.422483, 39.826181
    @Override
    public void onSensorChanged(SensorEvent event){

        compDegree = Math.round(event.values[0]);
        RotateAnimation compRotate = new RotateAnimation(compCurrentDegree ,-compDegree,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        compRotate.setDuration(210);
        compRotate.setFillAfter(true);
        compImg.startAnimation(compRotate);
        compCurrentDegree= -compDegree;

        qiblaDegree = Math.round(event.values[0])+qiblaAngle-90;
        RotateAnimation qiblaRotate = new RotateAnimation(qiblaCurrentDegree,-qiblaDegree,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        qiblaRotate.setDuration(210);
        qiblaRotate.setFillAfter(true);
       // qiblaImg.startAnimation(qiblaRotate);
        qiblaCurrentDegree = -qiblaDegree;
        /*TextView textView = (TextView)findViewById(R.id.testText);
        testText = "your lat is "+latitude+" & your long is "+longitude +" &your angle is "+qiblaAngle+" &degree is "+ Math.round(event.values[0]) ;
        textView.setText(testText);
        */

    }

    public float getQiblaAngle(double lat1,double long1,double lat2,double long2){
        double angle,dy,dx;
        dy = lat2 - lat1;
        dx = Math.cos(Math.PI/ 180 * lat1) * (long2 - long1);
        angle = Math.atan2(dy, dx);
        angle = Math.toDegrees(angle);
        return (float)angle;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}

