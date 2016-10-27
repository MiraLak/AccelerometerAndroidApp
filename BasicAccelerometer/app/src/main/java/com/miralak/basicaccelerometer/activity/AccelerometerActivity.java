package com.miralak.basicaccelerometer.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.miralak.basicaccelerometer.R;
import com.miralak.basicaccelerometer.api.CassandraRestApi;
import com.miralak.basicaccelerometer.model.Acceleration;

import java.util.Date;

import retrofit.RestAdapter;

public class AccelerometerActivity extends ActionBarActivity implements SensorEventListener{

    private String restURL;
    private TextView acceleration;
    private Button myStartButton;
    private Button myStopButton;

    private CassandraRestApi cassandraRestApi;

    private SensorManager sm;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        acceleration = (TextView) findViewById(R.id.acceleration);

        //Init accelerometer sensor
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        initRestApi();
        initActionButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accelerometer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Acceleration capturedAcceleration = getAccelerationFromSensor(event);
        updateTextView(capturedAcceleration);
        new SendAccelerationAsyncTask().execute(capturedAcceleration);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do nothing
    }

    /**
     * Init REST api to post data.
     */
    private void initRestApi() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            restURL = extras.getString(StartActivity.URL);
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(restURL)
                .build();

        cassandraRestApi = restAdapter.create(CassandraRestApi.class);
    }

    /**
     * Init start and stop buttons actions.
     */
    private void initActionButtons() {
        myStartButton = (Button) findViewById(R.id.button_start);
        myStopButton = (Button) findViewById(R.id.button_stop);

        myStartButton.setVisibility(View.VISIBLE);
        myStopButton.setVisibility(View.GONE);

        //Start button action on click
        myStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSensor();
                myStartButton.setVisibility(View.GONE);
                myStopButton.setVisibility(View.VISIBLE);
            }
        });

        //Stop button action on click
        myStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSensor();
                myStartButton.setVisibility(View.VISIBLE);
                myStopButton.setVisibility(View.GONE);
                finish();
            }
        });
    }

    private void startSensor() {
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopSensor() {
        sm.unregisterListener(this);
    }

    /**
     * Update acceleration text view with new values.
     * @param capturedAcceleration
     */
    private void updateTextView(Acceleration capturedAcceleration) {
        acceleration.setText("X:" + capturedAcceleration.getX() +
                "\nY:" + capturedAcceleration.getY() +
                "\nZ:" + capturedAcceleration.getZ() +
                "\nTimestamp:" + capturedAcceleration.getTimestamp());
    }

    /**
     * Get accelerometer sensor values and map it into an acceleration model.
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
    private class SendAccelerationAsyncTask extends AsyncTask<Acceleration, Void, Void>{

        @Override
        protected Void doInBackground(Acceleration... params) {
            try {
                cassandraRestApi.sendAccelerationValues(params[0]);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
