package com.example.dronique.ui.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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

public class Tab2Fragment extends Fragment implements SensorEventListener {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Marker mMarker;
    private Drone mDrone;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagneticField;

    private BitmapDrawable mBitmapDrawable =  null;
    private Bitmap mBitmap = null;
    private int mDefaultRotation = -43;

    Thread mThread;

    float[] magneticVector = new float[3];
    float[] accelerometerVector = new float[3];
    float[] resultMatrix = new float[9];
    float[] values = new float[3];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_two, container, false);

        mDrone = new Drone();

        //Gestion de la bitmap
        mBitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.my_marker);
        mBitmap = Bitmap.createScaledBitmap(mBitmapDrawable.getBitmap(), 75, 75, false);

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mMarker.setPosition();
            }
        });

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
                mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(mBitmap));
                mMarker.setRotation(mDefaultRotation);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(posLaRochelle).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mSensorManager.registerListener(this, mAccelerometer, 1000000000);
        mSensorManager.registerListener(this, mMagneticField, 1000000000);
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

        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticVector = event.values;
        }

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            accelerometerVector = event.values;
        }

        SensorManager.getRotationMatrix(resultMatrix, null, accelerometerVector, magneticVector);
        SensorManager.getOrientation(resultMatrix, values);
        int asimuth = (int)Math.toDegrees(values[0]);
        Log.w("ORIENTATION", "asimuth : " +  asimuth);

        if(mMarker != null) {
            mMarker.setRotation((float) Math.toDegrees(values[0]) + mDefaultRotation);

            double kmPerHour = 0;
            if(accelerometerVector[1] <= 0)
                kmPerHour = 111.11;
            else
                kmPerHour = 111.11 - ((accelerometerVector[1] * 10 * 111.11) / 90);

            // longitude
            double kmPerDegreeLng = 111.11 * (double)Math.cos((int) mMarker.getPosition().longitude);
            double longitudeAdded = kmPerHour / kmPerDegreeLng;

            // lattitude
            double kmPerDegreeLat = 111.11;
            double lattitudeAdded = kmPerHour / kmPerDegreeLat;


            double newLat = 0;
            double newLng = 0;
            double lngPercentage = 0;
            double latPercentage = 0;
            if(0 < asimuth && asimuth <= 90) {
                lngPercentage = asimuth/90*100;
                latPercentage = 100 - lngPercentage;

                newLat = mMarker.getPosition().latitude + latPercentage * lattitudeAdded;
                newLng = mMarker.getPosition().longitude + lngPercentage * longitudeAdded;
            }
            else if(90 < asimuth && asimuth <= 180) {
                lngPercentage = (asimuth + 90) /180 * 100;
                latPercentage = 100 - lngPercentage;

                newLat = mMarker.getPosition().latitude - latPercentage * lattitudeAdded;
                newLng = mMarker.getPosition().longitude + lngPercentage * longitudeAdded;
            }
            else if(-179 <= asimuth && asimuth < -90) {
                lngPercentage = (asimuth + 180) /90 * 100;
                latPercentage = 100 - lngPercentage;

                newLat = mMarker.getPosition().latitude - latPercentage * lattitudeAdded;
                newLng = mMarker.getPosition().longitude - lngPercentage * longitudeAdded;
            }
            else if(-90 < asimuth && asimuth <= 0) {
                lngPercentage = (asimuth +90) /90 * 100;
                latPercentage = 100 - lngPercentage;

                newLat = mMarker.getPosition().latitude + latPercentage * lattitudeAdded;
                newLng = mMarker.getPosition().longitude - lngPercentage * longitudeAdded;
            }

            mThread.

            mMarker.setPosition(new LatLng(newLat, newLng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(newLat, newLng)).zoom(12).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
