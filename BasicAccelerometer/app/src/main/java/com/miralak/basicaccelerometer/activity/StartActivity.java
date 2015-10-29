package com.miralak.basicaccelerometer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.miralak.basicaccelerometer.R;

public class StartActivity extends ActionBarActivity {

    public static final String URL = "restURL";
    private EditText restURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        restURL = (EditText) findViewById(R.id.editURL);

        final Button myStartButton = (Button) findViewById(R.id.button_start);
        myStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, AccelerometerActivity.class);
                intent.putExtra(URL, restURL.getText().toString());
                startActivity(intent);
            }
        });

        Button myCollectButton = (Button) findViewById(R.id.button_collect);
        myCollectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, CollectDataActivity.class);
                intent.putExtra(URL, restURL.getText().toString());
                startActivity(intent);
            }
        });


    }
}
