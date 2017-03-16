package com.miralak.basicaccelerometer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import duchess.fr.basicaccelerometer.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Button myStartButton = (Button) findViewById(R.id.button_start);
        myStartButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, AccelerometerActivity.class);
            startActivity(intent);
        });

        Button myCollectButton = (Button) findViewById(R.id.button_collect);
        myCollectButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, CollectDataActivity.class);
            startActivity(intent);
        });

        Button myConfigButton = (Button) findViewById(R.id.button_config);
        myConfigButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, ConfigurationActivity.class);
            startActivity(intent);
        });
    }
}
