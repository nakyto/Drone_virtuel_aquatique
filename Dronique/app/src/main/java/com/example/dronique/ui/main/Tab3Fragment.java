package com.example.dronique.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dronique.Client;
import com.example.dronique.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class Tab3Fragment extends Fragment {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private SeekBar mSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_three, container, false);

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
                //création d'un waypoint après un click sur la carte
                mGoogleMap.setOnMapClickListener(
                        new GoogleMap.OnMapClickListener(){
                            @Override
                            public void onMapClick(LatLng pos){
                                mGoogleMap.addMarker(new MarkerOptions().position(pos));
                            }
                        }
                );
                //suppression d'un waypoint après un click sur celui-ci
                mGoogleMap.setOnMarkerClickListener(
                        new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                marker.remove();
                                return true;
                            }
                        }
                );
                //définition de la position de la caméra au lancement de la vue
                LatLng posLaRochelle = new LatLng(46.1558,-1.1532);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(posLaRochelle).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


        return view;
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
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
