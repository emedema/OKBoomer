package com.example.okboomerprototype;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import androidx.appcompat.app.AppCompatActivity;

/**
 * This shows how to create a simple activity with a raw MapView and add a marker to it. This
 * requires forwarding all the important lifecycle methods onto MapView.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
//    Location currentLocation;
    Float rad;
    /**
     * Created by User on 10/2/2017.
     */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onMapReady: map is ready");
            mMap = googleMap;

            if (mLocationPermissionsGranted) {
                getDeviceLocation();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

            }
        }

        private static final String TAG = "MapActivity";

        private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
        private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
        private static final float DEFAULT_ZOOM = 15f;

        //vars
        private Boolean mLocationPermissionsGranted = false;
        private GoogleMap mMap;
        private FusedLocationProviderClient mFusedLocationProviderClient;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            Intent intent = getIntent();
            rad = intent.getFloatExtra("radius", 0);

            getLocationPermission();
        }

        private void getDeviceLocation(){
            Log.d(TAG, "getDeviceLocation: getting the devices current location");

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            try{
                if(mLocationPermissionsGranted){
                    Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: found location!");
                                Location currentLocation = (Location) task.getResult();
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        7f);

                            }else{
                                Log.d(TAG, "onComplete: current location is null");
                                Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (SecurityException e){
                Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
            }
        }

        private void moveCamera(LatLng latLng, float zoom){
            Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            mMap.addCircle(
                    new CircleOptions()
                            .center(latLng)
                            .radius(rad*1000)
                            .strokeWidth(3f)
                            .strokeColor(Color.RED)
                            .fillColor(Color.argb(70, 150, 50, 50))
            );
        }

        private void initMap(){
            Log.d(TAG, "initMap: initializing map");
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(MapsActivity.this);
        }

        private void getLocationPermission(){
            Log.d(TAG, "getLocationPermission: getting location permissions");
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionsGranted = true;
                    initMap();
                }else{
                    ActivityCompat.requestPermissions(this,
                            permissions,
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            Log.d(TAG, "onRequestPermissionsResult: called.");
            mLocationPermissionsGranted = false;

            switch(requestCode){
                case LOCATION_PERMISSION_REQUEST_CODE:{
                    if(grantResults.length > 0){
                        for(int i = 0; i < grantResults.length; i++){
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                                mLocationPermissionsGranted = false;
                                Log.d(TAG, "onRequestPermissionsResult: permission failed");
                                return;
                            }
                        }
                        Log.d(TAG, "onRequestPermissionsResult: permission granted");
                        mLocationPermissionsGranted = true;
                        //initialize our map
                        initMap();
                    }
                }
            }
        }


    }

