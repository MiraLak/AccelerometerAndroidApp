package com.miralak.basicaccelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.miralak.basicaccelerometer.api.CassandraRestApi;
import com.miralak.basicaccelerometer.model.Acceleration;


import retrofit.RestAdapter;


public class AccelerometerActivity extends ActionBarActivity implements SensorEventListener{

    public static final String URL = "http://192.168.1.17:8080/accelerometer-api/"; //TODO set the REST API URL
    Sensor accelerometer;
    SensorManager sm;
    TextView acceleration;

    float x_value;
    float y_value;
    float z_value;
    long timestamp;

    CassandraRestApi cassandraRestApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        acceleration = (TextView) findViewById(R.id.acceleration);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(URL)
                .build();

        cassandraRestApi = restAdapter.create(CassandraRestApi.class);
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
        x_value = event.values[0];
        y_value = event.values[1];
        z_value = event.values[2];
        timestamp = event.timestamp;
        acceleration.setText("X:"+ x_value +
        "\nY:"+ y_value +
        "\nZ:"+ z_value +
        "\nTimestamp:"+ timestamp);

        new myTask().execute();

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do nothing
    }


    /**
     * Asyncronous task to post request to a Rest API.
     */
    private class myTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
             //Post values ton a REST Api
             postToRestApi(x_value,y_value,z_value,timestamp);
            return null;
        }
    }

    /**
     * POST acceleration value to a REST API which will store them.
     * The REST API request sample used is:
     *
     * Header Content-Type must be set: application/json
     * Body:
     * {acceleration:
     *  {
     *  "date": 1428773040488,
     *  "x": 0.98,
     *  "y": 6.43,
     *  "z": 9.01,
     *  }
     * }
     *
     *returned status: 201 CREATED
     *
     * @param x_value
     * @param y_value
     * @param z_value
     * @param timestamp
     */
    private void postToRestApi(float x_value, float y_value, float z_value, long timestamp) {
        cassandraRestApi.sendAccelerationValues(new Acceleration(x_value, y_value, z_value, timestamp));
    }

}
