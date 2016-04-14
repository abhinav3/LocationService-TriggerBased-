package com.example.omsairam.locationservices;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by omsairam on 4/14/2016.
 */
public class DetectedActivitiesIntentService extends IntentService{
    protected static final String TAG="detection_is";
    public DetectedActivitiesIntentService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //LocationServices
        ActivityRecognitionResult result= ActivityRecognitionResult.extractResult(intent);
        Intent localintent =new Intent(Constants.BROADCAST_ACTION);

        ArrayList<DetectedActivity> detectedActivities= (ArrayList) result.getProbableActivities();

        Log.i(TAG, "activities detected");

        localintent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
        //localintent.putExtra(Constants.ACTIVITY_EXTRA,double [] value); //useful for putting lat and lang values.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localintent);

    }
}
