package com.example.dronique.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.dronique.Client;
import com.example.dronique.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Fragment contenant la MapView et la GoogleMap
 */
public class MapFragment extends Fragment {

    private static int setcionNumber;
    private PageViewModel mPageViewModel;
    private GoogleMap mGoogleMap;
    private MapView mMapView;

    /**
     * @param index Identifiant de la vue demandé
     * @return      Le fragment correspondant à la vue demandé
     */
    public static MapFragment newInstance(int index) {
        MapFragment fragment = new MapFragment();
        Bundle bundle = new Bundle();
        setcionNumber = index;
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * @param savedInstanceState    Instance d'une vue à un moment donné
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        /*
        if (getArguments() != null) {
            index = sectionNumber;
        }
        */
        mPageViewModel.setIndex(index);
    }

    /**
     *
     * @param inflater              Permet de récupérer un layout
     * @param container             Un groupe de vue
     * @param savedInstanceState    Instance d'une vue à un moment donné
     * @return                      Retourne la vue d'un layout
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Recuperation de la mapview
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mMapView = (MapView) view.findViewById(R.id.map);

        // initialisation de la mapview
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ajout de la GoogleMap a notre mapview
        mMapView.getMapAsync(new OnMapReadyCallback(){

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                LatLng posLaRochelle = new LatLng(46.1558,-1.1532);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(posLaRochelle).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

        });

        if(setcionNumber == 1){
            System.out.println("****** je suis sur la page 1 ****");

        }
        else if(setcionNumber == 2) {
            System.out.println("****** je suis sur la page 2 ****");
        }
        else{
            System.out.println("****** je suis sur la page 3 ****");
        }

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
