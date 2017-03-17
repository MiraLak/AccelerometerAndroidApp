package com.miralak.basicaccelerometer.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import duchess.fr.basicaccelerometer.R;


public class ConfigurationActivity extends AppCompatActivity {


    public static final String MY_CONFIG = "myConfiguration";
    public static final String URL = "serverURL";

    public static final String DEFAULT_URL = "http://localhost:8080/";

    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        sharedpreferences = getSharedPreferences(MY_CONFIG, Context.MODE_PRIVATE);
        String serverUrl = sharedpreferences.getString(ConfigurationActivity.URL, DEFAULT_URL);

        EditText serverUrlTextView = (EditText) findViewById(R.id.serverUrl);
        serverUrlTextView.setText(serverUrl);

        final Button saveConfigurationButton = (Button) findViewById(R.id.saveConfigurationButton);
        saveConfigurationButton.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(URL, serverUrlTextView.getText().toString());
            editor.apply();

            Toast.makeText(ConfigurationActivity.this, getString(R.string.info_configuration_saved), Toast.LENGTH_SHORT).show();
        });
    }
}

