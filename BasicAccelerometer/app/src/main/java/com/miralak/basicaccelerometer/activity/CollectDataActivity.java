package com.miralak.basicaccelerometer.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.miralak.basicaccelerometer.api.CassandraRestApi;
import com.miralak.basicaccelerometer.api.CassandraRestApiClient;
import com.miralak.basicaccelerometer.model.Acceleration;
import com.miralak.basicaccelerometer.model.ActivityType;
import com.miralak.basicaccelerometer.model.TrainingAcceleration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import duchess.fr.basicaccelerometer.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectDataActivity extends AppCompatActivity implements SensorEventListener {

    private String userID;
    private String selectedActivity;
    private Timer timer;
    private TimerTask startTimerTask;
    private TimerTask stopTimerTask;

    private Spinner activitySpinner;

    private CassandraRestApi cassandraRestApi;

    private SensorManager sm;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        //Init accelerometer sensor
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        timer = new Timer();

        initActivitySpinner();
        initRestApi();
        initTimerTasksWithAlertSound();
        initActionButtons();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopSensor();
        startTimerTask.cancel();
        stopTimerTask.cancel();
        timer.cancel();
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

    private void initActionButtons() {
        Button myStartButton = (Button) findViewById(R.id.button_start_training);
        Button myStopButton = (Button) findViewById(R.id.button_stop_training);

        myStartButton.setVisibility(View.VISIBLE);
        myStopButton.setVisibility(View.GONE);

        myStartButton.setOnClickListener(v -> {
            userID = ((EditText) findViewById(R.id.userID)).getText().toString();
            selectedActivity = (String) activitySpinner.getSelectedItem();

            myStartButton.setVisibility(View.GONE);
            myStopButton.setVisibility(View.VISIBLE);

            //Sensor starts after 3 seconds
            timer.schedule(startTimerTask, 3000);

            //Sensor stops after 20 seconds
            timer.schedule(stopTimerTask, 20000);

        });


        myStopButton.setOnClickListener(v -> {
            stopSensor();
            startTimerTask.cancel();
            stopTimerTask.cancel();
            timer.cancel();
            new ToneGenerator(AudioManager.STREAM_ALARM, 100).startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 200);

            myStartButton.setVisibility(View.VISIBLE);
            myStopButton.setVisibility(View.GONE);

        });
    }

    private void initTimerTasksWithAlertSound() {
        final Handler myHandler = new Handler();
        final Runnable myRunnable = () -> {
            Button myStartButton = (Button) findViewById(R.id.button_start_training);
            Button myStopButton = (Button) findViewById(R.id.button_stop_training);

            myStartButton.setVisibility(View.VISIBLE);
            myStopButton.setVisibility(View.GONE);
        };

        startTimerTask = new TimerTask() {
            @Override
            public void run() {
                new ToneGenerator(AudioManager.STREAM_ALARM, 100).startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                startSensor();
            }
        };

        stopTimerTask = new TimerTask() {
            @Override
            public void run() {
                new ToneGenerator(AudioManager.STREAM_ALARM, 100).startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                startTimerTask.cancel();
                stopSensor();
                myHandler.post(myRunnable);
            }
        };
    }

    private void initRestApi() {
        SharedPreferences sharedpreferences = getSharedPreferences(ConfigurationActivity.MY_CONFIG, Context.MODE_PRIVATE);
        String restURL = sharedpreferences.getString(ConfigurationActivity.URL, ConfigurationActivity.DEFAULT_URL);

        cassandraRestApi = CassandraRestApiClient.getClient(restURL).create(CassandraRestApi.class);
    }

    private void initActivitySpinner() {
        activitySpinner = (Spinner) findViewById(R.id.spinner_activity);

        final List<String> activityList = new ArrayList<>();
        for (ActivityType activityType : ActivityType.values()) {
            activityList.add(activityType.getLabel());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                activityList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(adapter);

        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedActivity = activitySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedActivity = activityList.get(0);
            }

        });
    }

    private void startSensor() {
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopSensor() {
        sm.unregisterListener(this);
    }


    private void updateTextView(Acceleration capturedAcceleration) {
        TextView acceleration = (TextView) findViewById(R.id.acceleration);
        acceleration.setText("X:" + capturedAcceleration.getX() +
                "\nY:" + capturedAcceleration.getY() +
                "\nZ:" + capturedAcceleration.getZ() +
                "\nTimestamp:" + capturedAcceleration.getTimestamp());
    }

    private Acceleration getAccelerationFromSensor(SensorEvent event) {
        long timestamp = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
        return new Acceleration(event.values[0], event.values[1], event.values[2], timestamp);
    }

    /**
     * Asyncronous task to post request to a Rest API.
     */
    private void sendDataToCassandra(Acceleration capturedAcceleration) {

        TrainingAcceleration training = new TrainingAcceleration();
        training.setAcceleration(capturedAcceleration);
        training.setUserID(userID);
        training.setActivity(selectedActivity);

        Call<Void> call = cassandraRestApi.sendTrainingAccelerationValues(training);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), getText(R.string.rest_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getBaseContext(), getText(R.string.rest_failure), Toast.LENGTH_LONG).show();
            }
        });
    }
}
