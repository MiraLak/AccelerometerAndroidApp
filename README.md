# AccelerometerAndroidApp
A basic accelerometer app which sends data to a REST API based on Cassandra :
https://github.com/pvardanega/accelerometer-rest-to-cassandra

Each acceleration contains:

    date when it has been captured as a timestamp (eg, 1428773040488)
    acceleration force along the x axis (unit is m/s²)
    acceleration force along the y axis (unit is m/s²)
    acceleration force along the z axis (unit is m/s²)

# Prerequisites
Download [Android Studio](https://developer.android.com/sdk/index.html)

Then, you have to create a new project importing sources from BasicAccelerometer folder.

**The project is using `Java 8`**

# Start the application
You can install the app into you Android phone (4.0.3 version and above).

Before installing the app, you'll have to start the REST API.

You will need the REST API URL to post data.
You have to add the URL using the configuration screen of the app. The URL will be stored for next usages so no need to set it up each time the app is started.
