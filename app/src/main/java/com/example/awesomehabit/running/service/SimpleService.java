package com.example.awesomehabit.running.service;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import com.example.awesomehabit.running.demo;
import com.example.awesomehabit.R;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfMeasurement;

import java.util.ArrayList;
import java.util.List;

public class SimpleService extends LifecycleService {

    private final String NOTIFICATION_CHANNEL_ID = "tracking_channel";
    private final String NOTIFICATION_CHANNEL_NAME = "Tracking";
    private final int NOTIFICATION_ID = 1;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 3000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS;

    private boolean first_run = true;
    public static MutableLiveData<Boolean> isTracking  = new MutableLiveData<Boolean>() ;
    public static MutableLiveData<List<Point>> points = new MutableLiveData<List<Point>>();
    private static List<Point> data = new ArrayList<>();
    private LocationEngine _locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback();
    public static MutableLiveData<Double> totalDistance = new MutableLiveData<Double>();
    private static double newdistance = 0.;
    public static int listSize = 0;
    public static Boolean activityonpause = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service", "OnCreate");
        postInitValues();
        isTracking.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                updateLocationTracking(aBoolean);
            }
        });
    }

    private void postInitValues(){
        isTracking.postValue(Boolean.FALSE);
        points.postValue(new ArrayList<Point>());
        totalDistance.postValue(0.);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            if(intent.getAction() == "START"){
                if(first_run){
                    Log.d("service", "Start");
                    startForeGroundService();
                    first_run = false;
                }else {
                Log.d("service", "Resumming service");
                }

            }else if(intent.getAction() == "PAUSE"){

            }else if(intent.getAction() == "STOP"){
                killservice();
            } else if (intent.getAction() == "ACTIVITY_ON_PAUSE"){ // neu nhu activity khong con visible nua
                activityonpause = true;
                Log.d("service", "activity on pause");
            } else if(intent.getAction() == "ACTIVITY_RESUME"){
                activityonpause = false;
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void killservice() {

        // tao intent gui ve cho demo
        Intent intent = new Intent(this, demo.class);
        intent.setAction("SAVE_DATABASE");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT );
        _locationEngine.removeLocationUpdates(pendingIntent);

        // reset lai cac gia tri
        first_run = true;
        activityonpause = false;
        listSize = 0;
        postInitValues();

        stopForeground(true);
        stopSelf();
    }

    private void updateLocationTracking(Boolean istracking){
        if(istracking){
            //
            Log.d("service", "updateLocationTracking");
            // trong nay co loop de update lai location
            initLocationEngine();
        }else{
             // remove location update
            _locationEngine.removeLocationUpdates(callback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForeGroundService(){
        postInitValues();

        // luc bat dau foreground cung chinh la bat dau thu thap du lieu
        isTracking.postValue(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager);
        }

        Intent intent = new Intent(this, demo.class);
        intent.setAction("SHOW_ACTIVITY");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT );


        Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                .setContentTitle("Running Tracking")
                .setContentText("00:00:00")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager){
        NotificationChannel notificationChannel =
                new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void initLocationEngine() {
        // lay cong cu dinh vi tot nhat hien tai, co the la gps, wwiffi , ko can biet
        _locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        // thiet lap de update location
        LocationEngineRequest request = new LocationEngineRequest
                .Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build();

        // studio tu sinh ra
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        _locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        _locationEngine.getLastLocation(callback);
    }

    private static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {
        // result khi lay location thanh cong o trong ham nay
        @Override
        public void onSuccess(LocationEngineResult result) {
            Location location = result.getLastLocation();
            if (location == null) {
                return;
            }
            data.add(Point.fromLngLat(location.getLongitude(), location.getLatitude()));
            listSize ++;
            points.postValue(data);
            // tinh toan khoang cach
            if(listSize>=2){
                totalDistance.postValue(totalDistance.getValue() + TurfMeasurement.distance(data.get(listSize-2), data.get(listSize-1)));
            }
//            Log.d("service", String.valueOf(location.getLatitude()) + ":" + String.valueOf(location.getLongitude()));
            Log.d("service", String.valueOf(activityonpause));

        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.e("service", "failure");
        }
    }
}
