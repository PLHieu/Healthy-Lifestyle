package com.example.awesomehabit.running.service;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.running.Run;
import com.example.awesomehabit.running.demo;
import com.example.awesomehabit.R;
import com.example.awesomehabit.running.utils.Utils;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfMeasurement;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SimpleService extends LifecycleService {

    private final String NOTIFICATION_CHANNEL_ID = "tracking_channel";
    private final String NOTIFICATION_CHANNEL_NAME = "Tracking";
    private final int NOTIFICATION_ID = 1;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 3000L;
    private static final long DEFAULT_MAX_WAIT_TIME = 100;

    private boolean first_run = true;
    public static MutableLiveData<Boolean> isTracking  = new MutableLiveData<Boolean>() ;
    private LocationEngine _locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback();

    public static MutableLiveData<Double> totalDistance = new MutableLiveData<Double>();
    public static MutableLiveData<List<Point>> points = new MutableLiveData<List<Point>>();
    private static List<Point> data = new ArrayList<>();
    public static int listSize = 0;
    public static Boolean activityonpause = false;
    // thong bao cho ben demo biet de updadte data
    public static MutableLiveData<Integer>  seconds =  new MutableLiveData<Integer>();

    private Timer _timer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service", "OnCreate");
        postInitValues();
        initLocationEngine();
    }

    private void postInitValues(){
        isTracking.postValue(false);
        points.postValue(new ArrayList<>());
        totalDistance.postValue(0.);
        seconds.postValue(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "startcommand");

        if(intent!=null){

            // trigged khi nhan tu button start running
            if(intent.getAction() == "START"){

                if(first_run){
                    Log.d("service", "Start");
                    startForeGroundService();
//                    startStopWatch();
                    Log.d("service", "startwatch");
                    Log.d("service",String.valueOf(seconds.getValue()));
                    first_run = false;
                }

            }else if(intent.getAction() == "STOP_SAVE"){

                stopAndSaveData(intent.getDoubleExtra("distance",0.),seconds.getValue(), true);
                killservice();

            }else if(intent.getAction() == "STOP_NOT_SAVE"){

                stopAndSaveData(intent.getDoubleExtra("distance",0.),intent.getLongExtra("time", 0), false);
                killservice();

            }else if (intent.getAction() == "ACTIVITY_ON_PAUSE"){ // neu nhu activity khong con visible nua

                activityonpause = true;

            } else if(intent.getAction() == "ACTIVITY_RESUME"){

                activityonpause = false;

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void startStopWatch() {
        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if(seconds.getValue()!= null && totalDistance.getValue()!=null){
                    seconds.postValue(seconds.getValue() + 1);
                    updatenotification(seconds.getValue(), totalDistance.getValue());
                }
            }
        }, 0,1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updatenotification(Integer value, Double value1) {
        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Intent intent = new Intent(this, demo.class);
            intent.setAction("SHOW_ACTIVITY");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT );

            // debug for api level 22
            NotificationCompat.Builder notificationBuilder = null;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            }else{
                notificationBuilder = new NotificationCompat.Builder(this, "");
            }

            notificationBuilder
                    .setAutoCancel(false)
                    .setOngoing(true)
                   /* .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())*/
//                    .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
//                    .setTicker("Hearty365")
//                    .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                    .setContentTitle("Running Tracking")
                    .setContentText("Time: " + String.valueOf((int)(value/60)) + ":" + String.valueOf(value%60) + "        " + String.valueOf(Utils.round(value1,2)) + " km")
                    .setContentIntent(pendingIntent)
                    .setContentInfo("Info");

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationBuilder.setSmallIcon(R.drawable.ic_baseline_directions_run_24);
                notificationBuilder.setContentIntent(pendingIntent);
                Notification notification = notificationBuilder.build();
                notificationManager.notify(NOTIFICATION_ID, notification);
            }else{

                notificationBuilder.setPriority(Notification.PRIORITY_MAX);
                notificationBuilder.setContentIntent(pendingIntent);
                Notification notification = notificationBuilder.build();
                notificationManager.notify(0, notification);

            }


            /*Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                    .setContentTitle("Running Tracking")
                    .setContentText("Time: " + String.valueOf((int)(value/60)) + ":" + String.valueOf(value%60) + "        " + String.valueOf(Utils.round(value1,2)) + " km")
                    .setContentIntent(pendingIntent)
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notification);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void killservice() {
        Intent intent = new Intent();
        intent.setAction("STOP_TRACKING");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT );
        _locationEngine.removeLocationUpdates(pendingIntent);

        // dung dong ho
        _timer.cancel();
        // reset lai cac gia tri
        first_run = true;
        activityonpause = false;
        listSize = 0;
        data.clear();
        postInitValues();

        stopForeground(true);
        stopSelf();
    }

    private void stopAndSaveData(double distance, long elapsedMillis, boolean saveRoute) {

        // truy cap data base
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                Long currenttime = System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currenttime);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Run run;
                if(saveRoute){
                    run = new Run(Utils.doubleKmTointMetter(distance),String.valueOf(currenttime), calendar,elapsedMillis, String.valueOf(date.getTime()));
                }else{
                    run = new Run(Utils.doubleKmTointMetter(distance),String.valueOf(currenttime),calendar,elapsedMillis, "none");
                }

                // chen row vao data base
                AppDatabase.getDatabase(getApplicationContext()).runDao().insertRun(run);
                // them file geojson
                if(saveRoute){
                    String route = getStringfromListPoints(points.getValue());
                    writeToFile(route, getApplicationContext(),run.routeID);
                }
            }
        });
    }

    private void updateLocationTracking(Boolean istracking){
        if(istracking){
//            Log.d("service", "updateLocationTracking");
            // trong nay co loop de update lai location
            initLocationEngine();
        }else{
             // remove location update
            _locationEngine.removeLocationUpdates(callback);
        }
    }


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

        // debug for api level 22
        NotificationCompat.Builder notificationBuilder = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        }else{
            notificationBuilder = new NotificationCompat.Builder(this, "");
        }

        notificationBuilder
                .setAutoCancel(false)
                .setOngoing(true)
                /*.setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())*/

               /* .setTicker("Hearty365")*/
//                .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                .setContentTitle("Running Tracking")
                .setContentText("00:00:00")
                .setContentInfo("Info");

        Notification notification = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                    .setContentIntent(pendingIntent);
            notification = notificationBuilder.build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }else{

            notificationBuilder
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pendingIntent);

            notification = notificationBuilder.build();
            notificationManager.notify(0, notification);

        }




        /*Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                .setContentTitle("Running Tracking")
                .setContentText("00:00:00")
                .setContentIntent(pendingIntent)
                .build();*/

        startForeground(NOTIFICATION_ID, notification);
        startStopWatch();
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


//            Log.d("service", "gia tri cua istracking" + Objects.requireNonNull(isTracking.getValue()).toString());
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.e("service", "failure");
        }
    }

    private void writeToFile(String data, Context context, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("demo", "File write failed: " + e.toString());
        }
    }

    private String getStringfromListPoints(List<Point> pointList) {
        String result = "";
        for (Point point:pointList){
            result += String.valueOf(point.latitude()) + "@" + String.valueOf(point.longitude()) + " " ;
        }
        return result;
    }

    public String getDateString(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");

        return sdf.format(new Date(timestamp));
    }

}
