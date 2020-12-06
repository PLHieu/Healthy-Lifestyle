package com.example.awesomehabit.running;

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
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;

import com.example.awesomehabit.R;
import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.database.running.Run;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfMeasurement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RunningService extends LifecycleService {

    public static MutableLiveData<Integer> seconds =  new MutableLiveData<>();
    public static MutableLiveData<Double> totalDistance = new MutableLiveData<>();
    public static MutableLiveData<List<Point>> points = new MutableLiveData<>();
    private static List<Point> data = new ArrayList<>();
    public static MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    public static Boolean  isTracking;
    public static int listSize;
    private Timer _timer;
    private LocationEngine _locationEngine;
    private RunningService.LocationChangeListeningActivityLocationCallback callback =
            new RunningService.LocationChangeListeningActivityLocationCallback();

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("sv", "OnCreate");
        initValues();
        initLocationEngine();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("sv", "onstartcommand");
        if(intent!=null){
            if(intent.getAction() == RunCons.SERVICE_START_RUNNINGTRACKING){
//                        Log.d("sv", "start runnign trackinh");
                startRunningTracking();

            }else if(intent.getAction() == RunCons.SERVICE_STOPSAVE_RUNNINGTRACKING){
                isTracking = false;
                saveData(totalDistance.getValue(),seconds.getValue(), true);
                totalDistance.setValue(0.);
                seconds.setValue(0);
                _timer.cancel();

            }else if(intent.getAction() == RunCons.SERVICE_STOPNOTSAVE_RUNNINGTRACKING){
                totalDistance.setValue(0.);
                isTracking = false;
                seconds.setValue(0);
                _timer.cancel();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void saveData(double distance, long elapsedMillis, boolean saveRoute) {
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
                    run = new Run(RunningUtils.doubleKmTointMetter(distance),String.valueOf(currenttime), calendar,elapsedMillis, String.valueOf(date.getTime()));
                }else{
                    run = new Run(RunningUtils.doubleKmTointMetter(distance),String.valueOf(currenttime),calendar,elapsedMillis, "none");
                }

                // chen row vao data base
                AppDatabase.getDatabase(getApplicationContext()).runDao().insertRun(run);
                // them file geojson
                if(saveRoute){
                    String route = RunningUtils.getStringfromListPoints(points.getValue());
                    RunningUtils.writeToFile(route, getApplicationContext(),run.routeID);
                }
            }
        });
    }

    private void startRunningTracking(){
//        Log.d("sv", "startrunnning");
        initValues();
        isTracking = true;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager);
        }

        Intent intent = new Intent(this, RunningTracking.class);
        intent.setAction(RunCons.HOME_SHOW);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT );

        // debug for api level 22
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, RunCons.NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.run)
                .setContentIntent(pendingIntent)
                .setContentTitle("Running Tracking")
                .setContentText("00:00:00")
                .setContentInfo("Info");
        Notification notification = notificationBuilder.build();
        notificationManager.notify(RunCons.NOTIFICATION_ID, notification);

        startForeground(RunCons.NOTIFICATION_ID, notification);
        startStopWatch();
    }

    private void initValues(){
        isTracking = false ;
        data.clear();
        points.setValue(new ArrayList<>());
        totalDistance.setValue(0.);
        seconds.setValue(0);
        listSize = 0;
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

            Intent intent = new Intent(this, RunningTracking.class);
            intent.setAction(RunCons.HOME_SHOW);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT );

            // debug for api level 22
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, RunCons.NOTIFICATION_CHANNEL_ID);

            notificationBuilder
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.run)
                    .setContentIntent(pendingIntent)
                    .setContentTitle("Running Tracking")
                    .setContentText("Time: " + String.valueOf((int)(value/60)) + ":" + String.valueOf(value%60) + "        " + String.valueOf(RunningUtils.round(value1,2)) + " km")
                    .setContentInfo("Info");


            Notification notification = notificationBuilder.build();
            notificationManager.notify(RunCons.NOTIFICATION_ID, notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager){
        NotificationChannel notificationChannel =
                new NotificationChannel(RunCons.NOTIFICATION_CHANNEL_ID, RunCons.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);

        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void initLocationEngine() {
        // lay cong cu dinh vi tot nhat hien tai, co the la gps, wwiffi , ko can biet
        _locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        // thiet lap de update location
        LocationEngineRequest request = new LocationEngineRequest
                .Builder(RunCons.DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(RunCons.DEFAULT_MAX_WAIT_TIME)
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

            // neu nhu chua tracking thì mới cho bên main update vị trí real time
            // còn khi tracking rồi thì sẽ update có kiểm soát
            if(location != null && isTracking!= null && !isTracking /*!isTracking.getValue()*/){
                currentLocation.setValue(location);
            }

            if(isTracking!= null && /*isTracking.getValue()*/ isTracking){
                // nếu như khoảng cách giữa điểm mớilấy với điểm cuối trong poinsst đủ lớn thì mới lấy điểm đó
                if(listSize>=1){ // tránh null pointer exception
//                    Log.d("sv", "get point");
                    Point newpoint = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                    Double temp_distance  = TurfMeasurement.distance(data.get(listSize-1), newpoint);
//                    Log.d("sv", temp_distance.toString());
                    // kc nho nhat de lay vi tri la 4m
                    if(temp_distance >= 0.04){
                        data.add(newpoint);
                        listSize++;
                        points.setValue(data);
                        currentLocation.setValue(location);
                        totalDistance.setValue(totalDistance.getValue() +  temp_distance);
                    }
                }else{
                    // debug trường hợp mói ban đầu chạy (lisize = 0)
                    Point newpoint = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                    data.add(newpoint);
                    points.setValue(data);
                    listSize++;
                }
            }

        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.e("service", "location Egine Call backk failure");
        }
    }



}
