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
import android.os.Handler;
import android.service.autofill.FieldClassification;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.dronique.Drone;
import com.example.dronique.R;
import com.example.dronique.Waypoint;
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

import java.util.Timer;

public class Tab2Fragment extends Fragment implements SensorEventListener {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Marker mMarker;
    private Drone mDrone;
    private Handler mHandler;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagneticField;

    private BitmapDrawable mBitmapDrawable =  null;
    private Bitmap mBitmap = null;
    private int mDefaultRotation = -43;

    float[] magneticVector = new float[3];
    float[] accelerometerVector = new float[3];
    float[] resultMatrix = new float[9];
    float[] values = new float[3];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_two, container, false);

        // Gestion du drone
        mDrone = new Drone();

        //Gestion de la bitmap
        mBitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.my_marker);
        mBitmap = Bitmap.createScaledBitmap(mBitmapDrawable.getBitmap(), 75, 75, false);

        // Création du handler
        mHandler = new Handler();

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
                mDrone.updatePosition(new Waypoint(46.1558, -1.1532, 0));
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

    private Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            int refresh = 200; // Taux de raffraichissement en ms

            // Récupération de la valeur asimuth
            SensorManager.getRotationMatrix(resultMatrix, null, accelerometerVector, magneticVector);
            float[] orientation = SensorManager.getOrientation(resultMatrix, values);
            int asimuth = (int)Math.toDegrees(values[0]);

            // On fait la rotation du marqueur
            float rotation = orientation[0];
            if(rotation >= 0){
                mMarker.setRotation(((rotation * 180) / 3 ) + mDefaultRotation);
            }
            if(rotation < 0){
                mMarker.setRotation((((rotation + 6) * 360) / 6 ) + mDefaultRotation);
            }

            // Variables
            double degreeePerKM = 111.11;
            double kmPerHour = 111.11;
            double speed = 0; // Vitesse du drone en ms
            double accelY = accelerometerVector[1];

            if(accelY <= 0)
                speed = kmPerHour / 3600000; // Vitesse maximum
            else
                speed = (kmPerHour - ((accelY * kmPerHour) / 10)) / 3600000;

            // Récupération du nombre de degré longitude parcourable
            double kmPerDegreeLng = degreeePerKM * (double)Math.cos((int) mMarker.getPosition().latitude);
            double longitudeAdded = speed / kmPerDegreeLng; // Nombre de degrès parcourable par ms
            longitudeAdded *= refresh; // On multipli pour chaque ms n'ont traité

            // Récupération du nombre de degré lattitude parcourable
            double kmPerDegreeLat = degreeePerKM;
            double lattitudeAdded = speed / kmPerDegreeLat; // Nombre de degrès parcourable par ms
            lattitudeAdded *= refresh; // On multipli pour chaque ms n'ont traité

            //
            double newLat = mDrone.getPosition().latitude;
            double newLng = mDrone.getPosition().longitude;
            double lngPercentage = 0;
            double latPercentage = 0;

            if(1 <= asimuth && asimuth <= 90) {
                // Pourcentage représentant l'inclinaison de la trajectoire
                lngPercentage = (double)asimuth / 90;
                latPercentage = 1 - lngPercentage;


                // Cas 1
                newLat = mDrone.getPosition().latitude  + (latPercentage * lattitudeAdded);
                newLng = mDrone.getPosition().longitude - (lngPercentage * longitudeAdded);
            }
            else if(91 <= asimuth && asimuth <= 180) {
                // Pourcentage représentant l'inclinaison de la trajectoire
                lngPercentage = (double)(asimuth-90) / 90;
                latPercentage = 1 - lngPercentage;

                // Cas 2
                newLat = mDrone.getPosition().latitude  - (latPercentage * lattitudeAdded);
                newLng = mDrone.getPosition().longitude - (lngPercentage * longitudeAdded);
            }
            else if(-180 <= asimuth && asimuth <= -90) {
                // Pourcentage représentant l'inclinaison de la trajectoire
                lngPercentage = (double)(asimuth + 180) / 90;
                latPercentage = 1 - lngPercentage;

                // Cas 3
                newLat = mDrone.getPosition().latitude  - (latPercentage * lattitudeAdded);
                newLng = mDrone.getPosition().longitude + (lngPercentage * longitudeAdded);
            }
            else if(-89 <= asimuth && asimuth <= 0) {
                // Pourcentage représentant l'inclinaison de la trajectoire
                lngPercentage = (double)(asimuth + 90) /90;
                latPercentage = 1 - lngPercentage;

                // Cas 4
                newLat = mDrone.getPosition().latitude  + (latPercentage * lattitudeAdded);
                newLng = mDrone.getPosition().longitude + (lngPercentage * longitudeAdded);
            }

            // Mise à jour de la postion du drone
            mDrone.updatePosition(new Waypoint(newLat, newLng, speed));
            mMarker.setPosition(mDrone.getPosition());

            // On relance la tâche
            mHandler.postDelayed(this, refresh);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        mHandler.post(updateUI);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mHandler.removeCallbacks(updateUI);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        mHandler.removeCallbacks(updateUI);
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

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
