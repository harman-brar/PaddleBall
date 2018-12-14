package com.echessbee.hsb.paddleball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Random;

public class Buffer extends AppCompatActivity implements View.OnClickListener {
    Button bRetry, bHome, bTheme;
    TextView fScore, fHigh, newHigh;
    InterstitialAd mInterstitialAd;
    int random;
    boolean notYetPlayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer);

        bRetry = (Button) findViewById(R.id.bRetry);
        bHome = (Button) findViewById(R.id.bHome);
        bTheme = (Button) findViewById(R.id.bTheme);

        bRetry.setOnClickListener(this);
        bHome.setOnClickListener(this);
        bTheme.setOnClickListener(this);

        fScore = (TextView) findViewById(R.id.fScore);
        fHigh = (TextView) findViewById(R.id.fHigh);
        newHigh = (TextView) findViewById(R.id.newHigh);

        String scores = getIntent().getStringExtra("scores");
        fScore.setText(scores);

        String highScore = getIntent().getStringExtra("highScore");
        fHigh.setText(highScore);

        String newHighS = getIntent().getStringExtra("newHighS");
        newHigh.setText(newHighS);

        MobileAds.initialize(this, "ca-app-pub-2657029667417496/6720266349");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2657029667417496/6720266349");
        //mInterstitialAd.loadAd(new AdRequest.Builder().build());

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        Random r = new Random();
        random = r.nextInt(3 - 1 + 1) + 1;

        notYetPlayed = true;

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                if (random == 1) {
                    if (notYetPlayed) {
                        showInterstitial();
                        notYetPlayed = false;
                    }
                }
            }

            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bRetry:
                startActivity(new Intent(Buffer.this, ThemeScreen.class));
                break;

            case R.id.bHome:
                startActivity(new Intent(Buffer.this, MainActivity.class));
                break;

            case R.id.bTheme:
                shareScore();
                break;

        }

    }

    private void shareScore() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "I challenge you to PaddleBall!");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Bet you can't beat my high-score in PaddleBall! Check it out on the Google Play Store: https://play.google.com/store/apps/details?id=com.echessbee.hsb.paddleball");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
