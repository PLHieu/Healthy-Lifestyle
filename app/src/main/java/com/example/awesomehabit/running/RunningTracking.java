package com.example.awesomehabit.running;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import com.example.awesomehabit.R;
import com.example.awesomehabit.running.utils.Utils;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
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

import java.util.List;

public class RunningTracking extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, View.OnClickListener {

    private MapboxMap _mapboxMap;
    private MapView _mapView;
    private Button _btn_Running;
    private TextView _tv_distance;
    private PermissionsManager _permissionsManager;
    private Double _distance = 0.;
    private TextView tv;
    private Animation rotate;
    private ImageView imv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("main", "create");
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.running_activity);

        initView();

        _mapView.onCreate(savedInstanceState);
        _mapView.getMapAsync(this);
    }



    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        _mapboxMap = mapboxMap;

        // load mapView xong thi load style cho mapView thong qua mapboxMap
        mapboxMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                // Cau hinh ve Line
                style.addSource(new GeoJsonSource(RunCons.SOURCE_ID));
                style.addLayer(new LineLayer("linelayer", RunCons.SOURCE_ID)
                        .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                                PropertyFactory.lineOpacity(.7f),
                                PropertyFactory.lineWidth(3f),
                                PropertyFactory.lineColor(Color.parseColor("#F44336"))));

                enableLocationComponent(style);

                //check cac permission, neu thoa thi start service
                checkInternetConnectAndLocation();

            }
        });
    }

    private void startService() {
//        Log.d("main", "startserice" );
        _btn_Running.setEnabled(true);

        // khoi dong service va bat dau observer
        sendCommandToService("");

        //todo: check điều kiện null khi mà service chưa khởi động hoàn toàn
        initObsever();

    }

    private void initObsever() {
        // observe tong khoang cach
        RunningService.totalDistance.observe(this, aDouble -> {
            _distance = aDouble;
            _tv_distance.setText(String.valueOf(Utils.round(_distance,2)));
        });

        //observe list point
        RunningService.points.observe(this, points -> {
            if(RunningService.isTracking){
                int size = points.size();
                if(size >=1 ){
                    Location location = new Location("");
                    location.setLatitude(points.get(size-1).latitude());
                    location.setLongitude(points.get(size-1).longitude());
//                    _mapboxMap.getLocationComponent().forceLocationUpdate(location);
                    _mapboxMap.getStyle(style -> {
                        GeoJsonSource geoJsonSource = style.getSourceAs(RunCons.SOURCE_ID);
                        geoJsonSource.setGeoJson(Feature.fromGeometry(LineString.fromLngLats(points)));
                    });
                }
            }
        });

        // observe thoi gian
        RunningService.seconds.observe(this, (Observer<Integer>) aLong -> {
            int minute  = (int) (aLong/60);
            int second = (int) (aLong%60);
            tv.setText(minute + ":" + second);
        });


        // observer location hien tai
        RunningService.currentLocation.observe(this , location -> {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude())) // Sets the new camera position
//                    .zoom(15) // Sets the zoom
//                    .bearing(180) // Rotate the camera
//                    .tilt(30) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder
            _mapboxMap.getLocationComponent().forceLocationUpdate(location);
            _mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 1000);
//            Log.d("main", "lcation changr");
        });
    }

    private void sendCommandToService(String action){
        Intent intent  = new Intent(this, RunningService.class);
        if(!action.equals("")) {
            intent.setAction(action);
            if (action.equals("STOP_SAVE") || action.equals("STOP_NOT_SAVE")) {
                intent.putExtra("distance", _distance);
            }
        }
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_startrunning) {
//            Log.d("main", "btnclick");
            if(!RunningService.isTracking /*!RunningService.isTracking.getValue()*/){
                _btn_Running.setText("Finish");
                sendCommandToService(RunCons.SERVICE_START_RUNNINGTRACKING);
            }else{
                new AlertDialog.Builder(RunningTracking.this)
                        .setMessage("Do you really want to stop ??")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            rotate.cancel();
                            imv.setAnimation(rotate);
                            _btn_Running.setText("Start");
                            new AlertDialog.Builder(RunningTracking.this)
                                    .setMessage("Wanna Save Your Route ?")
                                    .setPositiveButton("Of Course", (dialog1, which1) -> {
                                        sendCommandToService(RunCons.SERVICE_STOPSAVE_RUNNINGTRACKING);
                                    })
                                    .setNegativeButton("Don't save route", (dialog1, which1) -> {
                                        sendCommandToService(RunCons.SERVICE_STOPNOTSAVE_RUNNINGTRACKING);
                                    })
                                    .show();

                        } )
                        .setNegativeButton("Cancel", null)
                        .show();
            }

        }
    }






    private void initView() {
        _mapView = findViewById(R.id.mapView);
        _btn_Running = findViewById(R.id.btn_startrunning);
        _tv_distance = findViewById(R.id.distance);
        tv = findViewById(R.id.time);
        _btn_Running.setEnabled(false);
        imv = findViewById(R.id.imgView);
        rotate = AnimationUtils.loadAnimation(this,R.anim.rotation);

        _btn_Running.setOnClickListener(this);
        if(RunningService.isTracking == null || !RunningService.isTracking){
            _btn_Running.setText("start");
        }else{
            _btn_Running.setText("finish");
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enableLocationComponent(Style loadedMapStyle) {

        // kieu nhu cai nay de cho viec hien thi cai cham toa do cua minh
        // tham khao them: https://docs.mapbox.com/android/maps/overview/location-component/#activating
        // https://docs.mapbox.com/archive/android/maps/api/8.1.0/com/mapbox/mapboxsdk/location/LocationComponent.html
        Log.d("demo", "enablelocationcomponent");
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
    }

    private void checkInternetConnectAndLocation() {
        // kiem tra internet
        int connectionType = RunningUtils.getConnectionType(this);
        if(connectionType == 0){
            new AlertDialog.Builder(this)
                    .setMessage("You need to connect to Internet for loading map")
                    .setPositiveButton("I have already connected", (dialog, which) -> {
                        dialog.cancel();
                        checkLocationAccess();

                    })
                    .setNegativeButton("Cancel", ((dialog, which) -> {
                        dialog.cancel();
                        checkLocationAccess();

                    }))
                    .show();
        }else{
            checkLocationAccess();
        }
    }

    private void checkLocationAccess() {
        // kiem tra truy cap vao vi tri
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            // hien thi popup xin cap quyen truy cap vao vitri, co override xu li call back tai ham on Permissionresult
            _permissionsManager = new PermissionsManager(this);
            _permissionsManager.requestLocationPermissions(this);
        }

        checkLocationTurnOn();
    }

    private void checkLocationTurnOn(){
        // check da bat gps hay chua
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsturnon = false;
        try {
            gpsturnon = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            Log.d("RunningTracking", e.toString());
        }
        if(!gpsturnon){
            new AlertDialog.Builder(this)
                    .setMessage("Need to enable GPS for using !!")
                    .setPositiveButton("Settings", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("Cancel", (dialog, which) -> {_btn_Running.setEnabled(false);})
                    .show();
            //todo: xử lí sau khi nguòi dùng bật gps xong rồi quay   start service
        }else{
            startService();
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            startService();
        } else {
            Toast.makeText(this, "Cannot using running tracker without accessing location", Toast.LENGTH_SHORT).show();
        }
    }

}
