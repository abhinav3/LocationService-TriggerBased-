package com.example.omsairam.locationservices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , LocationListener ,SensorEventListener{


    protected static final String TAG = "LocationServices";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    private LocationRequest mLocationRequest;
    private SensorManager sensorManager;
    private float accelerationThreshold = 2;
    private long lastUpdate;
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private final long startTime = 5 * 1000;
    private final long interval = 1 * 1000;
    private TextView currentX, currentY, currentZ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));



        currentX=(TextView) findViewById(R.id.XAcc);
        currentY=(TextView) findViewById(R.id.YAcc);
        currentZ=(TextView) findViewById(R.id.ZAcc);
        buildGoogleApiClient();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();*/

        countDownTimer = new MyCountDownTimer(startTime, interval);

        //waiting for 5 sec to get the locations updates then disconnects the client.
        countDownTimer.start();
        /*if (!timerHasStarted) {
            countDownTimer.start();
            timerHasStarted = true;
        } else {
            countDownTimer.cancel();
            timerHasStarted = false;
        }*/


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelerationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelerationSquareRoot >= accelerationThreshold) //value is =4
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            currentX.setText(Float.toString(x));
            currentY.setText(Float.toString(y));
            currentZ.setText(Float.toString(z));
            lastUpdate = actualTime;
            fireGoogleClinet();
            Toast.makeText(this, "Device was shuffled, firing Google APi Client", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public class MyCountDownTimer extends CountDownTimer { //CountDown Timer Class.
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            if(mGoogleApiClient.isConnected()){
                mGoogleApiClient.disconnect();
                Toast.makeText(getApplicationContext(),"Google client Disconnected, 5 Sec ended", Toast.LENGTH_SHORT).show();
                timerHasStarted = false;
                countDownTimer.cancel();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }

    }

    public void fireGoogleClinet(){
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            Toast.makeText(getApplicationContext(),"Google client is now connected", Toast.LENGTH_SHORT).show();
            countDownTimer.start();
            /*if (!timerHasStarted) { //waiting for 5 sec to get the locations updates then disconnects the client.
                countDownTimer.start();
                timerHasStarted = true;
            } else {
                countDownTimer.cancel();
                timerHasStarted = false;
            }*/
        }
        else{
            Toast.makeText(getApplicationContext(),"Google client is already Connected", Toast.LENGTH_SHORT).show();
            countDownTimer.start();
            /*if (!timerHasStarted) { //waiting for 5 sec to get the locations updates then disconnects the client.
                countDownTimer.start();
                timerHasStarted = true;
            } else {
                countDownTimer.cancel();
                timerHasStarted = false;
            }*/
        }

    }



    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);//Update location every 10 second.
        mLocationRequest.setFastestInterval(5000);

        /*Since SDK 23, you should/need to check the permission before you call Location API functionality. Here is an example of how to do it:*/



        //Set the last location.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText("In On connected\n"+String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText("In On connected\n" + String.valueOf(mLastLocation.getLongitude()));
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Implement the access granting popup.

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }


    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override

    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Toast.makeText(getApplicationContext(), "GoogleApiClient has been suspended", Toast.LENGTH_SHORT).show();//not able to connect this time only.
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {


        //We only start the service to fetch the address if GoogleApiClient is connected.
        if (mGoogleApiClient.isConnected() && location != null) {
            mLatitudeText.setText(String.valueOf(location.getLatitude()));
            mLongitudeText.setText(String.valueOf(location.getLongitude()));
            Log.i(TAG, location.toString());
            Toast.makeText(getApplicationContext(), "HI " + location.toString(), Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(), "HI first connect the client", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "GoogleApiClient connection has failed", Toast.LENGTH_SHORT).show();//not able to connect this time only.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


}