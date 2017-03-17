package com.miralak.basicaccelerometer.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.miralak.basicaccelerometer.api.CassandraRestApi;
import com.miralak.basicaccelerometer.api.CassandraRestApiClient;
import com.miralak.basicaccelerometer.model.Acceleration;

import java.util.Date;

import duchess.fr.basicaccelerometer.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private CassandraRestApi cassandraRestApi;

    private SensorManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        initRestApi();
        initActionButtons();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopSensor();
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Acceleration capturedAcceleration = getAccelerationFromSensor(event);
        updateTextView(capturedAcceleration);
        sendDataToCassandra(capturedAcceleration);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do nothing
    }

    /**
     * Init REST api to post data.
     */
    private void initRestApi() {
        SharedPreferences sharedpreferences = getSharedPreferences(ConfigurationActivity.MY_CONFIG, Context.MODE_PRIVATE);
        String restURL = sharedpreferences.getString(ConfigurationActivity.URL, ConfigurationActivity.DEFAULT_URL);
        cassandraRestApi = CassandraRestApiClient.getClient(restURL).create(CassandraRestApi.class);
    }

    /**
     * Init start and stop buttons actions.
     */
    private void initActionButtons() {
        Button myStartButton = (Button) findViewById(R.id.button_start);
        Button myStopButton = (Button) findViewById(R.id.button_stop);

        myStartButton.setVisibility(View.VISIBLE);
        myStopButton.setVisibility(View.GONE);

        //Start button action on click
        myStartButton.setOnClickListener(v -> {
            startSensor();
            myStartButton.setVisibility(View.GONE);
            myStopButton.setVisibility(View.VISIBLE);
        });

        //Stop button action on click
        myStopButton.setOnClickListener(v -> {
            stopSensor();
            myStartButton.setVisibility(View.VISIBLE);
            myStopButton.setVisibility(View.GONE);
        });
    }

    private void startSensor() {
        Sensor accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopSensor() {
        sm.unregisterListener(this);
    }

    /**
     * Update acceleration text view with new values.
     *
     * @param capturedAcceleration
     */
    private void updateTextView(Acceleration capturedAcceleration) {
        TextView acceleration = (TextView) findViewById(R.id.acceleration);
        acceleration.setText("X:" + capturedAcceleration.getX() +
                "\nY:" + capturedAcceleration.getY() +
                "\nZ:" + capturedAcceleration.getZ() +
                "\nTimestamp:" + capturedAcceleration.getTimestamp());
    }

    /**
     * Get accelerometer sensor values and map it into an acceleration model.
     *
     * @param event
     * @return an acceleration model.
     */
    private Acceleration getAccelerationFromSensor(SensorEvent event) {
        long timestamp = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
        return new Acceleration(event.values[0], event.values[1], event.values[2], timestamp);
    }


    /**
     * Asyncronous task to post request to a Rest API.
     */
    private void sendDataToCassandra(Acceleration capturedAcceleration) {


        Call<Void> call = cassandraRestApi.sendAccelerationValues(capturedAcceleration);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), getText(R.string.rest_error), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getBaseContext(), getText(R.string.rest_failure) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

