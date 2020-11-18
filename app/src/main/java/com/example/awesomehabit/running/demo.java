package com.example.awesomehabit.running;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import com.example.awesomehabit.database.AppDatabase;
import com.example.awesomehabit.running.utils.Utils;
import com.example.awesomehabit.database.running.Run;
//import com.example.tutorial.database.runninghabit.RunDatabase;
import com.example.awesomehabit.running.service.SimpleService;
import com.example.awesomehabit.R;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.turf.TurfMeasurement;
//import com.yashovardhan99.timeit.Stopwatch;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class demo extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private static final String SOURCE_ID = "SOURCE_ID";
    private MapboxMap _mapboxMap;
    private MapView _mapView;
    private Button _starRunning,  _stopRunning , _data;
    private TextView _dis;
    private PermissionsManager _permissionsManager;
    private Double _distance = 0.;
    private Boolean is_tracking = false;
    private TextView tv;
//    private long savetime = 0;

    // lay vi tri khi khong chay
    private LocationEngine _locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback();

    @Override
    // todo: check rotation https://docs.mapbox.com/android/maps/examples/location-component-camera-options/
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // todo: loi camera animation
        Log.d("demo", "OnCreate");
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.running_activity);

        initView();

        if(savedInstanceState!=null){
            is_tracking = true;
        }

        _starRunning.setOnClickListener(v -> {
            _locationEngine.removeLocationUpdates(callback);
            sendCommandToService("START");
        });
        _stopRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(demo.this)
                        .setMessage("Do you really want to stop ??")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            is_tracking = false;
                            saveRoute();
                        } )
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        _data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });

        _mapView.onCreate(savedInstanceState);
        _mapView.getMapAsync(this);
    }

     private void saveRoute(){
        new AlertDialog.Builder(demo.this)
                .setMessage("Wanna Save Your Route ?")
                .setPositiveButton("Of Course", (dialog, which) -> {
                    sendCommandToService("STOP_SAVE");
                })
                .setNegativeButton("Don't save route", (dialog, which) -> {
                    sendCommandToService("STOP_NOT_SAVE");
                })
                .show();
    }

    private void getdata() {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Run> runs = AppDatabase.getDatabase(getApplicationContext()).runDao().getAllRun();
                List<Point> po = runs.get(4).getRoute(getApplicationContext());
                List<Double> speed = runs.get(4).getSpeeds(getApplicationContext());
            }
        });
    }

    private void initView(){
        _mapView = findViewById(R.id.mapView);
        _starRunning = findViewById(R.id.btn_startrunning);
        _stopRunning = findViewById(R.id.btn_stoprunning);
        _dis = findViewById(R.id.distance);
        _data = findViewById(R.id.data);
        tv = findViewById(R.id.time);
    }

    private void subcribeToObserver(){
        SimpleService.isTracking.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                is_tracking = aBoolean;
            }
        });

        SimpleService.totalDistance.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                _distance = aDouble;
                _dis.setText(String.valueOf(Utils.round(_distance,2)));
            }
        });

        SimpleService.points.observe(this, new Observer<List<Point>>() {
            @Override
            public void onChanged(final List<Point> points) {
                if(!is_tracking){
                    return;
                }

//                Log.d("demo", "Points change");
//                Log.d("demo", "servicesize: " + points.size());

                int size = points.size();
                Location location = new Location("");
                location.setLatitude(points.get(size-1).latitude());
                location.setLongitude(points.get(size-1).longitude());
                _mapboxMap.getLocationComponent().forceLocationUpdate(location);
                _mapboxMap.getStyle(style -> {
                    GeoJsonSource geoJsonSource = style.getSourceAs(SOURCE_ID);
                    geoJsonSource.setGeoJson(Feature.fromGeometry(LineString.fromLngLats(points)));
                });
            }
        });

        SimpleService.seconds.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer aLong) {
                int minute  = (int) (aLong/60);
                int second = (int) (aLong%60);
                tv.setText(String.valueOf(minute) + ":" + String.valueOf(second) );

            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("demo", "onNewIntent");
        super.onNewIntent(intent);
        if(intent.getAction() == "SHOW_ACTIVITY"){
            Log.d("demo", "sho activity");
//            startActivity(intent);
        }else if(intent.getAction() == "SAVE_DATABASE"){
            is_tracking = false;
        }/*else if(intent.getAction() == "SEND_TIME"){
            savetime = intent.getLongExtra("current_chrono", 0);
        }*/
    }

    private void sendCommandToService(String action){
        Intent intent  = new Intent(this, SimpleService.class);
        intent.setAction(action);
        if(action ==  "STOP_SAVE" || action == "STOP_NOT_SAVE"){
            intent.putExtra("distance", _distance );
        }
        startService(intent);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        _mapboxMap = mapboxMap;

        // load mapView xong thi load style cho mapView thong qua mapboxMap
        mapboxMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // them mot source co dinh cho style

                // Cau hinh ve Line
                style.addSource(new GeoJsonSource(SOURCE_ID));
                style.addLayer(new LineLayer("linelayer", SOURCE_ID)
                        .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                                PropertyFactory.lineOpacity(.7f),
                                PropertyFactory.lineWidth(3f),
                                PropertyFactory.lineColor(Color.parseColor("#F44336"))));

                //Tien hanh xu li location
                enableLocationComponent(style);

                if(!is_tracking){
                    initLocationEngine();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enableLocationComponent(Style loadedMapStyle) {

        // check co quyen vao location chua ?
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // check da bat gps hay chua
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gpsturnon = false;
            boolean network_enabled = false;
            try {
                gpsturnon = locationManager.isLocationEnabled();
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER ) ;
            } catch (Exception e) {
                e.printStackTrace() ;
            }

            if(!gpsturnon && !network_enabled){
                new AlertDialog.Builder(this)
                        .setMessage("GPS Enable")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            // kieu nhu cai nay de cho viec hien thi cai cham toa do cua minh
            // tham khao them: https://docs.mapbox.com/android/maps/overview/location-component/#activating
            // https://docs.mapbox.com/archive/android/maps/api/8.1.0/com/mapbox/mapboxsdk/location/LocationComponent.html
            LocationComponent locationComponent = _mapboxMap.getLocationComponent();

            // thiet lap cho location
            LocationComponentOptions option = LocationComponentOptions.builder(this)
                    .compassAnimationEnabled(false)
                    .enableStaleState(false)
                    .staleStateTimeout(10)
                    .trackingAnimationDurationMultiplier(0)
                    .build();


            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(option)
                            .useDefaultLocationEngine(false)
                            .build();

            //
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

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
            locationComponent.setLocationComponentEnabled(true);

            // camera theo di theo vi tri
            locationComponent.setCameraMode(CameraMode.NONE);

            // hien thi cai mui ten chi huong cua dien thoai
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            _permissionsManager = new PermissionsManager( this);
            _permissionsManager.requestLocationPermissions(this);
        }
    }

    private class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {
        // result khi lay location thanh cong o trong ham nay
        @Override
        public void onSuccess(LocationEngineResult result) {
            Location currentLoca = result.getLastLocation();
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(currentLoca.getLatitude(), currentLoca.getLongitude())) // Sets the new camera position
                    .zoom(15) // Sets the zoom
//                    .bearing(180) // Rotate the camera
//                    .tilt(30) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder

            _mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 1000);
        }
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.e("demo", "getting location failure");
        }
    }

    private void initLocationEngine() {
        // lay cong cu dinh vi tot nhat hien tai, co the la gps, wwiffi , ko can biet
        _locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        // thiet lap de update location
        LocationEngineRequest request = new LocationEngineRequest
                .Builder(1000)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(1000)
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
        subcribeToObserver();
        _locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        _locationEngine.getLastLocation(callback);
    }
    
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "cc",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            _mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "thieu permission", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        Log.d("demo","OnStart");
        super.onStart();
        _mapView.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("demo", "OnResume");
//        if(invisible){
//             sendCommandToService("ACTIVITY_RESUME");
//            invisible = false;
//        }
        super.onResume();
        _mapView.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("demo", "onPaused");
//        if(is_tracking){
//            sendCommandToService("ACTIVITY_ON_PAUSE");
//            invisible = true;
//        }
        super.onPause();
        _mapView.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("demo", "onStop");
        super.onStop();
        _mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        _mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Log.d("demo", "onDestroy");
        super.onDestroy();
        _mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        _mapView.onLowMemory();
    }
}
