package com.example.dronique.ui.main;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dronique.Client;
import com.example.dronique.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class Tab2Fragment extends Fragment implements SensorEventListener {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Marker mMarker;

    private SensorManager mSensorManager;
    private Sensor mAccel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_two, container, false);

        // Gestion de la MapView
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                LatLng posLaRochelle = new LatLng(46.1558,-1.1532);
                mMarker = mGoogleMap.addMarker(new MarkerOptions().position(posLaRochelle));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(posLaRochelle).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mSensorManager.registerListener(this, mAccel, 10);
    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.w("Accelerometer : ", "X -> " + event.values[0]);
        Log.w("Accelerometer : ", "Y -> " + event.values[1]);
        Log.w("Accelerometer : ", "Z -> " + event.values[2]);

        update(event.values[0], event.values[1], event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void update(double x, double y, double z) {

    }
}
