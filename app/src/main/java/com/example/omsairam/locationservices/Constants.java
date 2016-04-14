package com.example.omsairam.locationservices;

import com.google.android.gms.location.DetectedActivity;

/**
 * Created by omsairam on 4/14/2016.
 */
public class Constants {
    private Constants(){

    }
    public static final String PACKAGE_NAME="com.example.omsairam.locationservices";
    public static final String BROADCAST_ACTION= PACKAGE_NAME+".BROADCAST_ACTION";
    public static final String ACTIVITY_EXTRA= PACKAGE_NAME+".ACTIVITY_EXTRA";
    public static final String SHARED_PREFERENCES_NAME=PACKAGE_NAME+".SHARED_PREFRENCES";
    public static final String ACTIVITY_UPDATES_REQUESTED_KEY= PACKAGE_NAME+"..ACTIVITY_UPDATES_REQUESTED";
    public static final String DETECTED_ACTIVITIES= PACKAGE_NAME+".DETECTED_ACTIVITIES";
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS=0;
    protected static final int [] MONITORED_ACTIVITIES={
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN

    };
}
