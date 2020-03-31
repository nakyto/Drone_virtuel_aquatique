package com.example.dronique.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dronique.Client;
import com.example.dronique.Drone;
import com.example.dronique.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class Tab1Fragment extends Fragment {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Marker mMarker;
    private Client mClient = null;
    private Drone mDrone;
    private Bundle mSavedState;

    public Tab1Fragment(Drone drone)
    {
        mDrone = drone;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);

        // Création du drone
        mDrone = new Drone();

        // Gestion de la MapView
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

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
                mMarker = mGoogleMap.addMarker(new MarkerOptions().position(posLaRochelle).title("drone"));
            }
        });

        return view;
    }


     @Override
    public void onStart(){
        super.onStart();
        mMapView.onStart();
        mClient = new Client(mDrone, this);
        mClient.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        // Suppression du client
        mClient.cancel(true);

        // Sauvegarde du drone
        mSavedState = new Bundle();
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

    public void update() {
        if(mDrone != null) {
            LatLng pos = mDrone.getPosition();
            if(pos != null) {
                mMarker.setPosition(pos);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(pos).zoom(12).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }
}
