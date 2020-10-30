package com.nrsoftbd.ramadan;

import android.content.pm.ActivityInfo;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Tasbih extends AppCompatActivity {

    public int counter = 0;
    public int counter2 = 0;
    private TextView textCounter, countTxt;
    private String count, count2;
    private final String TAG = Tasbih.class.getSimpleName();
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasbih);


        ads();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("তসবীহ");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("DDDA271394B93B7E1D42B5E127C171A1").build());


    }

    public void hitung(View v) {
        counter++;
        Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        textCounter = (TextView) findViewById(R.id.textCounter);
        count = "";

        convert_Bn();

        textCounter.setText(count);

        vibe.vibrate(50);

        if (counter == 33) {
            vibe.vibrate(2000);
        }
        if (counter == 34) {
            counter2++;
            countTxt = (TextView) findViewById(R.id.countTxt);
            count2 = "";
            countConvert();
            countTxt.setText(count2);
            countReset();
            loadAds();

        }
    }

    private void loadAds() {
        if (mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }


    private void countConvert() {

        if (counter2 == 0) {
            count2 = "০";
        } else if (counter2 == 1) {
            count2 = "১";
        } else if (counter2 == 2) {
            count2 = "২";
        } else if (counter2 == 3) {
            count2 = "৩";
        } else if (counter2 == 4) {
            count2 = "৪";
        } else if (counter2 == 5) {
            count2 = "৫";
        } else if (counter2 == 6) {
            count2 = "৬";
        } else if (counter2 == 7) {
            count2 = "৭";
        } else if (counter2 == 8) {
            count2 = "৮";
        } else if (counter2 == 9) {
            count2 = "৯";
        } else if (counter2 == 10) {
            count2 = "১০";
        } else if (counter2 == 11) {
            count2 = "১১";
        } else if (counter2 == 12) {
            count2 = "১২";
        } else if (counter2 == 13) {
            count2 = "১৩";
        } else if (counter2 == 14) {
            count2 = "১৪";
        } else if (counter2 == 15) {
            count2 = "১৫";
        } else if (counter2 == 16) {
            count2 = "১৬";
        } else if (counter2 == 17) {
            count2 = "১৭";
        } else if (counter2 == 18) {
            count2 = "১৮";
        } else if (counter2 == 19) {
            count2 = "১৯";
        } else if (counter2 == 20) {
            count2 = "২০";
        } else if (counter2 == 21) {
            count2 = "২১";
        } else if (counter2 == 22) {
            count2 = "২২";
        } else if (counter2 == 23) {
            count2 = "২৩";
        } else if (counter2 == 24) {
            count2 = "২৪";
        } else if (counter2 == 25) {
            count2 = "২৫";
        } else if (counter2 == 26) {
            count2 = "২৬";
        } else if (counter2 == 27) {
            count2 = "২৭";
        } else if (counter2 == 28) {
            count2 = "২৮";
        } else if (counter2 == 29) {
            count2 = "২৯";
        } else if (counter2 == 30) {
            count2 = "৩০";
        } else if (counter2 == 31) {
            count2 = "৩১";
        } else if (counter2 == 32) {
            count2 = "৩২";
        } else if (counter2 == 33) {
            count2 = "৩৩";
        }

    }

    private void countReset() {
        counter = 0;
        String recount = "০";
        textCounter = (TextView) findViewById(R.id.textCounter);
        textCounter.setText(recount);

    }

    private void convert_Bn() {

        if (counter == 0) {
            count = "০";
        } else if (counter == 1) {
            count = "১";
        } else if (counter == 2) {
            count = "২";
        } else if (counter == 3) {
            count = "৩";
        } else if (counter == 4) {
            count = "৪";
        } else if (counter == 5) {
            count = "৫";
        } else if (counter == 6) {
            count = "৬";
        } else if (counter == 7) {
            count = "৭";
        } else if (counter == 8) {
            count = "৮";
        } else if (counter == 9) {
            count = "৯";
        } else if (counter == 10) {
            count = "১০";
        } else if (counter == 11) {
            count = "১১";
        } else if (counter == 12) {
            count = "১২";
        } else if (counter == 13) {
            count = "১৩";
        } else if (counter == 14) {
            count = "১৪";
        } else if (counter == 15) {
            count = "১৫";
        } else if (counter == 16) {
            count = "১৬";
        } else if (counter == 17) {
            count = "১৭";
        } else if (counter == 18) {
            count = "১৮";
        } else if (counter == 19) {
            count = "১৯";
        } else if (counter == 20) {
            count = "২০";
        } else if (counter == 21) {
            count = "২১";
        } else if (counter == 22) {
            count = "২২";
        } else if (counter == 23) {
            count = "২৩";
        } else if (counter == 24) {
            count = "২৪";
        } else if (counter == 25) {
            count = "২৫";
        } else if (counter == 26) {
            count = "২৬";
        } else if (counter == 27) {
            count = "২৭";
        } else if (counter == 28) {
            count = "২৮";
        } else if (counter == 29) {
            count = "২৯";
        } else if (counter == 30) {
            count = "৩০";
        } else if (counter == 31) {
            count = "৩১";
        } else if (counter == 32) {
            count = "৩২";
        } else if (counter == 33) {
            count = "৩৩";
        }

    }

    public void reset(View v) {

        String counterTxt = "";
        String counter2Txt = "";
        counter = 0;
        counter2 = 0;

        textCounter = (TextView) findViewById(R.id.textCounter);
        countTxt = (TextView) findViewById(R.id.countTxt);

        if (counter == 0) {
            counterTxt = "০";
        }

        if (counter2 == 0) {
            counter2Txt = "০";
        }

        textCounter.setText(counterTxt);
        countTxt.setText(counter2Txt);


        //  String pesan = "Berhasil direset";
        // Toast notif = Toast.makeText(this, pesan, Toast.LENGTH_LONG);
        // notif.setGravity(Gravity.FILL_HORIZONTAL, 0, -500);
        //notif.show();
    }


}