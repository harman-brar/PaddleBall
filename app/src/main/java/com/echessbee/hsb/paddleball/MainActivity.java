package com.echessbee.hsb.paddleball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button bPlay, bLeaderboard, bShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bPlay = (Button) findViewById(R.id.bPlay);
        //bLeaderboard = (Button) findViewById(R.id.bLeaderboard);
        bShare = (Button) findViewById(R.id.bShare);

        bPlay.setOnClickListener(this);
        //bLeaderboard.setOnClickListener(this);
        bShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPlay:
                startActivity(new Intent(MainActivity.this, ThemeScreen.class));
                break;

            case R.id.bShare:
                share();
                break;

            /*case R.id.bLeaderboard:

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Leaderboards and achievements coming soon!");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


                break;*/
        }
    }

    private void share() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "I challenge you to PaddleBall!");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Bet you can't beat my high-score in PaddleBall! Check it out on the Google Play Store: https://play.google.com/store/apps/details?id=com.echessbee.hsb.paddleball");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
