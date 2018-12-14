package com.echessbee.hsb.paddleball;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ThemeScreen extends AppCompatActivity implements View.OnClickListener {

    Button bInverse, bNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_screen);

        bInverse = (Button) findViewById(R.id.bInverse);
        bNight = (Button) findViewById(R.id.bNight);

        bInverse.setOnClickListener(this);
        bNight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bInverse:
                startActivity(new Intent(ThemeScreen.this, InversePlay.class));
                break;

            case R.id.bNight:
                startActivity(new Intent(ThemeScreen.this, NightPlay.class));
                break;

        }
    }
}
