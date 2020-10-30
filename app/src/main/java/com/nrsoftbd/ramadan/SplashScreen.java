package com.nrsoftbd.ramadan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class SplashScreen extends AppCompatActivity {

    InterstitialAd mInterstitialAd;
    ProgressBar progressBar;
    TextView loadingtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        progressBar = (ProgressBar) findViewById(R.id.pb);
        loadingtxt = (TextView) findViewById(R.id.tv);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                doFanction();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mInterstitialAd.isLoaded()) {
                    progressBar.setVisibility(View.GONE);
                    loadingtxt.setVisibility(View.GONE);
                    mInterstitialAd.show();
                } else {
                    doFanction();
                }

            }
        }, 3000);

    }

    private void doFanction() {

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();


    }
}
